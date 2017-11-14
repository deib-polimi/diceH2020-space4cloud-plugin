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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Group;
import javax.swing.JButton;
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
	private Button existingLTC, notExistingLTC;
	private Button admissionControl, notAdmissionControl;
	private int classes = 0;
	private int alternatives;
	private Text t1, h1, h2, h3;
	private List t2;
	private Label l1;
	private Label l2;
	private GridData g1, g3, g5, g6, f1, f2, f3;
	private Text reservedInstancesText, spotRatioText;
	private Composite ltcCompositeText;
	private Label errSR;

	// Scenario
	private CloudType cloudType;
	private boolean hasLTC;
	private boolean hasAdmissionControl; // TODO Create the radio button for
											// this

	protected InitialPage(String title, String description) {
		super("Choose service type");
		setTitle(title);
		setDescription(description);
	}

	@Override
	public void createControl(Composite parent) {
		
		/*
		 * Create Control
		 */
		container = new Composite(parent, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		container.setLayout(layout);
		
		l1 = new Label(container, SWT.NONE);
		l1.setText("Number of classes:");

		l2 = new Label(container, SWT.NONE);
		l2.setText("Select technology:");

		t1 = new Text(container, SWT.BORDER);
		t1.setEditable(true);

		t2 = new List(container, SWT.BORDER);
		t2.add("Storm");
		t2.add("Spark");
		t2.add("Hadoop/MapReduce");

		Composite cloudTypeComposite = new Composite(container, SWT.NONE);
		RowLayout cloudTypeRow = new RowLayout(SWT.VERTICAL);
		cloudTypeRow.spacing = 10;
		cloudTypeComposite.setLayout(cloudTypeRow);

		Label label = new Label(cloudTypeComposite, SWT.NONE);
		label.setText("Cloud Type: ");

		privateBtn = new Button(cloudTypeComposite, SWT.RADIO);
		privateBtn.setVisible(true);
		privateBtn.setText("Private");

		publicBtn = new Button(cloudTypeComposite, SWT.RADIO);
		publicBtn.setVisible(true);
		publicBtn.setText("Public");
		publicBtn.setSelection(true);

		Composite optionsComposite = new Composite(container, SWT.NONE);
		GridLayout optionsGrid = new GridLayout();
		optionsGrid.numColumns = 2;
		optionsGrid.verticalSpacing = 10;
		optionsComposite.setLayout(optionsGrid);

		// Column 1 - LTC
		Composite ltcComposite = new Composite(optionsComposite, SWT.NONE);
		RowLayout ltcRow = new RowLayout(SWT.VERTICAL);
		ltcRow.spacing = 10;
		ltcComposite.setLayout(ltcRow);

		existingLTC = new Button(ltcComposite, SWT.RADIO);
		existingLTC.setText("Long Term Contract");

		notExistingLTC = new Button(ltcComposite, SWT.RADIO);
		notExistingLTC.setText("No Long Term Contract");
		notExistingLTC.setSelection(true);

		// Column 2 - LTC Options
		Composite ltcOptionsComposite = new Composite(optionsComposite, SWT.NONE);
		GridLayout ltcOptionsGrid = new GridLayout();
		ltcOptionsGrid.numColumns = 2;
		ltcOptionsGrid.makeColumnsEqualWidth = true;
		ltcOptionsGrid.verticalSpacing = 10;
		ltcOptionsGrid.marginTop = 40;
		ltcOptionsGrid.marginRight = 40;
		ltcOptionsComposite.setLayout(ltcOptionsGrid);

		Label ltcOptionsLabel = new Label(ltcOptionsComposite, SWT.NONE);
		ltcOptionsLabel.setText("Reserved \ninstances:");

		reservedInstancesText = new Text(ltcOptionsComposite, SWT.BORDER);
		reservedInstancesText.setEditable(true);
		reservedInstancesText.setToolTipText("Reserved instances in LTC");

		Label tTextLabel = new Label(ltcOptionsComposite, SWT.NONE);
		tTextLabel.setText("Spot ratio");
		spotRatioText = new Text(ltcOptionsComposite, SWT.BORDER);
		spotRatioText.setEditable(true);
		spotRatioText.setToolTipText("Ratio between spot instances with \nrespect to demand and reserved VMs");

		// Column 1 - Admission Control
		Composite admissionComposite = new Composite(optionsComposite, SWT.NONE);
		RowLayout admissionRow = new RowLayout(SWT.VERTICAL);
		admissionRow.spacing = 10;
		admissionComposite.setLayout(admissionRow);

		admissionControl = new Button(admissionComposite, SWT.RADIO);
		admissionControl.setText("Admission Control");

		notAdmissionControl = new Button(admissionComposite, SWT.RADIO);
		notAdmissionControl.setText("No Admission Control");

		// RowLayout ltcOptionsRow = new RowLayout(SWT.VERTICAL);
		// ltcOptionsComposite.setLayout(ltcOptionsRow);
		//

		// Label tTextLabel = new Label(ltcOptionsComposite, SWT.NONE);
		// tTextLabel.setText("Spot ratio");
		// spotRatioText = new Text(ltcOptionsComposite, SWT.BORDER);
		// spotRatioText.setEditable(true);
		// if (!Premium.isPremium()) {
		// tTextLabel.setVisible(false);
		// spotRatioText.setVisible(false);
		// }

		// Composite err = new Composite(ltcCompositeText, SWT.NONE);
		// RowLayout errRow_3 = new RowLayout();
		// errRow_3.type = SWT.HORIZONTAL;
		// errRow_3.pack = false;
		// err.setLayout(errRow_3);
		//
		// errSR = new Label(err, SWT.BORDER);
		//
		// errSR.setText("Not a valid spot ratio");
		// errSR.setVisible(false);

		/*
		 *  Listeners
		 */

		t1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getWizard().getContainer().updateButtons();
			}
		});

		t2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				// Error Message
				if (true)
					setErrorMessage("You must enter only consonants");
				else
					setErrorMessage(null);
			    
				if (getTechnology() == Technology.STORM) {
					privateBtn.setEnabled(false);
				} else {
					privateBtn.setEnabled(true);
				}

				if (getTechnology() == Technology.SPARK) {
					t1.setText("1");
					t1.setEnabled(false);
				} else {
					t1.setEnabled(true);
					t1.setEditable(true);
				}
				getWizard().getContainer().updateButtons();
			}
		});

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

		notExistingLTC.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ltcCompositeText.setVisible(false);
				getWizard().getContainer().updateButtons();
				hasLTC = false;
			}
		});

		// reservedInstancesText.addModifyListener(new ModifyListener() {
		//
		// @Override
		// public void modifyText(ModifyEvent arg0) {
		// getWizard().getContainer().updateButtons();
		// }
		// });
		//
		// if (Premium.isPremium()) {
		// spotRatioText.addModifyListener(new ModifyListener() {
		//
		// @Override
		// public void modifyText(ModifyEvent arg0) {
		// try {
		// if (Float.parseFloat(spotRatioText.getText()) <= 1) {
		// errSR.setVisible(false);
		// } else {
		// errSR.setVisible(true);
		// }
		// } catch (NumberFormatException e) {
		//
		// }
		//
		// getWizard().getContainer().updateButtons();
		// }
		// });
		// }

		setControl(container);
		setPageComplete(false);
	}

	private boolean areLtcFieldsValid() {
		return true;
	}
	// private boolean areLtcFieldsValid() {
	// if (ltcCompositeText.getVisible()) {
	// if (reservedInstancesText.getText().equals("")) {
	// return false;
	// }
	// try {
	// if (Premium.isPremium()
	// && (spotRatioText.getText().equals("") ||
	// Float.parseFloat(spotRatioText.getText()) > 1)) {
	// return false;
	// }
	// } catch (NumberFormatException e) {
	// return false;
	// }
	// return true;
	// }
	// return true;
	// }
	//
	// public int getReservedIstances() {
	// return Integer.parseInt(reservedInstancesText.getText());
	// }
	//
	// public float getSpotRatio() {
	// if (spotRatioText.getText().isEmpty()) {
	// return 0;
	// }
	// else {
	// return Float.parseFloat(spotRatioText.getText());
	// }
	// }

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

	public CloudType getCloudType() {
		return cloudType;
	}

	public boolean hasLTC() {
		return hasLTC;
	}

	public boolean hasAdmissionControl() {
		return hasAdmissionControl;
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
