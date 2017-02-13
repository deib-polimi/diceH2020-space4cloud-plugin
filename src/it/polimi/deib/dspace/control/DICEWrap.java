package it.polimi.deib.dspace.control;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

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
import org.eclipse.uml2.uml.Transition;

import es.unizar.disco.pnml.m2m.builder.HadoopActivityDiagram2PnmlResourceBuilder;
import es.unizar.disco.pnml.m2m.builder.StormActivityDiagram2PnmlResourceBuilder;
import es.unizar.disco.pnml.m2t.templates.gspn.GenerateGspn;
import es.unizar.disco.simulation.models.builders.IAnalyzableModelBuilder.ModelResult;
import es.unizar.disco.simulation.models.datatypes.PrimitiveVariableAssignment;
import es.unizar.disco.simulation.models.traces.Trace;
import fr.lip6.move.pnml.ptnet.PetriNetDoc;
import fr.lip6.move.pnml.ptnet.Place;
import it.polimi.deib.dspace.net.NetworkManager;

public class DICEWrap {
	private static DICEWrap diceWrap;
	private ModelResult result;
	private Configuration conf;
//	private String fileNames[] = {"1_h8_D500000.0MapJ1Cineca5xlarge.txt","1_h8_D500000.0RSJ1Cineca5xlarge.txt",
//	"1_h8_D500000.0.json"}; //to be replaced with conf content once web serice is ready to process it
	private String fileNames[] = {"aaa0MapJ1Cineca5xlarge.txt","aaa0RSJ1Cineca5xlarge.txt",
	"aaa0.json"}; //to be replaced with conf content once web service is ready to process it
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
	
	public void start(){
		conf = Configuration.getCurrent();
		if(!conf.isComplete()){
			System.out.println("Incomplete, aborting"); //TODO check completion for real
			return;
		}
		
		switch(conf.getTechnology()){
			case "Storm":
				for (ClassDesc c : conf.getClasses()){
					for(String alt : c.getAltDdsm().keySet()){
						try {
							buildStormAnalyzableModel(c.getAltDdsm().get(alt));
							genGSPN(); 
							FileManager.getInstance().editFiles(c.getId(),alt,extractId());
						} catch (IOException e) {
							System.out.println(e.getMessage());
						}
					}
				}
				break;
			case "Hadoop":
				for (ClassDesc c : conf.getClasses()){
					try {
						buildHadoopAnalyzableModel(c.getDtsmPath());
						extractHadoopInitialMarking();
						genGSPN();
					} catch (Exception e) {
						System.err.println("HADOOP EXCEPTION");
						System.out.println(e.getMessage());
					}
				}
				break;
			default:
				System.err.println("Unknown technology: "+conf.getTechnology());
		}
		
		FileManager.getInstance().generateJson();
		try {
			NetworkManager.getInstance().sendModel(FileManager.getInstance().selectFiles(), scenario);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void extractStormInitialMarking(){
		for(Trace i: result.getTraceSet().getTraces()){
			if (i.getFromDomainElement() instanceof Device  && i.getToAnalyzableElement() instanceof Place){
				initialMarking = ((Place)i.getToAnalyzableElement()).getInitialMarking().getText().toString();
				System.out.println(initialMarking);
			}
		}
	}
	
	private String extractId(){
		System.out.println("Extracting");
		for(Trace i: result.getTraceSet().getTraces()){
			if (i.getFromDomainElement() instanceof Device  && i.getToAnalyzableElement() instanceof Place){
				System.out.println("Found "+ ((Place)i.getToAnalyzableElement()).getId());
				return ((Place)i.getToAnalyzableElement()).getId();
			}
		}
		return null;
	}
	
	public void buildStormAnalyzableModel(String umlModelPath){
		StormActivityDiagram2PnmlResourceBuilder builder = new StormActivityDiagram2PnmlResourceBuilder();
		
		ResourceSet set = new ResourceSetImpl();
		Resource res = set.getResource(URI.createFileURI(umlModelPath), true);
		result = builder.createAnalyzableModel((Model)res.getContents().get(0), new BasicEList<PrimitiveVariableAssignment>());
		
		System.out.println("Model built");
		
		/*PetriNet pnd = ((PetriNetDoc)result.getModel().get(0)).getNets().get(0);
		File aFile = new File("small.pnml"); 
	    FileOutputStream outputFile = null; 
	    try {
	      outputFile = new FileOutputStream(aFile, true);
	      System.out.println("File stream created successfully.");
	    } catch (Exception e) {
	      e.printStackTrace(System.err);
	    }
	    FileChannel outChannel = outputFile.getChannel();
	    pnd.toPNML(outChannel);*/
	}
	
	public void buildHadoopAnalyzableModel(String umlModelPath){
		HadoopActivityDiagram2PnmlResourceBuilder builder = new HadoopActivityDiagram2PnmlResourceBuilder();
		
		ResourceSet set = new ResourceSetImpl();
		Resource res = set.getResource(URI.createFileURI(umlModelPath), true);
		result = builder.createAnalyzableModel((Model)res.getContents().get(0), new BasicEList<PrimitiveVariableAssignment>());
	}
	
	public void extractHadoopInitialMarking(){
		System.out.println("Extracting marking\n");
		System.err.println(result.getTraceSet().getTraces().size());
		for(Trace i: result.getTraceSet().getTraces()){
			System.out.println("Traversing traces");
			if (i.getFromDomainElement() instanceof FinalNode  && i.getToAnalyzableElement() instanceof Transition){
				initialMarking = ((Place)i.getToAnalyzableElement()).getInitialMarking().getText().toString();
				System.err.println(initialMarking);
			}
		}
	} 
	
	public void genGSPN() throws IOException{
		File targetFolder = new File(FileManager.getInstance().getPath()+"tmp/");
		GenerateGspn gspn = new GenerateGspn(((PetriNetDoc)result.getModel().get(0)).getNets().get(0),targetFolder, new ArrayList<EObject>());
		gspn.doGenerate(new BasicMonitor());
		System.out.println("GSPN generated");
	}
}
