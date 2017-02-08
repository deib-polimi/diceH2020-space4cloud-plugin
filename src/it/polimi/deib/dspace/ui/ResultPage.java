package it.polimi.deib.dspace.ui;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ResultPage extends WizardPage{

	
	private EmbeddedBrowser browser;
	private Composite container;


	public ResultPage(String name) {
		super(name);
		this.setTitle(name);
	
	}
	
	@Override
	public void createControl(Composite arg0) {
		container=new Composite(arg0, SWT.NONE);
		setPageComplete(true);
		getWizard().getContainer().updateButtons();
		setControl(container);
	}
	
	public void displayUrl(String URL){
		browser=new EmbeddedBrowser(URL);
		browser.launch(container);
		getShell().setSize(800, 800);
		
	}
	

}
