/*
Copyright 2017 Arlind Rufi
Copyright 2017 Gianmario Pozzi
Copyright 2017 Giorgio Pea

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package it.polimi.diceH2020.plugin.ui;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import it.polimi.diceH2020.plugin.control.ClassDesc;
import it.polimi.diceH2020.plugin.control.Configuration;
import it.polimi.diceH2020.plugin.control.FileHandler;
import it.polimi.diceH2020.plugin.control.PrivateConfiguration;

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
	private SparkDataPage spPage;
	private SelectFolderPage folPage;
	private StormDataPage stPage;
	private PrivateConfigPage prConfigPage;
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
		stPage=new StormDataPage("Set Storm parameter");
		spPage=new SparkDataPage("Set Spark parameter");
		prConfigPage=new PrivateConfigPage("Set cluster parameters");

		addPage(choice);
		addPage(prConfigPage);
		addPage(folPage);
		addPage(stPage);
		addPage(hPage);
		addPage(spPage);
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

			if(Configuration.getCurrent().getIsPrivate()){
				return prConfigPage;
			}

			classp.udpate();
			classp.setNumClasses(classes);
			return classp;
		}

		if(currentPage==hPage){
			c.setHadoopParUD(hPage.getHadoopParUD());

			if(n == classes){
				finish = true;
				Configuration.getCurrent().dump();
				return fpage;
			}

			classp.reset();
			hPage.reset();

			if(Configuration.getCurrent().getIsPrivate())
				classp.privateCase();

			return classp;
		}
		
		if(currentPage==spPage){		
			c.setHadoopParUD(spPage.getHadoopParUD());		
			if(n == classes){		
				finish = true;		
				Configuration.getCurrent().dump();		
				return fpage;		
			}		
			classp.reset();		
			spPage.reset();		
			if(Configuration.getCurrent().getIsPrivate())		
				classp.privateCase();		
			return classp;		
		}

		if(currentPage==stPage){
			c.setStormU(stPage.getStormU());

			if(n == classes){
				finish = true;
				Configuration.getCurrent().dump();
				return fpage;
			}

			classp.reset();
			stPage.reset();

			if(Configuration.getCurrent().getIsPrivate())
				classp.privateCase();

			return classp;
		}

		if(currentPage == classp){
			c = new ClassDesc(++n);
			System.out.println("N: "+n+" classes: "+classes);
			c.setDdsmPath(classp.getDDSMPath());
			c.setAltDtsm(classp.getAltDtsm());

			Configuration.getCurrent().getClasses().add(c);

			if(Configuration.getCurrent().getTechnology().contains("Hadoop Map-reduce")){
				c.setMlPath(classp.getMlPath());
				return hPage;
			} else if (Configuration.getCurrent().getTechnology().contains("Spark")){
				c.setMlPath(classp.getMlPath());
				return spPage;
			}else{
				return stPage;
			}
		}

		if(currentPage==this.folPage){
			fileHandler.setFolder(folPage.getSelectedFolder());
			fileHandler.setScenario(false,false);
			fileHandler.sendFile();
			finish = true;
			return fpage;
		}

		if(currentPage==this.prConfigPage){
			PrivateConfiguration.getCurrent().setPriE(prConfigPage.getCostNode());
			PrivateConfiguration.getCurrent().setPriM(prConfigPage.getMemForNode());
			PrivateConfiguration.getCurrent().setPriN(prConfigPage.getNumNodes());
			PrivateConfiguration.getCurrent().setPriV(prConfigPage.getCpuNode());
			classp.privateCase();
			classp.udpate();
			return classp;
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
