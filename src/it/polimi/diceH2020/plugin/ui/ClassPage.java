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

import it.polimi.diceH2020.plugin.control.Configuration;
import it.polimi.diceH2020.plugin.control.PrivateConfiguration;
import it.polimi.diceH2020.plugin.control.VmClass;
import utils.JsonDatabase;

/**
 * Allows user to set parameters for this class.
 * @author kom
 *
 */
public class ClassPage extends WizardPage{
	private Composite container;
	private GridLayout layout;
	private List l1;
	private List l2;
	private String ddsmPath = "";
	private Label fileName, label_error;
	private HashMap<String, String> altDtsm;
	private Button mlProfile ;
	private String mlPath="";
	private Button button;
	private Label mlNameFile;

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

		l1 = new List(container, SWT.BORDER);
		l1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Button add = new Button(container, SWT.PUSH);
		add.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		add.setText(">>");

		Button remove = new Button(container, SWT.PUSH);
		remove.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		remove.setText("<<");

		l2 = new List(container, SWT.BORDER);
		GridData gdata = new GridData(SWT.BEGINNING, SWT.FILL, true,true);
		gdata.widthHint = 300;
		l2.setLayoutData(gdata);

		add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//Move alternative on the other side
				if(l1.getSelectionCount() < 1){
					return;
				}
				l2.add(l1.getSelection()[0]);

				container.layout();

				//Open file browser
				JFileChooser chooser= new JFileChooser();
				chooser.setMultiSelectionEnabled(false); //JUST ONE UML FILE

				int choice = chooser.showOpenDialog(null);
				if (choice != JFileChooser.APPROVE_OPTION) return;
				altDtsm.put(l1.getSelection()[0], chooser.getSelectedFile().getPath());

				l1.remove(l1.getSelectionIndices()[0]);

				//Refresh page
				container.layout();
				getWizard().getContainer().updateButtons();
			}
		});

		remove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(l2.getSelectionCount() < 1){
					return;
				}
				l1.add(l2.getSelection()[0]);
				altDtsm.remove(l2.getSelection()[0]);
				l2.remove(l2.getSelectionIndices()[0]);
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
		button.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				refreshAlternatives();
			}
		});

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		label_error = new Label(container, SWT.NONE);
		label_error.setText("Error: Unable to get vm configurations from the webservice");
		label_error.setVisible(false);
		fileName.setLayoutData(new GridData(SWT.BEGINNING, SWT.END, false, false));	

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		mlProfile = new Button(container, SWT.PUSH);
		mlProfile.setText("Choose machine learning profile");

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		mlNameFile=new Label(container,SWT.NONE);

		mlProfile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				JFileChooser chooser= new JFileChooser();
				chooser.setMultiSelectionEnabled(false); //JUST ONE UML FILE

				int choice = chooser.showOpenDialog(null);

				if (choice != JFileChooser.APPROVE_OPTION) return;

				mlPath = chooser.getSelectedFile().getPath();

				mlNameFile.setText(chooser.getSelectedFile().getName());
				//setPageComplete(true);
				container.layout();
				getWizard().getContainer().updateButtons();
			}
		});

		browse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				JFileChooser chooser= new JFileChooser();
				chooser.setMultiSelectionEnabled(false); //JUST ONE UML FILE

				int choice = chooser.showOpenDialog(null);

				if (choice != JFileChooser.APPROVE_OPTION) return;

				ddsmPath = chooser.getSelectedFile().getPath();

				fileName.setText(chooser.getSelectedFile().getName());
				//setPageComplete(true);
				container.layout();
				getWizard().getContainer().updateButtons();
			}
		});

		populateAlternatives();

		setPageComplete(false);
		setControl(container);
	}

	@Override
	public boolean canFlipToNextPage(){
		if(Configuration.getCurrent().getTechnology().contains("Hadoop")){
			if(!ddsmPath.equals("") && l2.getItemCount() > 0&&!mlPath.equals("")){
				return true;
			}else{
				return false;
			}
		}
		if(!ddsmPath.equals("") && l2.getItemCount() > 0){
			return true;
		}
		return false;
	}

	private void populateAlternatives(){
		String[] vmConfigs = JsonDatabase.getInstance().getVmConfigs();
		if(vmConfigs == null){
			label_error.setVisible(true);
		}
		else{
			l1.setItems(JsonDatabase.getInstance().getVmConfigs());
		}
	}

	private void refreshAlternatives(){
		String[] vmConfigs = JsonDatabase.getInstance().refreshDbContents();
		if(vmConfigs == null){
			label_error.setVisible(true);
		}
		else{
			l1.setItems(JsonDatabase.getInstance().getVmConfigs());
		}
	}

	public String getDDSMPath(){
		return ddsmPath;
	}

	public HashMap<String, String> getAltDtsm(){
		return altDtsm;
	}

	public void reset(){
		l2.removeAll();
		populateAlternatives();
		fileName.setText("");
		ddsmPath = "";
		getWizard().getContainer().updateButtons();
		container.layout();
		altDtsm = new HashMap<String, String>();
		mlPath="";
	}

	public String[] getSelectedAlternatives() {
		return l2.getItems();
	}

	public void setNumClasses(int numClasses){
	}

	public void udpate(){
		if(Configuration.getCurrent().getTechnology().contains("Hadoop")){
			mlProfile.setVisible(true);
		}else{
			mlProfile.setVisible(false);
		}
	}

	public String getMlPath(){
		return mlPath;
	}

	public void privateCase(){
		button.setVisible(false);
		l1.removeAll();
		for(VmClass vm:PrivateConfiguration.getCurrent().getVmList()){
			l1.add(vm.getName());
		}
	}
}
