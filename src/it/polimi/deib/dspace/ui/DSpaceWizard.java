package it.polimi.deib.dspace.ui;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import it.polimi.deib.dspace.control.ClassDesc;
import it.polimi.deib.dspace.control.Configuration;


public class DSpaceWizard extends Wizard{
	private ChoicePage choice;
	private ClassPage classp;
	private FinalPage fpage;
	private ResultPage result;
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
		choice = new ChoicePage("Service type","Choose service type");
		classp = new ClassPage("Class page", "Select page parameters and alternatives");
		fpage = new FinalPage("Goodbye", ".");
		result=new ResultPage("Result");
		
		addPage(choice);
		addPage(classp);
		addPage(fpage);
		addPage(result);
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage currentPage) {
		if (currentPage == choice){
			//TODO just for testing delete after 
			FileHandler hd=new FileHandler();
			hd.setFolder("/home/arlind/Downloads/31_h8_D500000.0");
			hd.setInitialMarking("smth");
			hd.setScenario("PublicPeakWorkload");
			hd.sendFile();
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
				finish = false;
				return fpage;
			}
			classp.reset();
			return classp;
		}
		if(currentPage==fpage){
			if(n == classes){
				finish = true;
				result.displayUrl("www.google.com");
				return result;
			}
		}
		
		return null;
	}
	
	@Override
	public boolean canFinish() {
		if (finish == true)
			return true;
		return false;
	}
}
