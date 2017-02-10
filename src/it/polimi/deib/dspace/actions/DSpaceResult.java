package it.polimi.deib.dspace.actions;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import it.polimi.deib.dspace.control.JSonReader;
import it.polimi.deib.dspace.ui.ResultPage;


public class DSpaceResult implements IWorkbenchWindowActionDelegate{
	private ResultPage p;
	@Override
	public void run(IAction arg0) {
		
		p=new ResultPage(new Shell());
		p.displayUrl("http://specclient1.dei.polimi.it:8018/resPub");
		Map<String,String> idClassUmlFile=new HashMap<String,String>();
		idClassUmlFile.put("1", "/home/arlind/Downloads/Wikistats-Models/Wikistats-Models/wikistats.uml");
		JSonReader reader=new JSonReader(idClassUmlFile);
		reader.read("/home/arlind/Downloads/5550669387137824906/1_h8_D500000.0.json");
		reader.write();
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
	}
}
