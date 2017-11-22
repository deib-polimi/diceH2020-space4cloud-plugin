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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import it.polimi.diceH2020.plugin.control.ClassDesc;
import it.polimi.diceH2020.plugin.control.Configuration;
import it.polimi.diceH2020.plugin.control.FileHandler;
import it.polimi.diceH2020.plugin.control.PrivateConfiguration;
import it.polimi.diceH2020.plugin.preferences.Preferences;
import it.polimi.diceH2020.SPACE4Cloud.shared.settings.*;

/**
 * Class needed by Eclipse to manage wizards. The core of this class is
 * getNextPage() method.
 * 
 * @author kom
 *
 */
public class DSpaceWizard extends Wizard {
	private FileHandler fileHandler;
	
	private InitialPage initialPage;
	private ClassPage classPage;
	private PrivateConfigPage privateConfigPage;
	private HadoopDataPage hadoopDataPage;
	private SparkDataPage sparkDataPage;
	private StormDataPage stormDataPage;
	private FinalPage finalPage;
	
	private int numClasses;			
	private int currentClass = 1;
	private ClassDesc classDescription;
	
	private boolean wizardCompleted = false;

	public DSpaceWizard() {
		super();
		setNeedsProgressMonitor(true);
		this.fileHandler = new FileHandler();
	}

	@Override
	public boolean performFinish() {
		System.out.println("FINISH");
		return true;
	}

	@Override
	public void addPages() {
		initialPage = new InitialPage("Service type", "Choose service type");
		classPage = new ClassPage("Class page", "Select page parameters and alternatives");
		finalPage = new FinalPage("Finish", ".");
		hadoopDataPage = new HadoopDataPage("Set Hadoop parameters");
		stormDataPage = new StormDataPage("Set Storm parameters");
		sparkDataPage = new SparkDataPage("Set Spark parameters");
		privateConfigPage = new PrivateConfigPage("Set cluster parameters");

		addPage(initialPage);
		addPage(privateConfigPage);
		addPage(stormDataPage);
		addPage(hadoopDataPage);
		addPage(sparkDataPage);
		addPage(classPage);
		addPage(finalPage);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage currentPage) {
		
		Configuration currentConfig = Configuration.getCurrent();	
		
		/*
		 *  Initial Page
		 */
		
		if (currentPage == initialPage) {
			
			// Parse input from InitialPage 
			boolean spotPricing = initialPage.getSpotPricing();
			boolean admissionControl = initialPage.getAdmissionControl();
			Technology technology = initialPage.getTechnology();
			CloudType cloudType = initialPage.getCloudType();
			
			numClasses = initialPage.getClasses();
			currentClass = 0;
			
			currentConfig.setNumClasses(numClasses);
			
			if (spotPricing) {
				float spotRatio = initialPage.getSpotRatio();
				currentConfig.setSpotRatio(spotRatio);
			}
			
			try {
				if (cloudType == CloudType.PUBLIC)
					currentConfig.setScenario(technology, cloudType, spotPricing, null);
				else 
					currentConfig.setScenario(technology, cloudType, null, admissionControl);
				
			} catch (RuntimeException e) {
				initialPage.setErrorMessage("There was an error during the creation of the scenario, please try again.");
				return initialPage;
			}
					
			if (cloudType == CloudType.PRIVATE) {
				sparkDataPage.privateCase();
				hadoopDataPage.privateCase();
				return privateConfigPage;
				
			} 
			else {
				sparkDataPage.publicCase();
				hadoopDataPage.publicCase();
				return classPage;
			}
		}
		
		/*
		 *  Class Page
		 */
		
		if (currentPage == classPage) {
						
			classDescription = new ClassDesc(currentClass);
			currentClass ++;
			
			classDescription.setDdsmPath(classPage.getDDSMPath());
			classDescription.setAltDtsm(classPage.getAltDtsm());
			
			if (currentConfig.isHadoop()){
				
				// Get Think Time from the the first entry 
				String firstEntry = classPage.getAltDtsm().values().iterator().next();
				String thinkTime = getThinkTimeFromModel(firstEntry);
				classDescription.setMlPath(classPage.getMlPath());
				hadoopDataPage.setThinkTime(thinkTime);
				
				currentConfig.getClasses().add(classDescription);
				return hadoopDataPage;
			} 
			else if (currentConfig.isSpark()){
				classDescription.setMlPath(classPage.getMlPath());
				
				if ( Preferences.simulatorIsGSPN() || Preferences.simulatorIsJMT() ){
					String firstEntry = classPage.getAltDtsm().values().iterator().next();
					String thinkTime = getThinkTimeFromModel(firstEntry);
					sparkDataPage.setThinkTime(thinkTime);
				}
				return sparkDataPage;
				
			} else {
				return stormDataPage;
			}
		}
		
		/*
		 *  Hadoop Page
		 */

		if (currentPage == hadoopDataPage) {
			
			// Read Parameters from page
			classDescription.setHadoopParUD(hadoopDataPage.getParameters());
			
			
			// Check if I've done all classes
			if (currentClass == numClasses) {
				wizardCompleted = true;
				return finalPage;
			}
			
			// Otherwise create new ClassParameter
			classPage.reset();
			hadoopDataPage.reset();

			if (currentConfig.isPrivate())
				classPage.privateCase();

			return classPage;
		}

		/*
		 *  Spark Page
		 */
		
		if (currentPage == sparkDataPage) {
			classDescription.setHadoopParUD(sparkDataPage.getParameters());
	
			if (currentClass == numClasses) {
				wizardCompleted = true;
				return finalPage;
			}
			classPage.reset();
			sparkDataPage.reset();

			if (currentConfig.isPrivate())
				classPage.privateCase();
			return classPage;
		}
		
		/*
		 *  Storm Page
		 */
		
		if (currentPage == stormDataPage) {
			classDescription.setStormU(stormDataPage.getStormU());

			if (currentClass == numClasses) {
				wizardCompleted = true;
				return finalPage;
			}

			classPage.reset();
			stormDataPage.reset();

			if (currentConfig.isPrivate())
				classPage.privateCase();

			return classPage;
		}
		
		/*
		 *  Private Configuration Page
		 */
		
		if (currentPage == this.privateConfigPage) {
			PrivateConfiguration.getCurrent().setPriM(privateConfigPage.getMemForNode());
			PrivateConfiguration.getCurrent().setPriN(privateConfigPage.getNumNodes());
			PrivateConfiguration.getCurrent().setPriV(privateConfigPage.getCpuNode());
			classPage.privateCase();
			return classPage;
		}

		return null;
	}

	@Override
	public boolean canFinish() {
		return wizardCompleted;
	}
	
	
	private String getThinkTimeFromModel(String inputModel){
		
		String think;
		Configuration currentConfig = Configuration.getCurrent();
		try {
			
			String content = new String(Files.readAllBytes(Paths.get(inputModel)));
	        Pattern pattern = null;
        
	        if (currentConfig.isHadoop())    		
	        	pattern = Pattern.compile("hadoopExtDelay=\\[([0-9]+)\\]");
	        
	        if (currentConfig.isSpark())
	        	pattern = Pattern.compile("sparkExtDelay=\"\\(expr=([0-9]+),");
	        
	        Matcher matcher = pattern.matcher(content);
	        if (matcher.find()) {
	            think = matcher.group(1);
	            return think;
	        }
	        else {
	        	System.out.println("Cannot found think time in file: " + inputModel);
	        	return "";
	        }
        
		}
	    catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
        
		return "";
	}
}
