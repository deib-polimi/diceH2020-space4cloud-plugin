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

package it.polimi.diceH2020.plugin.control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Map;
import java.io.FileWriter;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.Device;
import org.eclipse.uml2.uml.FinalNode;
import org.eclipse.uml2.uml.Model;

import es.unizar.disco.pnml.m2m.builder.HadoopActivityDiagram2PnmlResourceBuilder;
import es.unizar.disco.pnml.m2m.builder.SparkActivityDiagram2PnmlResourceBuilder;
import es.unizar.disco.pnml.m2m.builder.StormActivityDiagram2PnmlResourceBuilder;
import es.unizar.disco.pnml.m2t.templates.gspn.GenerateGspn;
import es.unizar.disco.simulation.models.builders.IAnalyzableModelBuilder.ModelResult;
import es.unizar.disco.simulation.models.datatypes.PrimitiveVariableAssignment;
import es.unizar.disco.simulation.models.traces.Trace;
import fr.lip6.move.pnml.ptnet.PetriNetDoc;
import fr.lip6.move.pnml.ptnet.Place;
import fr.lip6.move.pnml.ptnet.Transition;
import it.polimi.diceH2020.plugin.net.NetworkManager;
import it.polimi.diceH2020.plugin.preferences.Preferences;
import it.polimi.diceH2020.SPACE4Cloud.shared.settings.*;
import utils.WriterReader;

/**
 * Manages models transformations and analysis using DICE-plugin APIs and
 * internal methods.
 * 
 * @author kom
 *
 */
public class DICEWrap {
	private static DICEWrap diceWrap;
	private ModelResult result;
	private Configuration currentConfig;
	private String initialMarking;

	public DICEWrap() {
		initialMarking = "";
	}

	public static DICEWrap getWrapper() {
		if (diceWrap == null) {
			diceWrap = new DICEWrap();
		}
		return diceWrap;
	}

	/**
	 * Creates models from input files and upload them to the web service
	 */
	public void start() {
		
		currentConfig = Configuration.getCurrent();

		if (!currentConfig.isComplete()) {
			System.out.println("Incomplete, aborting");
			return;
		}

		/*
		 * STORM 
		 */
		
		if (currentConfig.isStorm()){
			
			for (ClassDesc c : currentConfig.getClasses()) {
				for (String alt : c.getAltDtsm().keySet()) {
					try {
						buildStormAnalyzableModel(c.getAltDtsm().get(alt));
						generatePNML(String.valueOf(c.getId()), alt);
						genGSPN();
						FileManager.editFiles(c.getId(), alt, extractStormId());
						FileManager.createStatFile(c.getId(), alt, extractStormId());
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}
				}
			}
		}
		
		/*
		 * HADOOP
		 */
		
		if (currentConfig.isHadoop()){
			for (ClassDesc c : currentConfig.getClasses()) {
				for (String alt : c.getAltDtsm().keySet()){
					try {
						buildHadoopAnalyzableModel(c.getAltDtsm().get(alt));
						generatePNML(String.valueOf(c.getId()), alt);
						
						if (Preferences.simulatorIsDAGSIM() ){
							System.err.println("Dag Sim not supported yet");	
							return;
						}
						
						else if (Preferences.simulatorIsGSPN()){
							genGSPN();
							FileManager.editFiles(c.getId(), alt, extractHadoopId());
						} 
						else if (Preferences.getSimulator().equals(Preferences.JMT)){
							genJSIM(c.getId(), alt, extractHadoopId());
							FileManager.editJsimgHadoop(c.getId(), alt, extractHadoopId());		
						}
										
						FileManager.createStatFile(c.getId(), alt, extractHadoopId());
						extractParametersFromHadoopModel(c, alt);
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
			}
		}
		
		/*
		 * SPARK 
		 */
		
		if (currentConfig.isSpark()){
			for (ClassDesc c : currentConfig.getClasses()) {
				for (String alt : c.getAltDtsm().keySet()){
					
					// DagSim
					if (Preferences.simulatorIsDAGSIM()){
						File logFolder = new File(c.getAltDtsm().get(alt));
						SparkFileManager.copyDagLogs(logFolder, c.getId(), alt);
						// FileManager.generateInputJson(); 	fails
						
						try {
							NetworkManager.getInstance().sendModel(FileManager.selectFiles(), currentConfig.getScenario());
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return;
					}
					
					else {
					
						try {
							buildSparkAnalyzableModel(c.getAltDtsm().get(alt));
							generatePNML(String.valueOf(c.getId()), alt);
						
							if (Preferences.simulatorIsGSPN()){
								genGSPN();
								SparkFileManager.editFiles(c.getId(), alt, extractSparkIds());							
							}
							
							else if (Preferences.simulatorIsJMT()){
								System.out.println("JMT!");
								genJSIM(c.getId(), alt, extractSparkIds().getNumberOfConcurrentUsers());
								SparkFileManager.editJSIMG(c.getId(), alt, extractSparkIds());
							}
							
							SparkFileManager.createStatFile(c.getId(), alt, extractSparkIds().getNumberOfConcurrentUsers());
							extractParametersFromHadoopModel(c, alt);
						} catch (Exception e) {
							System.err.println("SPARK EXCEPTION");
							e.printStackTrace();
						}
					}
				}
			}	
		}
		
		FileManager.generateInputJson();
		FileManager.generateOutputJson();
		if (!Configuration.getCurrent().canSend()) {
			return;
		}
		try {
			NetworkManager.getInstance().sendModel(FileManager.selectFiles(), currentConfig.getScenario());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 

	/**
	 * Extracts parameters from Hadoop model and appends them to the internal
	 * class structure
	 * 
	 * @param c: Current Class
	 * @param alt: Current alternative to expand
	 *            
	 */
	private void extractParametersFromHadoopModel(ClassDesc c, String alt) {
		String srcFile = c.getAltDtsm().get(alt);
		Map<String, String> par = FileManager.parseDOMXmlFile(srcFile);
		c.expandAltDtsmHadoop(alt, par);
	}

	// TODO: may be useless
	public void extractStormInitialMarking() {
		for (Trace i : result.getTraceSet().getTraces()) {
			if (i.getFromDomainElement() instanceof Device && i.getToAnalyzableElement() instanceof Place) {
				initialMarking = ((Place) i.getToAnalyzableElement()).getInitialMarking().getText().toString();
				System.out.println(initialMarking);
			}
		}
	}

	/**
	 * Analyzes current model looking for the Trace to be replaced w/
	 * placeholder
	 * 
	 * @return String ID of the Trace
	 */
	private String extractStormId() {
		System.out.println("Extracting");
		for (Trace i : result.getTraceSet().getTraces()) {
			if (i.getFromDomainElement() instanceof Device && i.getToAnalyzableElement() instanceof Place) {
				System.out.println("Found " + ((Place) i.getToAnalyzableElement()).getId());
				return ((Place) i.getToAnalyzableElement()).getId();
			}
		}
		return null;
	}

	/**
	 * Builds an analyzable Storm ModelResult from the given model
	 * 
	 * @param umlModelPath
	 *            Input file path
	 */
	public void buildStormAnalyzableModel(String umlModelPath) {
		StormActivityDiagram2PnmlResourceBuilder builder = new StormActivityDiagram2PnmlResourceBuilder();

		ResourceSet set = new ResourceSetImpl();
		Resource res = set.getResource(URI.createFileURI(umlModelPath), true);
		result = builder.createAnalyzableModel((Model) res.getContents().get(0),
				new BasicEList<PrimitiveVariableAssignment>());

		System.out.println("Model built for file: " + umlModelPath);
	}

	/**
	 * Builds an analyzable Hadoop ModelResult from the given model
	 * 
	 * @param umlModelPath
	 *            Input file path
	 */
	public void buildHadoopAnalyzableModel(String umlModelPath) {
		HadoopActivityDiagram2PnmlResourceBuilder builder = new HadoopActivityDiagram2PnmlResourceBuilder();

		ResourceSet set = new ResourceSetImpl();
		Resource res = set.getResource(URI.createFileURI(umlModelPath), true);
		result = builder.createAnalyzableModel((Model) res.getContents().get(0),
				new BasicEList<PrimitiveVariableAssignment>());
	}

	public void buildSparkAnalyzableModel(String umlModelPath) {
		SparkActivityDiagram2PnmlResourceBuilder builder = new SparkActivityDiagram2PnmlResourceBuilder();

		ResourceSet set = new ResourceSetImpl();
		Resource res = set.getResource(URI.createFileURI(umlModelPath), true);
		EList<EObject> e = res.getContents();
		System.out.println(e.size());
		result = builder.createAnalyzableModel((Model) e.get(0), new BasicEList<PrimitiveVariableAssignment>());
	}

	private SparkIds extractSparkIds() {
		String devices2resourcesId = getPlaceIdFromTraces(SparkIds.DEVICES_2_RESOURCES);
		String usersId = getPlaceIdFromTraces(SparkIds.USERS);

		// Find Id of Last Transaction
		String numberOfConcurrentUsersIds = getTransitionIdFromTraces(SparkIds.NUMBER_OF_CONCURRENT_USERS);
		return new SparkIds(devices2resourcesId, usersId, numberOfConcurrentUsersIds);
	}

	private String getTransitionIdFromTraces(String rule) {
		String id = null;
		for (Trace i : result.getTraceSet().getTraces()) {
			if (rule.equalsIgnoreCase(i.getRule())) {
				if (i.getToAnalyzableElement() instanceof Transition) {
					id = ((Transition) i.getToAnalyzableElement()).getId();
					return id;
				}
			}
		}
		return null;
	}
	
	private String getPlaceIdFromTraces(String rule) {
		String id = null;
		for (Trace i : result.getTraceSet().getTraces()) {
			if (rule.equalsIgnoreCase(i.getRule())) {
				if (i.getToAnalyzableElement() instanceof Place) {
					id = ((Place) i.getToAnalyzableElement()).getId();
					return id;
				}
			}
		}
		return null;
	}

	/**
	 * Builds PNML model file from ModelResult.
	 * 
	 * @param classID
	 * @param alt
	 */
	public void generatePNML(String classID, String alt) {
		PetriNetDoc pnd = (PetriNetDoc) result.getModel().get(0);
		File aFile = new File(Preferences.getSavingDir() + currentConfig.getID() + "J" + classID + alt.replaceAll("-", "") + ".pnml");
		FileOutputStream outputFile = null;
		try {
			outputFile = new FileOutputStream(aFile, true);
			System.out.println("File stream created successfully.");
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		FileChannel outChannel = outputFile.getChannel();
		pnd.toPNML(outChannel);
	}

	// TODO: set all these methods to private

	/**
	 * Analyzes current model looking for the Trace to be replaced w/
	 * placeholder
	 * 
	 * @return String ID of the Trace
	 */
	public String extractHadoopId() {
		for (Trace i : result.getTraceSet().getTraces()) {
			if (i.getFromDomainElement() instanceof FinalNode && i.getToAnalyzableElement() instanceof Transition) {
				String id = ((Transition) i.getToAnalyzableElement()).getId();
				return id;
			}
		}
		return null;
	}

	/**
	 * Creates GSPN model (.net and .def files) in the given directory.
	 * 
	 * @throws IOException
	 */
	public void genGSPN() throws IOException {
		File targetFolder = new File(Preferences.getSavingDir() + "tmp/");
		GenerateGspn gspn = new GenerateGspn(((PetriNetDoc) result.getModel().get(0)).getNets().get(0), targetFolder,
				new ArrayList<EObject>());
		gspn.doGenerate(new BasicMonitor());
		System.out.println("GSPN generated");
	}
	
	/**
	 * Creates JSIM model (.jsimg) from pnml in the given directory  
	 * 
	 * @throws IOException
	 */
	
	public static void genJSIM(int cdid, String alt, String lastTransactionId) throws IOException {
		Configuration currentConfig = Configuration.getCurrent();
		File sparkIdx = new File(Preferences.getSavingDir() + "spark.idx");
		String fileName; 
				
		if (currentConfig.isPrivate()) {
			fileName = Preferences.getSavingDir() + currentConfig.getID() + "J" + cdid + "inHouse" + alt;
		} else {
			fileName = Preferences.getSavingDir() + currentConfig.getID() + "J" + cdid + alt.replaceAll("-", "");
		}
		
		try {
			FileWriter fw = new FileWriter(sparkIdx.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(lastTransactionId);
			bw.close();			
		}
		catch (IOException e){
			e.printStackTrace();
		}
		
		String pnmlPath = Preferences.getSavingDir() + currentConfig.getID() + "J" + cdid + alt.replaceAll("-", "") + ".pnml";
		String outputPath = new File(fileName + ".jsimg").getAbsolutePath();
		String indexPath = sparkIdx.getAbsolutePath();
		
				
		String command = String.format("java -cp %sbin:%slib/* PNML_Pre_Processor gspn %s %s %s", 
						 Preferences.getJmTPath(), Preferences.getJmTPath(), pnmlPath, outputPath, indexPath);
		
		System.out.println("Calling PNML_Pre_Processor");
		System.out.println(command);
		
		Process proc;

		try {
			proc = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			e.printStackTrace();
			return; 
		}
		
		try {
			proc.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
