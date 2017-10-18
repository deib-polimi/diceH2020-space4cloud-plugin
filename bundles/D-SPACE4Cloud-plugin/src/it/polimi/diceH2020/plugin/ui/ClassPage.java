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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import it.polimi.diceH2020.plugin.control.Configuration;
import it.polimi.diceH2020.plugin.control.PrivateConfiguration;
import it.polimi.diceH2020.plugin.control.VmClass;
import it.polimi.diceH2020.plugin.preferences.Preferences;
import utils.JsonDatabase;
import utils.Premium;

/**
 * Allows user to set parameters for this class.
 * 
 * @author kom
 *
 */
public class ClassPage extends WizardPage {
	private static final String DEFAULT_ML_PROFILE_PATH = Preferences.getSavingDir() + "ml_model.txt";
	private Composite container;
	private GridLayout layout;
	private List availableAlternatives;
	private List chosenAlternatives;
	private String ddsmPath = "";
	private Label fileName, errorLabel;
	private HashMap<String, String> altDtsm;
	private Button mlProfile;
	private String mlPath = "";
	private Button button;
	private Label mlNameFile;
	protected static String thinkTime; 

	protected ClassPage(String title, String description) {
		super("Browse Files");
		setTitle(title);
		setDescription(description);
		altDtsm = new HashMap<String, String>();
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 4;

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

		add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// Move alternative on the other side
				if (availableAlternatives.getSelectionCount() == 1) {
					final String selectedAlternative = availableAlternatives.getSelection()[0];
					final int selectedIdx = availableAlternatives.getSelectionIndices()[0];

					// Open file browser
					JFileChooser chooser = new JFileChooser();
					// JUST ONE UML FILE
					chooser.setMultiSelectionEnabled(false); 
																
					final int choice = chooser.showOpenDialog(null);
					if (choice == JFileChooser.APPROVE_OPTION) {
						altDtsm.put(selectedAlternative, chooser.getSelectedFile().getPath());
					
						if (Configuration.isSpark() || Configuration.isHadoop())
							thinkTime = getThinkTimeFromModel(chooser.getSelectedFile());
						
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
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				refreshAlternatives();
			}
		});

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

		mlProfile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled(false); // JUST ONE UML FILE

				int choice = chooser.showOpenDialog(null);

				if (choice != JFileChooser.APPROVE_OPTION)
					return;

				mlPath = chooser.getSelectedFile().getPath();

				mlNameFile.setText(chooser.getSelectedFile().getName());
				// setPageComplete(true);
				container.layout();
				getWizard().getContainer().updateButtons();
			}
		});
		
		if(!Premium.isPremium()){
			mlProfile.setVisible(false);
		}

		browse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled(false); 

				int choice = chooser.showOpenDialog(null);

				if (choice != JFileChooser.APPROVE_OPTION)
					return;

				ddsmPath = chooser.getSelectedFile().getPath();
				fileName.setText(chooser.getSelectedFile().getName());
				// setPageComplete(true);
				container.layout();
				getWizard().getContainer().updateButtons();
			}
		});

		populateAlternatives();
		setPageComplete(false);
		setControl(container);
	}
	
	@Override
	public boolean canFlipToNextPage() {
		if (Configuration.getCurrent().getTechnology().contains("Hadoop/MapReduce")
			|| Configuration.getCurrent().getTechnology().contains("Spark")) {
			if (!ddsmPath.equals("") && chosenAlternatives.getItemCount() > 0 && !mlPath.equals("")) {
				return true;
			} else {
				return false;
			}
		}
		if (!ddsmPath.equals("") && chosenAlternatives.getItemCount() > 0) {
			return true;
		}
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
	
	private static String getThinkTimeFromModel(File inputModel){
		String think;
		try {
			
			Path path = Paths.get(inputModel.getAbsolutePath());
	        String content = new String(Files.readAllBytes(path));
	        Pattern pattern = null;
        
	        if (Configuration.isHadoop())    		
	        	pattern = Pattern.compile("hadoopExtDelay=\\[([0-9]+)\\]");
	        
	        if (Configuration.isSpark())
	        	pattern = Pattern.compile("sparkExtDelay=\"\\(expr=([0-9]+),");
	        
	        
	        Matcher matcher = pattern.matcher(content);
	        if (matcher.find()) {
	            think = matcher.group(1);
	            System.out.println("Found Think Time: " + think);
	            return think;
	        }
	        else {
	        	// TODO Throw exception wrong model
	        	System.out.println("Cannot found think time in file: " + inputModel.getName());
	        	return "";
	        }
        
		}
	    catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
        
		return "";
	}

	public String getDDSMPath() {
		return ddsmPath;
	}

	public HashMap<String, String> getAltDtsm() {
		return altDtsm;
	}

	public void reset() {
		chosenAlternatives.removeAll();
		populateAlternatives();
		fileName.setText("");
		ddsmPath = "";
		getWizard().getContainer().updateButtons();
		container.layout();
		altDtsm = new HashMap<String, String>();
		mlPath = "";
	}

	public String[] getSelectedAlternatives() {
		return chosenAlternatives.getItems();
	}

	public void setNumClasses(int numClasses) {
	}

	public void udpate() {
		if (Configuration.isSpark() || Configuration.isHadoop()) {
			if (Premium.isPremium()) {
				mlProfile.setVisible(true);
			}
		} else {
			mlProfile.setVisible(false);
		}
	}

	public String getMlPath() {
		if(mlPath.isEmpty()){
			return DEFAULT_ML_PROFILE_PATH;
		}
		return mlPath;
	}

	public void privateCase() {
		button.setVisible(false);
		availableAlternatives.removeAll();
		for (VmClass vm : PrivateConfiguration.getCurrent().getVmList()) {
			availableAlternatives.add(vm.getName());
		}
	}
}
