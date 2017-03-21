package it.polimi.diceH2020.plugin.ui;

import java.io.File;

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



public class SelectFolderPage extends WizardPage{
	private Composite container;
	private GridLayout layout;
	private Label folder;
	private boolean canFlip;
	private String selectedFolder;
	private Button browse;

	protected SelectFolderPage(String pageName) {
		super(pageName);
		canFlip=false;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createControl(Composite arg0) {
		container = new Composite(arg0, SWT.NONE);
		layout = new GridLayout();
		container.setLayout(layout);
		browse = new Button(container, SWT.PUSH);
		browse.setLayoutData(new GridData(SWT.BEGINNING, SWT.END, false, false));
		browse.setText("Select Folder which contains the data...");
		folder = new Label(container, SWT.NONE);
		folder.setLayoutData(new GridData(SWT.BEGINNING, SWT.END, false, false));
		
		browse.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {


            	JFileChooser j = new JFileChooser();
            	j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            	int choice = j.showOpenDialog(null);
            	
            	if (choice!= JFileChooser.APPROVE_OPTION) return;
            	selectedFolder=j.getSelectedFile().getAbsolutePath();
            	checkFolder();
            	container.layout();
            	getWizard().getContainer().updateButtons();
            }
        });
		setPageComplete(false);
		this.setControl(container);
	}
	
	@Override
	public boolean canFlipToNextPage(){
		return this.canFlip;
	}
	public String getSelectedFolder(){
		return this.selectedFolder;
	}
	public void checkFolder(){
		File fold=new File(this.selectedFolder);
		for(File f:fold.listFiles()){
			if(f.getName().contains(".json")){
				this.folder.setText(this.selectedFolder);
				this.canFlip=true;
				this.browse.setText("Select another folder...");
				return;
			}
		}
		folder.setText("The chosen folder does not contain a json file");
		this.browse.setText("Select another folder...");
		
	}

}
