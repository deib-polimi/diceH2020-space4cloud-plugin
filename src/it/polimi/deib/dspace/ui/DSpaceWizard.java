package it.polimi.deib.dspace.ui;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import it.polimi.deib.dspace.control.ClassDesc;
import it.polimi.deib.dspace.control.Configuration;

public class DSpaceWizard extends Wizard{
	private ChoicePage choice;
	private DICEPage dice;
	private ClassPage classp;
	private ClassPageF classpf;
	private FinalPage fpage;
	private int n = 0;
	private int classes;
	private ClassDesc c;
	private boolean finish = false;
	
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
		classpf = new ClassPageF("Class page", "Select page parameters and alternatives");
		fpage = new FinalPage("Goodbye", ".");
		addPage(choice);
		addPage(classp);
		addPage(classpf);
		addPage(fpage);
		//addPage(dice);
		//addPage(dice);
		//addPage(xxx);
		//addPage(yyy);
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage currentPage) {
		if (currentPage == dice){
			return choice;
		}
		if (currentPage == choice){
			classes = choice.getClasses();
			Configuration.getCurrent().setNumClasses(classes);
			Configuration.getCurrent().setPrivate(choice.getChoice());
			Configuration.getCurrent().setTechnology(choice.getTechnology());
			classp.setNumClasses(classes);
			return classp;
		}
		
		
		
		if(currentPage == classp){
			c = new ClassDesc(n++);
			System.out.println("N: "+n+" classes: "+classes);
			c.setDtsmPath(classp.getDTSMPath());
			c.setDdsmPath(classp.getDDSMPath());
			c.setAlternatives(classp.getSelectedAlternatives());
			Configuration.getCurrent().getClasses().add(c);
			Configuration.getCurrent().dump();
			if(n == classes){
				finish = true;
				return fpage;
			}
			classp.reset();
			return classp;
		}
//		
//		if(currentPage == fpage){
//			return null;
//		}
//		
		
		return null;
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
	}
	
	@Override
	public boolean canFinish() {
		if (finish == true)
			return true;
		return false;
	}
}
