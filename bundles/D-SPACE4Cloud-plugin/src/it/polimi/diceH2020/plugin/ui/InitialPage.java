/*
Copyright 2017 Arlind Rufi
Copyright 2017 Gianmario Pozzi
Copyright 2017 Giorgio Pea

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package it.polimi.diceH2020.plugin.ui;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Group;
import it.polimi.diceH2020.SPACE4Cloud.shared.settings.CloudType;
import it.polimi.diceH2020.SPACE4Cloud.shared.settings.Technology;


import it.polimi.diceH2020.plugin.control.Configuration;
import it.polimi.diceH2020.plugin.net.NetworkManager;
import utils.Premium;

/**
 * Initial page. The user can: -choose among private/public solution -select
 * computation technology -tune some general parameters (e.g. number of classes)
 * 
 * @author kom
 *
 */
public class InitialPage extends WizardPage {
	private Composite container;
	private GridLayout layout;
	private Button privateBtn, publicBtn;
	private int classes = 0;
	private int alternatives;
	private Text t1, h1, h2, h3;
	private List t2;
	private Label l1;
	private Label l2;
	private GridData g1, g3, g5, g6, f1, f2, f3;
	private Button existingLTC, nExistingLTC;
	private Text reservedInstancesText, spotRatioText;
	private Composite ltcCompositeText;
	private Label errSR;
	
	// Scenario
	private CloudType cloudType;
	private boolean hasLTC;
	private boolean hasAdmissionControl;     // TODO Create the radio button for this 

	protected InitialPage(String title, String description) {
		super("Choose service type");
		setTitle(title);
		setDescription(description);
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		container.setLayout(layout);
		        

		g1 = new GridData();
		g1.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		g1.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;

		l1 = new Label(container, SWT.NONE);
		l1.setText("Number of classes:");
		

		new GridData(SWT.CENTER, SWT.BEGINNING, true, true);
		l2 = new Label(container, SWT.NONE);
		l2.setText("Select technology:");

		g3 = new GridData();
		g3.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		g3.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;

		t1 = new Text(container, SWT.BORDER);
		t1.setEditable(true);

		t1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getWizard().getContainer().updateButtons();
			}
		});

		new GridData(SWT.CENTER, SWT.BEGINNING, true, true);
		t2 = new List(container, SWT.BORDER);
		t2.add("Storm");
		t2.add("Spark");
		t2.add("Hadoop/MapReduce");
		
		t2.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {

				if (getTechnology() == Technology.STORM) {
					privateBtn.setEnabled(false);
				} 
				else {
					privateBtn.setEnabled(true);
				}
				
				if (getTechnology() == Technology.SPARK){
					t1.setText("1");
					t1.setEnabled(false);
				} 
				else {
					t1.setEnabled(true);
					t1.setEditable(true);
				}
				getWizard().getContainer().updateButtons();
			}
		});

		f1 = new GridData();
		f1.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		f1.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;

		h1 = new Text(container, SWT.PUSH);
		h1.setVisible(false);

		f2 = new GridData();
		f2.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		f2.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;

		h2 = new Text(container, SWT.BORDER);
		h2.setVisible(false);

		g5 = new GridData();
		g5.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		
//		Group cloudTypeGroup = new Group(container, SWT.NONE);
//		cloudTypeGroup.setLayout(new RowLayout(SWT.VERTICAL));
//		
//		 
//		Label label = new Label(cloudTypeGroup, SWT.NONE);
//        label.setText("Cloud Type: ");
//        
//		Button buttonMale = new Button(cloudTypeGroup, SWT.RADIO);
//		buttonMale.setText("Public");
//		 
//		Button buttonFemale = new Button(cloudTypeGroup, SWT.RADIO);
//		buttonFemale.setText("Private");
		
		
		privateBtn = new Button(container, SWT.RADIO);
		privateBtn.setVisible(true);
		privateBtn.setText("Private");

		f3 = new GridData();
		f3.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		f3.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;

		h3 = new Text(container, SWT.BORDER);
		h3.setVisible(false);

		g6 = new GridData();
		g6.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		publicBtn = new Button(container, SWT.RADIO);
		publicBtn.setVisible(true);
		publicBtn.setText("Public");

		Composite ltcUberComposite = new Composite(container, SWT.NONE);
		RowLayout layoutRow_uber = new RowLayout();
		layoutRow_uber.fill = true;
		ltcUberComposite.setLayout(layoutRow_uber);

		// Composite for ltc radio btns
		final Composite ltcComposite = new Composite(ltcUberComposite, SWT.NONE);

		RowLayout layoutRow = new RowLayout();
		layoutRow.type = SWT.VERTICAL;
		ltcComposite.setLayout(layoutRow);
		ltcComposite.setVisible(false);

		// Composite for ltc text inputs
		ltcCompositeText = new Composite(ltcUberComposite, SWT.NONE);

		RowLayout layoutRow_1 = new RowLayout();
		layoutRow_1.type = SWT.VERTICAL;
		ltcCompositeText.setLayout(layoutRow_1);
		ltcCompositeText.setVisible(false);

		Composite rTextComposite = new Composite(ltcCompositeText, SWT.NONE);
		RowLayout layoutRow_4 = new RowLayout();
		layoutRow_4.type = SWT.HORIZONTAL;
		layoutRow_4.pack = false;
		rTextComposite.setLayout(layoutRow_4);

		Label rTextLabel = new Label(rTextComposite, SWT.NONE);
		rTextLabel.setText("Reserved instances: ");

		reservedInstancesText = new Text(rTextComposite, SWT.BORDER);
		reservedInstancesText.setEditable(true);

		Composite tTextComposite = new Composite(ltcCompositeText, SWT.NONE);
		RowLayout layoutRow_3 = new RowLayout();
		layoutRow_3.type = SWT.HORIZONTAL;
		layoutRow_3.pack = false;
		tTextComposite.setLayout(layoutRow_3);

		Label tTextLabel = new Label(tTextComposite, SWT.NONE);
		tTextLabel.setText("Spot ratio");
		spotRatioText = new Text(tTextComposite, SWT.BORDER);
		spotRatioText.setEditable(true);
		if (!Premium.isPremium()) {
			tTextLabel.setVisible(false);
			spotRatioText.setVisible(false);
		}

		Composite err = new Composite(ltcCompositeText, SWT.NONE);
		RowLayout errRow_3 = new RowLayout();
		errRow_3.type = SWT.HORIZONTAL;
		errRow_3.pack = false;
		err.setLayout(errRow_3);

		errSR = new Label(err, SWT.BORDER);

		errSR.setText("Not a valid spot ratio");
		errSR.setVisible(false);

		existingLTC = new Button(ltcComposite, SWT.RADIO);
		existingLTC.setText("Existing Long Term Contract");

		nExistingLTC = new Button(ltcComposite, SWT.RADIO);
		nExistingLTC.setText("No Long Term Contract");

		privateBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cloudType = CloudType.PRIVATE;
				getWizard().getContainer().updateButtons();
				ltcCompositeText.setVisible(false);
				ltcComposite.setVisible(false);
				
			}
		});

		publicBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cloudType = CloudType.PUBLIC;
				getWizard().getContainer().updateButtons();
				ltcComposite.setVisible(true);
				if (existingLTC.getSelection()) {
					ltcCompositeText.setVisible(true);
				}
			}
		});

		existingLTC.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ltcCompositeText.setVisible(true);
				getWizard().getContainer().updateButtons();
				hasLTC = true;
			}
		});

		reservedInstancesText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				getWizard().getContainer().updateButtons();
			}
		});

		if (Premium.isPremium()) {
			spotRatioText.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent arg0) {
					try {
						if (Float.parseFloat(spotRatioText.getText()) <= 1) {
							errSR.setVisible(false);
						} else {
							errSR.setVisible(true);
						}
					} catch (NumberFormatException e) {

					}

					getWizard().getContainer().updateButtons();
				}
			});
		}

		nExistingLTC.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ltcCompositeText.setVisible(false);
				getWizard().getContainer().updateButtons();
				hasLTC = false;
			}
		});

		setControl(container);
		setPageComplete(false);
	}

	private boolean areLtcFieldsValid() {
		if (ltcCompositeText.getVisible()) {
			if (reservedInstancesText.getText().equals("")) {
				return false;
			}
			try {
				if (Premium.isPremium()
						&& (spotRatioText.getText().equals("") || Float.parseFloat(spotRatioText.getText()) > 1)) {
					return false;
				}
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		}
		return true;
	}

	public int getClasses() {
		classes = Integer.parseInt(t1.getText());
		return classes;
	}
	
	public int getAlternatives() {
		return alternatives;
	}
	
	
	public Technology getTechnology() {
		String selection = t2.getSelection()[0];
		
		if (selection.equals("Storm"))
			return Technology.STORM;
		
		else if (selection.equals("Spark"))
			return Technology.SPARK;
		
		else 
			return Technology.HADOOP;
		
	}

	
	public CloudType getCloudType(){
		return cloudType;
	}
	
	public boolean hasLTC(){
		return hasLTC;
	}
	
	public boolean hasAdmissionControl(){
		return hasAdmissionControl;
	}
	
	public int getReservedIstances() {
		return Integer.parseInt(reservedInstancesText.getText());
	}

	public float getSpotRatio() {
		if (spotRatioText.getText().isEmpty()) {
			return 0;
		} else {
			return Float.parseFloat(spotRatioText.getText());
		}

	}

	@Override
	public boolean canFlipToNextPage() {
		if (areLtcFieldsValid() && t2.getSelectionCount() > 0 && (privateBtn.getSelection() || publicBtn.getSelection())
				&& getClasses() != 0) {
			if (getTechnology() == Technology.STORM && privateBtn.getSelection()) {
				return false;
			}
			return true;
		}
		return false;
	}

}
