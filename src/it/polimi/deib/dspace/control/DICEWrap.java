package it.polimi.deib.dspace.control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.Device;
import org.eclipse.uml2.uml.Model;

import es.unizar.disco.pnml.m2m.builder.StormActivityDiagram2PnmlResourceBuilder;
import es.unizar.disco.pnml.m2t.templates.gspn.GenerateGspn;
import es.unizar.disco.simulation.models.builders.IAnalyzableModelBuilder.ModelResult;
import es.unizar.disco.simulation.models.datatypes.PrimitiveVariableAssignment;
import es.unizar.disco.simulation.models.traces.Trace;
import fr.lip6.move.pnml.ptnet.PetriNet;
import fr.lip6.move.pnml.ptnet.PetriNetDoc;
import fr.lip6.move.pnml.ptnet.Place;
import it.polimi.deib.dspace.net.NetworkManager;

public class DICEWrap {
	private static DICEWrap diceWrap;
	private ModelResult result;
	private Configuration conf;
	private String path;
	private String fileNames[] = {"1_h8_D500000.0MapJ1Cineca5xlarge.txt","1_h8_D500000.0RSJ1Cineca5xlarge.txt",
	"1_h8_D500000.0.json"}; //to be replaced with conf content once web serice is ready to process it
	private String scenario;
	private String initialMarking;
	
	public DICEWrap(){
		path = "/home/kom/eclipse/java-neon/eclipse/"; // to be replaced by fetching this info in tools
		scenario = "PublicAvgWorkLoad"; // ditto
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
			System.out.println("Incomplete, aborting");
			return;
		}
		
		switch(conf.getTechnology()){
			case "Storm":
				for (ClassDesc c : conf.getClasses()){
					createDirectory(); // TODO
					try {
						buildAnalyzableModel(c.getDtsmPath());
						extractInitialMarking();
						genGSPN();
						sendModel();
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
			default:
				System.err.println("Unknown technology");
		}
	}
	
	public void extractInitialMarking(){
		for(Trace i: result.getTraceSet().getTraces()){
			if (i.getFromDomainElement() instanceof Device  && i.getToAnalyzableElement() instanceof Place){
				initialMarking = ((Place)i.getToAnalyzableElement()).getInitialMarking().getText().toString();
				System.out.println(initialMarking);
			}
		}
	}
	
	public void buildAnalyzableModel(String umlModelPath) throws Exception{
		StormActivityDiagram2PnmlResourceBuilder builder = new StormActivityDiagram2PnmlResourceBuilder();
		
		ResourceSet set = new ResourceSetImpl();
		Resource res = set.getResource(URI.createFileURI(umlModelPath), true);
		result = builder.createAnalyzableModel((Model)res.getContents().get(0), new BasicEList<PrimitiveVariableAssignment>());
		
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
	
	public void genGSPN() throws Exception{
		File targetFolder = new File(path);
		GenerateGspn gspn = new GenerateGspn(((PetriNetDoc)result.getModel().get(0)).getNets().get(0),targetFolder, new ArrayList<EObject>());
		gspn.doGenerate(new BasicMonitor());
	}
	
	public void sendModel(){
		File files[] = {new File(path+fileNames[0]),
				new File(path+fileNames[1]),
				new File(path+fileNames[2])};
		try {
			System.out.println("Sending model:");
			for(File i: files){
				System.out.println("\t"+i.getName());
			}
			System.out.println("\t"+initialMarking);
			System.out.println("\t"+scenario);
			NetworkManager.getInstance().sendModel(files, scenario, initialMarking);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createDirectory(){}
}
