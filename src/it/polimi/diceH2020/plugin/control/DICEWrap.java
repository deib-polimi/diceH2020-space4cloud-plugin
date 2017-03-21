package it.polimi.diceH2020.plugin.control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.Device;
import org.eclipse.uml2.uml.FinalNode;
import org.eclipse.uml2.uml.Model;

import es.unizar.disco.pnml.m2m.builder.HadoopActivityDiagram2PnmlResourceBuilder;
import es.unizar.disco.pnml.m2m.builder.StormActivityDiagram2PnmlResourceBuilder;
import es.unizar.disco.pnml.m2t.templates.gspn.GenerateGspn;
import es.unizar.disco.simulation.models.builders.IAnalyzableModelBuilder.ModelResult;
import es.unizar.disco.simulation.models.datatypes.PrimitiveVariableAssignment;
import es.unizar.disco.simulation.models.traces.Trace;
import fr.lip6.move.pnml.ptnet.PetriNet;
import fr.lip6.move.pnml.ptnet.PetriNetDoc;
import fr.lip6.move.pnml.ptnet.Place;
import fr.lip6.move.pnml.ptnet.Transition;
import it.polimi.diceH2020.plugin.net.NetworkManager;

/**
 * Manages models transformations and analysis using DICE-plugin APIs and internal methods.
 * @author kom
 *
 */
public class DICEWrap {
	private static DICEWrap diceWrap;
	private ModelResult result;
	private Configuration conf;
	private String scenario;
	private String initialMarking;

	public DICEWrap(){
		scenario = "PublicAvgWorkLoad";
		initialMarking = "";
	}

	public static DICEWrap getWrapper(){
		if (diceWrap == null){
			diceWrap = new DICEWrap();
		}
		return diceWrap;
	}

	/**
	 * Creates models from input files and upload them to the web service
	 */
	public void start(){
		conf = Configuration.getCurrent();
		System.out.println(conf.getID());
		if(!conf.isComplete()){
			System.out.println("Incomplete, aborting"); //TODO check completion for real
			return;
		}

		switch(conf.getTechnology()){
		case "Storm":
			for (ClassDesc c : conf.getClasses()){
				for(String alt : c.getAltDtsm().keySet()){
					try {
						buildStormAnalyzableModel(c.getAltDtsm().get(alt));
						generatePNML(String.valueOf(c.getId()), alt);
						genGSPN(); 
						FileManager.getInstance().editFiles(c.getId(), alt, extractStormId());
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}
				}
			}
			break;
		case "Hadoop":
			for (ClassDesc c : conf.getClasses()){
				for(String alt : c.getAltDtsm().keySet())
					try {
						buildHadoopAnalyzableModel(c.getAltDtsm().get(alt));
						generatePNML(String.valueOf(c.getId()), alt);
						genGSPN();
						FileManager.getInstance().editFiles(c.getId(), alt, extractHadoopId());
						extractParametersFromHadoopModel(c, alt);
					} catch (Exception e) {
						System.err.println("HADOOP EXCEPTION");
						System.out.println(e.getMessage());
					}
			}
			//TODO: just for debug, remove this cycle
			for(ClassDesc c : conf.getClasses()){
				System.err.println("Class: " + c.getId());
				for(String alt : c.getAltDtsmHadoop().keySet()){
					System.err.println("\t" + alt);
					for (String a : c.getAltDtsmHadoop().get(alt).keySet()){
						System.err.println("\t\t" + a + " : " + c.getAltDtsmHadoop().get(alt).get(a));
					}
				}
			}
			break;
		default:
			System.err.println("Unknown technology: "+conf.getTechnology());
			return;
		}

		FileManager.getInstance().generateInputJson();
		FileManager.getInstance().generateOutputJson();
		try {
			NetworkManager.getInstance().sendModel(FileManager.getInstance().selectFiles(), scenario);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Extracts parameters from Hadoop model and appends them to the internal class structure
	 * @param c Current class
	 * @param alt Current alternative to expand
	 */
	private void extractParametersFromHadoopModel(ClassDesc c, String alt) {
		String srcFile = c.getAltDtsm().get(alt);
		Map<String, String> par = FileManager.getInstance().parseDOMXmlFile(srcFile);
		c.expandAltDtsmHadoop(alt, par);
	}

	//TODO: may be useless
	public void extractStormInitialMarking(){
		for(Trace i: result.getTraceSet().getTraces()){
			if (i.getFromDomainElement() instanceof Device  && i.getToAnalyzableElement() instanceof Place){
				initialMarking = ((Place)i.getToAnalyzableElement()).getInitialMarking().getText().toString();
				System.out.println(initialMarking);
			}
		}
	}

	/**
	 * Analyzes current model looking for the Trace to be replaced w/ placeholder
	 * @return String ID of the Trace
	 */
	private String extractStormId(){
		System.out.println("Extracting");
		for(Trace i: result.getTraceSet().getTraces()){
			if (i.getFromDomainElement() instanceof Device  && i.getToAnalyzableElement() instanceof Place){
				System.out.println("Found "+ ((Place)i.getToAnalyzableElement()).getId());
				return ((Place)i.getToAnalyzableElement()).getId();
			}
		}
		return null;
	}

	/**
	 * Builds an analyzable Storm ModelResult from the given model
	 * @param umlModelPath Input file path
	 */
	public void buildStormAnalyzableModel(String umlModelPath){
		StormActivityDiagram2PnmlResourceBuilder builder = new StormActivityDiagram2PnmlResourceBuilder();

		ResourceSet set = new ResourceSetImpl();
		Resource res = set.getResource(URI.createFileURI(umlModelPath), true);
		result = builder.createAnalyzableModel((Model)res.getContents().get(0), new BasicEList<PrimitiveVariableAssignment>());

		System.out.println("Model built for file: " + umlModelPath);
	}

	/**
	 * Builds an analyzable Hadoop ModelResult from the given model
	 * @param umlModelPath Input file path
	 */	
	public void buildHadoopAnalyzableModel(String umlModelPath){
		HadoopActivityDiagram2PnmlResourceBuilder builder = new HadoopActivityDiagram2PnmlResourceBuilder();

		ResourceSet set = new ResourceSetImpl();
		Resource res = set.getResource(URI.createFileURI(umlModelPath), true);
		result = builder.createAnalyzableModel((Model)res.getContents().get(0), new BasicEList<PrimitiveVariableAssignment>());
	}

	/**
	 * Builds PNML model file from ModelResult.  
	 * @param classID
	 * @param alt
	 */
	public void generatePNML(String classID, String alt){
		PetriNet pnd = ((PetriNetDoc)result.getModel().get(0)).getNets().get(0);
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

	//TODO: set all these methods to private

	/**
	 * Analyzes current model looking for the Trace to be replaced w/ placeholder
	 * @return String ID of the Trace
	 */
	public String extractHadoopId(){
		for(Trace i: result.getTraceSet().getTraces()){
			if (i.getFromDomainElement() instanceof FinalNode && i.getToAnalyzableElement() instanceof Transition){
				return ((Transition)i.getToAnalyzableElement()).getId();
			}
		}
		return null;
	} 

	/**
	 * Creates GSPN model (.net and .def files) in the given directory.
	 * @throws IOException
	 */
	public void genGSPN() throws IOException{
		File targetFolder = new File(FileManager.getInstance().getPath()+"tmp/");
		GenerateGspn gspn = new GenerateGspn(((PetriNetDoc)result.getModel().get(0)).getNets().get(0),targetFolder, new ArrayList<EObject>());
		gspn.doGenerate(new BasicMonitor());
		System.out.println("GSPN generated");
	}
}
