package it.polimi.deib.dspace.actions;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import it.polimi.deib.dspace.control.Configuration;
import it.polimi.deib.dspace.control.DICEWrap;
import it.polimi.deib.dspace.ui.ConfigurationDialog;
import it.polimi.deib.dspace.ui.DSpaceWizard;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class DSpaceAction implements IWorkbenchWindowActionDelegate {
	/**
	 * The constructor.
	 */
	public DSpaceAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		System.out.println("Hola "+action.getId());
		if(action.getId().endsWith("Start")){
			WizardDialog dialog = new WizardDialog(null, new DSpaceWizard());
			dialog.open();
			System.out.println("Starting");
//			FileManager.getInstance().parseXmlFile();
//			try {
//				DICEWrap.getWrapper().genGSPN();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			DICEWrap.getWrapper().start();
//			DICEWrap.getWrapper().sendModel();
//			FileManager.getInstance().generateInputJson();
		}else{
			System.out.println("Set up options");
			ConfigurationDialog con=new ConfigurationDialog(new Shell());
			con.load();
			con.setView();
			
		}
	}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
	}
	private void loadConfiguration(){
		String filePath="configFile/ConfigFile.txt";
		String defaultId="http://specclient1.dei.polimi.it:8018/";
		File f = new File(filePath);
		if(!(f.exists() && !f.isDirectory())) { 
			try{
			    PrintWriter writer = new PrintWriter(filePath, "UTF-8");
			    Configuration.getCurrent().setServerID(defaultId);
			    writer.println(defaultId);
			    writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			BufferedReader br=null;
			try {
				br = new BufferedReader(new FileReader(filePath));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
			    StringBuilder sb = new StringBuilder();
			    String line = br.readLine();

			    while (line != null) {
			        sb.append(line);
			        sb.append(System.lineSeparator());
			        line = br.readLine();
			    }
			    String everything = sb.toString();
			    Configuration.getCurrent().setServerID(everything);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
			    try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}