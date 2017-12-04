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


import java.util.HashMap;
import javax.swing.JFileChooser;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

import it.polimi.diceH2020.plugin.control.PrivateConfiguration;
import it.polimi.diceH2020.plugin.control.VmClass;
import it.polimi.diceH2020.plugin.preferences.Preferences;
import utils.JsonDatabase;

/**
 * Allows user to set parameters for this class.
 * 
 * @author kom
 *
 */
public class ClassPage extends WizardPage {
	
	
	private Composite container;
	private GridLayout layout;
	
	private List availableAlternatives, chosenAlternatives;
	private Label fileName, errorLabel, mlNameFile, classesLabel;
	private Button mlProfile, button;
	
	private HashMap<String, String> altDtsm;						// MAP OF: VM | InputModel
	private String ddsmPath = "";
	private String mlPath = "";
	

	protected ClassPage(String title, String description) {
		super("Browse Files");
		setTitle(title);
		setDescription(description);
		altDtsm = new HashMap<String, String>();
	}

	@Override
	public void createControl(Composite parent) {
		
		/*
		 * Control
		 */
		
		container = new Composite(parent, SWT.NONE);
		layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 4;
		layout.verticalSpacing = 15;
		
		classesLabel = new Label(container, SWT.NONE);
		classesLabel.setText("");
		classesLabel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label l = new Label(container, SWT.NONE);
		l.setText("Choose a vm configuration");
		l.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		availableAlternatives = new List(container, SWT.BORDER);
		availableAlternatives.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Button add = new Button(container, SWT.PUSH);
		add.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		add.setText(">>");

		Button remove = new Button(container, SWT.PUSH);
		remove.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		remove.setText("<<");

		chosenAlternatives = new List(container, SWT.BORDER);
		GridData gdata = new GridData(SWT.BEGINNING, SWT.FILL, true, true);
		gdata.widthHint = 300;
		chosenAlternatives.setLayoutData(gdata);

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Button browse = new Button(container, SWT.PUSH);
		browse.setLayoutData(new GridData(SWT.BEGINNING, SWT.END, false, false));
		browse.setText("Load DDSM for this class...");

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		fileName = new Label(container, SWT.NONE);
		fileName.setLayoutData(new GridData(SWT.BEGINNING, SWT.END, false, false));

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		button = new Button(container, SWT.PUSH);
		button.setText("Refresh vm configurations");

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		errorLabel = new Label(container, SWT.NONE);
		errorLabel.setText("Error: Unable to get vm configurations from the webservice.\nPlease check for current configuration in Windows > Preferences > DICE > Optimization");
		errorLabel.setVisible(false);
		fileName.setLayoutData(new GridData(SWT.BEGINNING, SWT.END, false, false));

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		mlProfile = new Button(container, SWT.PUSH);
		mlProfile.setText("Choose machine learning profile");

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		mlNameFile = new Label(container, SWT.NONE);
		
		/*
		 * Listeners
		 */
		
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				refreshAlternatives();
			}
		});
		
		mlProfile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled(false); // JUST ONE UML FILE

				int choice = chooser.showOpenDialog(null);

				if (choice != JFileChooser.APPROVE_OPTION)
					return;

				mlPath = chooser.getSelectedFile().getPath();

				mlNameFile.setText(chooser.getSelectedFile().getName());
				container.layout();
				getWizard().getContainer().updateButtons();
			}
		});
		
		browse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled(false); 

				int choice = chooser.showOpenDialog(null);

				if (choice != JFileChooser.APPROVE_OPTION)
					return;

				ddsmPath = chooser.getSelectedFile().getPath();
				fileName.setText(chooser.getSelectedFile().getName());
				container.layout();
				getWizard().getContainer().updateButtons();
			}
		});
		
		add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				// Move alternative on the other side
				if (availableAlternatives.getSelectionCount() == 1) {
					final String selectedAlternative = availableAlternatives.getSelection()[0];
					final int selectedIdx = availableAlternatives.getSelectionIndices()[0];

					// Open file browser
					JFileChooser chooser = new JFileChooser();
					
					if (Preferences.getSimulator().equals(Preferences.DAG_SIM)){
						chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					    chooser.setAcceptAllFileFilterUsed(false);
					}
				    
					chooser.setMultiSelectionEnabled(false); 
																
					final int choice = chooser.showOpenDialog(null);
					if (choice == JFileChooser.APPROVE_OPTION) {
						altDtsm.put(selectedAlternative, chooser.getSelectedFile().getPath());													
						chosenAlternatives.add(selectedAlternative);
						availableAlternatives.remove(selectedIdx);
					}

					// Refresh page
					container.layout();
					getWizard().getContainer().updateButtons();
				}
			}
		});

		remove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (chosenAlternatives.getSelectionCount() < 1) {
					return;
				}
				availableAlternatives.add(chosenAlternatives.getSelection()[0]);
				altDtsm.remove(chosenAlternatives.getSelection()[0]);
				chosenAlternatives.remove(chosenAlternatives.getSelectionIndices()[0]);
				container.layout();
			}
		});
		
		populateAlternatives();
		setPageComplete(false);
		setControl(container);
	}
	
	@Override
	public boolean canFlipToNextPage() {
		if (!ddsmPath.equals("") && chosenAlternatives.getItemCount() > 0) 
			return true;
		else
			return false;
	}

	private void populateAlternatives() {
		String[] vmConfigs = JsonDatabase.getInstance().getVmConfigs();
		if (vmConfigs == null) {
			errorLabel.setVisible(true);
		} else {
			errorLabel.setVisible(false);
			availableAlternatives.setItems(JsonDatabase.getInstance().getVmConfigs());
		}
	}

	private void refreshAlternatives() {
		String[] vmConfigs = JsonDatabase.getInstance().refreshDbContents();
		if (vmConfigs == null) {
			errorLabel.setVisible(true);
		} else {
			errorLabel.setVisible(false);
			availableAlternatives.setItems(JsonDatabase.getInstance().getVmConfigs());
		}
	}	

	public HashMap<String, String> getAltDtsm() {
		return altDtsm;
	}

	public void reset() {
		chosenAlternatives.removeAll();
		populateAlternatives();
		fileName.setText("");
		mlNameFile.setText("");
		getWizard().getContainer().updateButtons();
		container.layout();
		altDtsm = new HashMap<String, String>();
		mlPath = "";
		ddsmPath = "";
	}

	public String[] getSelectedAlternatives() {
		return chosenAlternatives.getItems();
	}

	public String getMlPath() {
		return mlPath;
	}
	
	public String getDDSMPath(){
		return ddsmPath;
	}
	
	public void setClasses(int current, int total){
		classesLabel.setText(String.format("Current class: %d / %d", current, total));
	}

	public void privateCase() {
		button.setVisible(false);
		availableAlternatives.removeAll();
		for (VmClass vm : PrivateConfiguration.getCurrent().getVmList()) {
			availableAlternatives.add(vm.getName());
		}
	}
}
