package it.polimi.deib.dspace.ui;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import it.polimi.deib.dspace.control.ClassDesc;
import it.polimi.deib.dspace.control.Configuration;
import it.polimi.deib.dspace.control.FileHandler;

/**
 * Class needed by Eclipse to manage wizards. The core of this class is getNextPage() method.
 * @author kom
 *
 */

public class DSpaceWizard extends Wizard{
	private FileHandler fileHandler;
	private ChoicePage choice;
	private ClassPage classp;
	private FinalPage fpage;
	private HadoopDataPage hPage;
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
		folPage=new SelectFolderPage("Select folder");
		hPage=new HadoopDataPage("Set Hadoop parameters");
		addPage(choice);
		addPage(folPage);
		addPage(hPage);
		addPage(classp);
		addPage(fpage);
		
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage currentPage) {
		if (currentPage == choice){
			classes = choice.getClasses();
			Configuration.getCurrent().setNumClasses(classes);
			if (Configuration.getCurrent().getHasLtc()){
				Configuration.getCurrent().setR(choice.getR());
				Configuration.getCurrent().setSpsr(choice.getSpsr());
			}
			
				classp.udpate();
				classp.setNumClasses(classes);
				return classp;
			
			
		}
		if(currentPage==hPage){
			
			c.setHadoopPar(hPage.getHadoopParUD());
			if(n == classes){
				finish = true;
				Configuration.getCurrent().dump();
				return fpage;
			}
			classp.reset();
			hPage.reset();
			return classp;
			
		}
		
		
		
		if(currentPage == classp){
			c = new ClassDesc(++n);
			System.out.println("N: "+n+" classes: "+classes);
			c.setDdsmPath(classp.getDDSMPath());
			c.setAltDtsm(classp.getAltDtsm());
			Configuration.getCurrent().getClasses().add(c);
			return hPage;
			
		}
		if(currentPage==this.folPage){
			
			fileHandler.setFolder(folPage.getSelectedFolder());
			fileHandler.setScenario(false,false);
			fileHandler.sendFile();
			finish = true;
			return fpage;
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
