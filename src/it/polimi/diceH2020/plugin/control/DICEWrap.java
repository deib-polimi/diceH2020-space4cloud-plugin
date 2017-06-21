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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Map;

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
	private Configuration conf;
	private String scenario;
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
		conf = Configuration.getCurrent();
		System.out.println(conf.getID());

		if (!conf.isComplete()) {
			System.out.println("Incomplete, aborting"); // TODO check completion
														// for real
			return;
		}

		switch (conf.getTechnology()) {
		case "Storm":
			for (ClassDesc c : conf.getClasses()) {
				for (String alt : c.getAltDtsm().keySet()) {
					try {
						buildStormAnalyzableModel(c.getAltDtsm().get(alt));
						generatePNML(String.valueOf(c.getId()), alt);
						genGSPN();
						FileManager.editFiles(c.getId(), alt, extractStormId());
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}
				}
			}
			break;
		case "Hadoop Map-reduce":
			for (ClassDesc c : conf.getClasses()) {
				for (String alt : c.getAltDtsm().keySet())
					try {
						buildHadoopAnalyzableModel(c.getAltDtsm().get(alt));
						generatePNML(String.valueOf(c.getId()), alt);
						genGSPN();
						FileManager.editFiles(c.getId(), alt, extractHadoopId());
						extractParametersFromHadoopModel(c, alt);
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
			}
			break;
		case "Spark":
			for (ClassDesc c : conf.getClasses()) {
				for (String alt : c.getAltDtsm().keySet())
					try {
						buildSparkAnalyzableModel(c.getAltDtsm().get(alt));
						generatePNML(String.valueOf(c.getId()), alt);
						genGSPN();
						SparkFileManager.editFiles(c.getId(), alt, extractSparkIds());
						extractParametersFromHadoopModel(c, alt);
					} catch (Exception e) {
						System.err.println("SPARK EXCEPTION");
						e.printStackTrace();
					}
			}
			break;
		default:
			System.err.println("Unknown technology: " + conf.getTechnology());
			return;
		}

		FileManager.generateInputJson();
		FileManager.generateOutputJson();
		if (!Configuration.getCurrent().canSend()) {
			return;
		}
		try {
			setScenario();
			NetworkManager.getInstance().sendModel(FileManager.selectFiles(), scenario);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Extracts parameters from Hadoop model and appends them to the internal
	 * class structure
	 * 
	 * @param c
	 *            Current class
	 * @param alt
	 *            Current alternative to expand
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
		File aFile = new File(conf.getID() + "J" + classID + alt.replaceAll("-", "") + ".pnml");
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

	public void myGeneratePNML(int cdid, String alt) {

		String inputPath = Preferences.getSavingDir() + "res" + File.separator + "pnml_gspn_files" + File.separator
				+ "OutputSimulation" + File.separator + "PNML";
		String targetPath = Preferences.getSavingDir();

		System.out.println("PNML Input Directory: " + inputPath);
		System.out.println("PNML Output Directory: " + targetPath);

		File source = new File(inputPath);
		File dest = new File(targetPath);

		try {
			FileUtils.copyDirectory(source, dest);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// the pnml file has to have a specific name
		File files[] = dest.listFiles();
		String outputFilePath;
		if (Configuration.getCurrent().getIsPrivate()) {
			outputFilePath = Preferences.getSavingDir() + conf.getID() + "J" + cdid + "inHouse" + alt;
		} else {
			outputFilePath = Preferences.getSavingDir() + conf.getID() + "J" + cdid + alt.replaceAll("-", "");
		}

		File pnmlFilePath = new File(outputFilePath + ".pnml");
		File trcFilePath = new File(outputFilePath + ".trc.xmi");
		for (File f : files) {
			if (f.getName().endsWith(".pnml")) {
				f.renameTo(pnmlFilePath);
			} else if (f.getName().endsWith(".trc.xmi")) {
				f.renameTo(trcFilePath);
			}
		}
		// TODO find another way to rename these files because there are
		// different files that ends with pnml in this folder, so in order to
		// work you have to delete these files every time
	}

	public void myGenGSPN() {

		String inputPath = Preferences.getSavingDir() + "res" + File.separator + "pnml_gspn_files" + File.separator
				+ "OutputSimulation" + File.separator + "GSPN";
		String targetPath = Preferences.getSavingDir() + "tmp" + File.separator;

		System.out.println("GSPN Input Directory: " + inputPath);
		System.out.println("GSPN Output Directory: " + targetPath);

		File source = new File(inputPath);
		File dest = new File(targetPath);

		try {
			FileUtils.copyDirectory(source, dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setScenario() {
		if (!Configuration.getCurrent().getIsPrivate()) {
			if (Configuration.getCurrent().getHasLtc()) {
				this.scenario = "PublicPeakWorkload";
			} else {
				this.scenario = "PublicAvgWorkLoad";
			}
		} else {
			this.scenario = "PrivateAdmissionControl";
		}
	}

	public static void trySparkFork1() {
		tryMe("ConfFork1.txt");
	}

	public static void trySparkFork2() {
		tryMe("ConfFork2.txt");
	}

	public static void tryMe(String configFile) {
		DICEWrap dw = new DICEWrap();
		dw.conf = (Configuration) WriterReader.readObject(configFile);
		Configuration.setConfiguration(dw.conf);
		dw.start();
	}
}
