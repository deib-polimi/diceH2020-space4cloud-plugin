package it.polimi.deib.dspace.actions;


import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import it.polimi.deib.dspace.control.DICEWrap;
import it.polimi.deib.dspace.ui.DSpaceWizard;
import it.polimi.deib.dspace.ui.EmbeddedBrowser;
import it.polimi.deib.dspace.ui.ResultPage;


public class DSpaceResult implements IWorkbenchWindowActionDelegate{
	private EmbeddedBrowser brows;
	private IWorkbenchWindow window;
	private ResultPage p;
	@Override
	public void run(IAction arg0) {
		brows=new EmbeddedBrowser("www.google.com");
		brows.launch(
				window.getWorkbench().getDisplay().getActiveShell());
	//	p.displayUrl("www.google.com");
		System.out.println("here");
		
	
	}

	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IWorkbenchWindow arg0) {
		this.window=arg0;
		ResultPage p=new ResultPage("smsafpo");
		
	}
	public static Shell getShell() {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (window == null) {
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench()
					.getWorkbenchWindows();
			if (windows.length > 0) {
				return windows[0].getShell();
			}
		} else {
			return window.getShell();
		}
		return null;
	}
	
	

}
