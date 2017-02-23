package it.polimi.deib.dspace.actions;


import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import it.polimi.deib.dspace.control.Configuration;
import it.polimi.deib.dspace.control.DICEWrap;
import it.polimi.deib.dspace.control.FileManager;
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
			Configuration.getCurrent().reset();
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
			FileManager.getInstance().generateInputJson();
		}else{
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
	
	
}