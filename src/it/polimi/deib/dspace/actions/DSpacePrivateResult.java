package it.polimi.deib.dspace.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import it.polimi.deib.dspace.ui.ResultPage;

public class DSpacePrivateResult implements IWorkbenchWindowActionDelegate{
	private ResultPage p;
	@Override
	public void run(IAction arg0) {
		p=new ResultPage(new Shell());
		p.displayUrl("localhost:8000/resPri");
		
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
		// TODO Auto-generated method stub
		
	}

}
