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
import it.polimi.diceH2020.SPACE4Cloud.shared.settings.CloudType;
import it.polimi.diceH2020.SPACE4Cloud.shared.settings.Technology;
import it.polimi.diceH2020.plugin.preferences.Preferences;
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
	private GridLayout layout;
	private Button privateBtn, publicBtn, spotPricingBtn, admissionControlBtn;
	private Composite spotComposite, spotRatioComposite, optionsComposite, cloudTypeComposite, admissionComposite;
	private Text classesText, spotRatioText;
	private Label classesLabel, technologyLabel;
	private List technologyList;

	// Scenario
	private int classes = -1;
	private float spotRatio = -1;
	private CloudType cloudType = CloudType.PUBLIC;
	private Technology technology;
	private boolean admissionControl = false; 
	private boolean spotPricing = false;

	
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
		layout.horizontalSpacing = 177;
		layout.makeColumnsEqualWidth = true;
		container.setLayout(layout);
		
		classesLabel = new Label(container, SWT.NONE);
		classesLabel.setText("Number of classes:");

		technologyLabel = new Label(container, SWT.NONE);
		technologyLabel.setText("Select technology:");

		classesText = new Text(container, SWT.BORDER);
		classesText.setEditable(true);

		technologyList = new List(container, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.widthHint = 200;
		gridData.heightHint = 75;
		technologyList.setLayoutData(gridData);
		technologyList.add("Spark");
		
		if (Preferences.simulatorIsJMT()){
			// technologyList.add("Storm");
			technologyList.add("Hadoop/MapReduce");
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
		
		
		publicBtn = new Button(cloudTypeComposite, SWT.RADIO);
		publicBtn.setVisible(true);
		publicBtn.setText("Public");
		publicBtn.setSelection(true);

		
		optionsComposite = new Composite(container, SWT.NONE);
		GridLayout optionsGrid = new GridLayout();
		optionsGrid.numColumns = 1;
		optionsGrid.verticalSpacing = 20;
		optionsGrid.marginTop = 30;
		optionsComposite.setLayout(optionsGrid);

		
		spotComposite = new Composite(optionsComposite, SWT.NONE);
		RowLayout ltcRow = new RowLayout(SWT.VERTICAL);
		spotComposite.setLayout(ltcRow);
		spotPricingBtn = new Button(spotComposite, SWT.CHECK);
		spotPricingBtn.setText("Spot Pricing");
		
	
		spotRatioComposite = new Composite(spotComposite, SWT.NONE);
		RowLayout spotRatioRow = new RowLayout(SWT.HORIZONTAL);
		spotRatioRow.spacing = 20;
		spotRatioRow.marginTop = 30;
		spotRatioComposite.setLayout(spotRatioRow);
		spotRatioComposite.setVisible(false);
		
		Label spotRatioLabel = new Label(spotRatioComposite, SWT.NONE);
		spotRatioLabel.setText("Spot ratio");
		spotRatioText = new Text(spotRatioComposite, SWT.BORDER);
		spotRatioText.setEditable(true);
		spotRatioText.setToolTipText("Ratio between spot instances with respect \nto total number of VMs in the cluster");
		
		
		admissionComposite = new Composite(optionsComposite, SWT.NONE);
		RowLayout admissionRow = new RowLayout(SWT.VERTICAL);
		admissionComposite.setLayout(admissionRow);
		admissionComposite.setVisible(false);
		admissionControlBtn = new Button(admissionComposite, SWT.CHECK);
		admissionControlBtn.setText("Admission Control");
		
		
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
						classes = value;
						setErrorMessage(null);
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
					disablePrivateCase();
					classesText.setEnabled(true);
				}
				
				if (selection.equals("Spark")){
					technology = Technology.SPARK;
					privateBtn.setEnabled(true);
					
					if ((Preferences.simulatorIsJMT() || Preferences.simulatorIsGSPN()) && 
							(Preferences.simulatorIsDAGSIM() && cloudType == CloudType.PUBLIC)){
						
					classes = 1;
					classesText.setText("1");
					classesText.setEnabled(false);
					}
				}
				
				if (selection.contains("Hadoop")){
					technology = Technology.HADOOP;
					classesText.setEnabled(true);
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

		privateBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				spotComposite.setVisible(false);
				spotRatioComposite.setVisible(false);
				if (Preferences.simulatorIsJMT() && technology.equals(Technology.SPARK)) {
					admissionComposite.setVisible(false);
				} else {
					admissionComposite.setVisible(true);
				}
				
				cloudType = CloudType.PRIVATE;
				spotPricing = false;
				
				if (Preferences.simulatorIsDAGSIM() && cloudType == CloudType.PUBLIC){
					classesText.setEnabled(true);
				}
				
				resetPublicCase();			
				getWizard().getContainer().updateButtons();

			}
		});

		publicBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				spotComposite.setVisible(true);
				spotPricingBtn.setSelection(false);
				spotRatioComposite.setVisible(false);
				admissionComposite.setVisible(false);
				
				cloudType = CloudType.PUBLIC;
				admissionControl = false;
				admissionControlBtn.setSelection(false);
				
				getWizard().getContainer().updateButtons();
			}
		});
		
		spotPricingBtn.addSelectionListener(new SelectionAdapter()
		{
		    @Override
		    public void widgetSelected(SelectionEvent e)
		    {
		    	spotPricing = spotPricingBtn.getSelection();
		    	spotRatioComposite.setVisible(spotPricingBtn.getSelection());
		    	
		    	if (!spotPricing) {
		    		spotRatioText.setText("");
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
					 if (ratio >= 0 && ratio <= 1.0 ){
						 spotRatio = ratio;
						 setErrorMessage(null);
					 }
					 else {
						 setErrorMessage("Spot Ratio must be a ratio. Please enter a value between 0 and 1");
						 spotRatioText.setFocus();
						 spotRatio = -1;
						 
					 }
				 }
				 catch (NumberFormatException e) {
					 setErrorMessage("Spot Ratio must be a ratio. Please enter a value between 0 and 1");
					 spotRatio = -1;
					 spotRatioText.setFocus();
				 }
				 getWizard().getContainer().updateButtons();
			 }
		});
		
		admissionControlBtn.addSelectionListener(new SelectionAdapter()
		{
		    @Override
		    public void widgetSelected(SelectionEvent e)
		    {
		    	admissionControl = admissionControlBtn.getSelection();
				getWizard().getContainer().updateButtons();
		    }
		});
		
		setControl(container);
		setPageComplete(false);
	}

	
	private void resetPublicCase() {
		spotPricingBtn.setSelection(false);
		spotPricing = false; 
		spotRatioText.setText("");
		spotRatio = -1;
		spotRatioComposite.setVisible(false);
		setErrorMessage(null);
	}
	
	private void disablePrivateCase() {
		privateBtn.setSelection(false);
		privateBtn.setEnabled(false);
		publicBtn.setSelection(true);		
		admissionComposite.setVisible(false);
		admissionControlBtn.setSelection(false);
		admissionControl = false;
		spotComposite.setVisible(true);
		resetPublicCase();
		
		return;
	}
	@Override
	public boolean canFlipToNextPage() {
		
		if (spotPricing && (spotRatio > 1 || spotRatio < 0))
			return false;
			
		if (technology != null && classes > 0 && (privateBtn.getSelection() || publicBtn.getSelection()))
			return true;

		else
			return false;
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

	public CloudType getCloudType() {
		return cloudType;
	}

	public boolean getSpotPricing() {
		return spotPricing;
	}

	public boolean getAdmissionControl() {
		return admissionControl;
	}

	public float getSpotRatio() {
		return spotRatio;
	}

	
}
