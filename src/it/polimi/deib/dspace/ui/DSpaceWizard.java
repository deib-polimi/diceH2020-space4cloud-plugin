package it.polimi.deib.dspace.ui;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import it.polimi.deib.dspace.control.ClassDesc;
import it.polimi.deib.dspace.control.Configuration;


public class DSpaceWizard extends Wizard{
	private FileHandler fileHandler;
	private ChoicePage choice;
	private ClassPage classp;
	private FinalPage fpage;
	private ResultPage result;
	private SelectFolderPage folPage;
	private int n = 0;
	private int classes;
	private ClassDesc c;
	private boolean finish = false;
	
	public DSpaceWizard() {
        super();
        setNeedsProgressMonitor(true);
        this.fileHandler=new FileHandler();
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
		folPage=new SelectFolderPage("Select folder");
		addPage(choice);
		addPage(result);
		addPage(folPage);
		addPage(classp);
		addPage(fpage);
		
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage currentPage) {
		if (currentPage == choice){
			classes = choice.getClasses();
			Configuration.getCurrent().setNumClasses(classes);
			Configuration.getCurrent().setPrivate(choice.getChoice());
			Configuration.getCurrent().setTechnology(choice.getTechnology());
			classp.setNumClasses(classes);
			return classp;
		}
		
		if(currentPage == classp){
			c = new ClassDesc(++n);
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
		if(currentPage==this.folPage){
			
			fileHandler.setFolder(folPage.getSelectedFolder());
			fileHandler.setInitialMarking("smth");
			//TODO get from configuration
			fileHandler.setScenario(false,true);
			fileHandler.sendFile();
			finish = true;
			result.displayUrl("http://specclient1.dei.polimi.it:8018/resPub");
			return result;
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
