package it.polimi.deib.dspace.startup;

import org.eclipse.ui.IStartup;

import it.polimi.deib.dspace.control.ResultCheck;

public class StartUpThread implements IStartup{
	
	ResultCheck check;
	@Override
	public void earlyStartup() {
		System.out.println("starting");
		ResultCheck.unZipIt("/home/arlind/Desktop/Untitled Folder/Untitled Folder.zip", "/home/arlind/Desktop/Untitled Folder");
	}

}
