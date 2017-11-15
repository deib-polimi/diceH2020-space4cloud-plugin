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
import it.polimi.diceH2020.plugin.preferences.Preferences;
import utils.Premium;

/**
 * Initial page. The user can: -choose among private/public solution -select
 * computation technology -tune some general parameters (e.g. number of classes)
 * 
 * @author kom
 *
 */
public class InitialPage extends WizardPage {
	
	// Layout
	private Composite container;
	private Composite ltcComposite, optionsComposite, cloudTypeComposite, ltcOptionsComposite, admissionComposite;
	private GridLayout layout;
	private Button privateBtn, publicBtn;
	private Button existingLTC, notExistingLTC;
	private Button admissionControl, notAdmissionControl;
	private Text classesText, reservedInstancesText, spotRatioText;
	private List technologyList;
	private Label l1, l2;

	// Scenario
	private int classes = -1;
	private int alternatives;
	private CloudType cloudType = CloudType.PUBLIC;
	private Technology technology = null;
	private boolean hasAdmissionControl = false; 
	private boolean hasLTC = false;
	private float spotRatio = -1;
	private int reservedInstances = -1;
	
	protected InitialPage(String title, String description) {
		super("Choose service type");
		setTitle(title);
		setDescription(description);
	}

	public int getClasses() {
		return classes;
	}

	public void setClasses(int classes) {
		this.classes = classes;
	}

	public Technology getTechnology() {
		return technology;
	}

	public int getAlternatives() {
		return alternatives;
	}

	public void setAlternatives(int alternatives) {
		this.alternatives = alternatives;
	}

	public CloudType getCloudType() {
		return cloudType;
	}

	public boolean hasAdmissionControl() {
		return hasAdmissionControl;
	}

	public boolean hasLTC() {
		return hasLTC;
	}

	public float getSpotRatio() {
		return spotRatio;
	}

	public int getReservedInstances() {
		return reservedInstances;
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

		classesText = new Text(container, SWT.BORDER);
		classesText.setEditable(true);

		technologyList = new List(container, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.widthHint = 200;
		gridData.heightHint = 75;
		technologyList.setLayoutData(gridData);
		technologyList.add("Spark");
		
		if (Preferences.simulatorIsJMT()){
			technologyList.add("Storm");
		}
		
		if (Preferences.simulatorIsGSPN()){
			technologyList.add("Storm");
			technologyList.add("Hadoop/MapReduce");
		}
		
		cloudTypeComposite = new Composite(container, SWT.NONE);
		RowLayout cloudTypeRow = new RowLayout(SWT.VERTICAL);
		cloudTypeRow.spacing = 10;
		cloudTypeComposite.setLayout(cloudTypeRow);

		Label simulatorLabel = new Label(cloudTypeComposite, SWT.NONE);
		simulatorLabel.setText("Simulation Engine:   " + Preferences.getSimulator());
		simulatorLabel.setToolTipText("You can select another simulator \nin the Preference Page");
		
		Text padding = new Text(cloudTypeComposite, SWT.NONE);
		padding.setVisible(false);
		
		Label cloudLabel = new Label(cloudTypeComposite, SWT.NONE);
		cloudLabel.setText("Cloud Type: ");

		privateBtn = new Button(cloudTypeComposite, SWT.RADIO);
		privateBtn.setVisible(true);
		privateBtn.setText("Private");
		
		if (Preferences.simulatorIsDAGSIM())
			privateBtn.setEnabled(false);

		publicBtn = new Button(cloudTypeComposite, SWT.RADIO);
		publicBtn.setVisible(true);
		publicBtn.setText("Public");
		publicBtn.setSelection(true);

		optionsComposite = new Composite(container, SWT.NONE);
		GridLayout optionsGrid = new GridLayout();
		optionsGrid.numColumns = 2;
		optionsGrid.verticalSpacing = 10;
		optionsComposite.setLayout(optionsGrid);

		// Column 1 - LTC
		ltcComposite = new Composite(optionsComposite, SWT.NONE);
		RowLayout ltcRow = new RowLayout(SWT.VERTICAL);
		ltcRow.spacing = 10;
		ltcComposite.setLayout(ltcRow);

		existingLTC = new Button(ltcComposite, SWT.RADIO);
		existingLTC.setText("Long Term Contract");

		notExistingLTC = new Button(ltcComposite, SWT.RADIO);
		notExistingLTC.setText("No Long Term Contract");
		notExistingLTC.setSelection(true);

		// Column 2 - LTC Options
		ltcOptionsComposite = new Composite(optionsComposite, SWT.NONE);
		GridLayout ltcOptionsGrid = new GridLayout();
		ltcOptionsGrid.numColumns = 2;
		ltcOptionsGrid.makeColumnsEqualWidth = true;
		ltcOptionsGrid.verticalSpacing = 10;
		ltcOptionsGrid.marginTop = 35;
		ltcOptionsGrid.marginLeft = 55;
		ltcOptionsComposite.setLayout(ltcOptionsGrid);
		ltcOptionsComposite.setVisible(false);

		Label ltcOptionsLabel = new Label(ltcOptionsComposite, SWT.NONE);
		ltcOptionsLabel.setText("Reserved \ninstances:");

		reservedInstancesText = new Text(ltcOptionsComposite, SWT.BORDER);
		reservedInstancesText.setEditable(true);
		reservedInstancesText.setToolTipText("Reserved instances in LTC");

		Label tTextLabel = new Label(ltcOptionsComposite, SWT.NONE);
		tTextLabel.setText("Spot ratio");
		spotRatioText = new Text(ltcOptionsComposite, SWT.BORDER);
		spotRatioText.setEditable(true);
		spotRatioText.setToolTipText("Ratio between spot instances with respect \nto total number of VMs in the cluster");

		// Column 1 - Admission Control
		admissionComposite = new Composite(optionsComposite, SWT.NONE);
		RowLayout admissionRow = new RowLayout(SWT.VERTICAL);
		admissionRow.spacing = 10;
		admissionComposite.setLayout(admissionRow);
		admissionComposite.setVisible(false);

		admissionControl = new Button(admissionComposite, SWT.RADIO);
		admissionControl.setText("Admission Control");

		notAdmissionControl = new Button(admissionComposite, SWT.RADIO);
		notAdmissionControl.setText("No Admission Control");
		notAdmissionControl.setSelection(true);

		/*
		 *  Listeners
		 */
		
		classesText.addModifyListener(new ModifyListener() {
			
			 @Override
			 public void modifyText(ModifyEvent arg0) {
				 try {
					 int value = Integer.parseInt(classesText.getText());
					 if (value <= 0){
						 setErrorMessage("Classes must be greater than 0");
						 classes = -1;
						 classesText.setFocus();
					 }
					 else{
						 setErrorMessage(null);	
						 classes = value;
					 }
						 
				 }
				 catch (NumberFormatException e) {
					 setErrorMessage("Classes must be an integer");
					 classes = -1;
					 classesText.setFocus();
				 }
				 getWizard().getContainer().updateButtons();
			 }
		});
		
		classesText.addListener (SWT.Verify, e -> {
			String string = e.text;
			char [] chars = new char [string.length ()];
			string.getChars (0, chars.length, chars, 0);
			for (int i=0; i < chars.length; i++) {
				if (!('0' <= chars [i] && chars [i] <= '9')) {
					e.doit = false;
					return;
				}
			}
		});

		technologyList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			    
				String selection = technologyList.getSelection()[0];
				
				if (selection.equals("Storm")){
					
					technology = Technology.STORM;
					cloudType = CloudType.PUBLIC;
					hasAdmissionControl = false;
					disablePrivateCase();
				}
				
				if (selection.equals("Spark")){
					technology = Technology.SPARK;
					if (Preferences.simulatorIsJMT() || Preferences.simulatorIsGSPN()){
						privateBtn.setEnabled(true);
						classesText.setText("1");
						classesText.setEnabled(false);
						classes = 1;
					}
				} else {
					disablePrivateCase();
					classesText.setEnabled(true);
					classesText.setEditable(true);
				}
				
				if (selection.contains("Hadoop")){
					technology = Technology.HADOOP;
					if (Preferences.simulatorIsGSPN()){
						privateBtn.setEnabled(true);
					}
					else {
						disablePrivateCase();
					}
				}
				
				getWizard().getContainer().updateButtons();
			}
		});

		// Cloud Type Listeners 
		privateBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				ltcComposite.setVisible(false);
				ltcOptionsComposite.setVisible(false);
				admissionComposite.setVisible(true);
				
				hasLTC = false;
				cloudType = CloudType.PRIVATE;
				getWizard().getContainer().updateButtons();

			}
		});

		publicBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				ltcComposite.setVisible(true);
				admissionComposite.setVisible(false);
				
				hasAdmissionControl = false;
				cloudType = CloudType.PUBLIC;
				getWizard().getContainer().updateButtons();
			}
		});

		// LTC Listeners
		existingLTC.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				hasLTC = true;
				if (publicBtn.getSelection()) {
					ltcOptionsComposite.setVisible(true);					
				}
				else 
					ltcOptionsComposite.setVisible(false);
				
				getWizard().getContainer().updateButtons();
			}
		});

		notExistingLTC.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ltcOptionsComposite.setVisible(false);	
				hasLTC = false;
				getWizard().getContainer().updateButtons();				
			}
		});
		
		// LTC Options Listeners
		reservedInstancesText.addListener (SWT.Verify, e -> {
			String string = e.text;
			char [] chars = new char [string.length ()];
			string.getChars (0, chars.length, chars, 0);
			for (int i=0; i<chars.length; i++) {
				if (!('0' <= chars [i] && chars [i] <= '9')) {
					e.doit = false;
					return;
				}
			}
		});
		
		reservedInstancesText.addModifyListener(new ModifyListener() {
			
			 @Override
			 public void modifyText(ModifyEvent arg0) {
				 try {
					 int instances = Integer.parseInt(reservedInstancesText.getText());
					 if (instances < 0) {
						 setErrorMessage("Instances should be a positive value");
						 reservedInstances = -1;
						 reservedInstancesText.setFocus();
						 
					 }
					 else
						reservedInstances = instances;
					 setErrorMessage(null);	
				 }
				 catch (NumberFormatException e) {
					 setErrorMessage("Instances should be a positive integer");
					 reservedInstancesText.setFocus();
				 }
				 getWizard().getContainer().updateButtons();
			 }
		});
		
		spotRatioText.addListener (SWT.Verify, e -> {
			String string = e.text;
			char [] chars = new char [string.length ()];
			string.getChars (0, chars.length, chars, 0);
			for (int i=0; i<chars.length; i++) {
				if (!(('0' <= chars [i] && chars [i] <= '9') || chars[i] == '.')) {
					e.doit = false;
					return;
				}
			}
		});

		spotRatioText.addModifyListener(new ModifyListener() {
			
			 @Override
			 public void modifyText(ModifyEvent arg0) {
				 try {
					 float ratio = Float.parseFloat(spotRatioText.getText());
					 if (ratio > 1 || ratio < 0) {
						 setErrorMessage("Spot Ratio must be a ratio. Please enter a value between 0 and 1");
						 spotRatioText.setFocus();
						 spotRatio = -1;
					 }
					 else
						spotRatio = ratio;
						setErrorMessage(null);	
				 }
				 catch (NumberFormatException e) {
					 setErrorMessage("Spot Ratio must be a ratio. Please enter a value between 0 and 1");
					 spotRatio = -1;
					 spotRatioText.setFocus();
				 }
				 getWizard().getContainer().updateButtons();
			 }
		});
		
		// Admission Control Listeners
		admissionControl.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				hasAdmissionControl = true;
				getWizard().getContainer().updateButtons();
			}
		});

		notAdmissionControl.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				hasAdmissionControl = false;
				setErrorMessage(null);
				getWizard().getContainer().updateButtons();
			}
		});
		
		setControl(container);
		setPageComplete(false);
	}

	private void disablePrivateCase() {
		privateBtn.setSelection(false);
		privateBtn.setEnabled(false);
		publicBtn.setSelection(true);
		ltcComposite.setVisible(true);
		admissionComposite.setVisible(false);
		admissionControl.setSelection(false);
		notAdmissionControl.setSelection(true);
		hasAdmissionControl = false;
		setErrorMessage(null);
		return;
	}
	@Override
	public boolean canFlipToNextPage() {
		if (technology != null && classes > 0 && (privateBtn.getSelection() || publicBtn.getSelection())){
			
			if (cloudType == CloudType.PRIVATE){
				if (hasAdmissionControl)
					if (getClasses() > 1)
						return true;
					else {
						setErrorMessage("When using Admission Control the number classes should be greater than 1");
						return false;
					}
				return true;		// PRIVATE + NO ADMISSION CONTROL
			}
			else {
				// CloudType.PUBLIC
				if (hasLTC){
					if (spotRatio >= 0 && spotRatio <= 1 && reservedInstances > 0)
						return true;
				}
				else 
					return true;	// PUBLIC + NO LTC			
			}
		}
		return false;
	}
}
