package it.polimi.deib.dspace.ui;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import it.polimi.deib.dspace.control.ClassDesc;
import it.polimi.deib.dspace.control.Configuration;

public class DSpaceWizard extends Wizard{
	ChoicePage choice;
	DICEPage dice;
	ClassPage classp;
	
	public DSpaceWizard() {
        super();
        setNeedsProgressMonitor(true);
	}

	@Override
	public boolean performFinish() {
		System.out.println("FINISH");
		return true;
	}
	
	@Override
	public void addPages(){
		dice = new DICEPage("Select UML model", "Select which UML file you want to build PNML on.");
		choice = new ChoicePage("Service type","Choose service type");
		//xxx = new XXXPage("Step 1","Load .xxx files");
		//yyy = new YYYPage("Step 2","Load .yyy files");
		classp = new ClassPage("Class page", "Select page parameters and alternatives");
		addPage(choice);
		addPage(classp);
		//addPage(dice);
		//addPage(dice);
		//addPage(xxx);
		//addPage(yyy);
	}
	
	private int n = 0;
	private ClassDesc c;
	@Override
	public IWizardPage getNextPage(IWizardPage currentPage) {
		if (currentPage == dice){
			return choice;
		}
		if (currentPage == choice){
			Configuration.getCurrent().setNumClasses(choice.getClasses());
			Configuration.getCurrent().setPrivate(choice.getChoice());
			Configuration.getCurrent().setTechnology(choice.getTechnology());
			return classp;
		}
		
		if(currentPage == classp && n < choice.getClasses()){
			c = new ClassDesc(n++);
			c.setUmlPath(classp.getUMLPath());
			c.setAlternatives(classp.getSelectedAlternatives());
			Configuration.getCurrent().getClasses().add(c);
			Configuration.getCurrent().dump();
			classp.reset();
			return classp;
		}
		
		System.out.println("FINISH!!");
//		if (currentPage == zero ){
//	        if (zero.getChoice()){
//	        	System.out.println("Returning xxx");
//	        	return xxx;
//	        }else{
//	        	System.out.println("Returning yyy - a");
//	        	return yyy;
//        	}
//	    }
//		if (currentPage == xxx){
//			System.out.println("Returning yyy - b");
//			return yyy;
//		}
//		if (currentPage == zero){
//			return xxx;
//		}
	    return null;
	}
}
