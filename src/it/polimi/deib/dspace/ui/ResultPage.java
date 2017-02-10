package it.polimi.deib.dspace.ui;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;

import it.polimi.deib.dspace.control.EmbeddedBrowser;

public class ResultPage extends Dialog{
	
	private EmbeddedBrowser browser;
	private Shell shell;

	public ResultPage(Shell name) {
		super(name);
		shell=name;
	}
	
	public void displayUrl(String URL){
		browser=new EmbeddedBrowser(URL);
		browser.launch(shell);
		shell.setSize(800, 800);
		shell.open();	
	}
}
