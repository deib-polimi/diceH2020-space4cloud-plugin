package it.polimi.deib.dspace.control;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.eclipse.uml2.uml.Transition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.unizar.disco.pnml.m2m.builder.HadoopActivityDiagram2PnmlResourceBuilder;
import es.unizar.disco.pnml.m2m.builder.StormActivityDiagram2PnmlResourceBuilder;
import es.unizar.disco.pnml.m2t.templates.gspn.GenerateGspn;
import es.unizar.disco.simulation.models.builders.IAnalyzableModelBuilder.ModelResult;
import es.unizar.disco.simulation.models.datatypes.PrimitiveVariableAssignment;
import es.unizar.disco.simulation.models.traces.Trace;
import fr.lip6.move.pnml.ptnet.PetriNetDoc;
import fr.lip6.move.pnml.ptnet.Place;
import it.polimi.deib.dspace.net.NetworkManager;
import it.polimi.diceH2020.SPACE4Cloud.shared.generators.ClassParametersGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.InstanceDataMultiProviderGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.JobMLProfilesMapGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.PublicCloudParametersGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.PublicCloudParametersMapGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.ClassParameters;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.ClassParametersMap;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.InstanceDataMultiProvider;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.JobMLProfilesMap;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.JobProfilesMap;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.PublicCloudParameters;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.PublicCloudParametersMap;

public class DICEWrap {
	private static DICEWrap diceWrap;
	private ModelResult result;
	private Configuration conf;
//	private String fileNames[] = {"1_h8_D500000.0MapJ1Cineca5xlarge.txt","1_h8_D500000.0RSJ1Cineca5xlarge.txt",
//	"1_h8_D500000.0.json"}; //to be replaced with conf content once web serice is ready to process it
	private String fileNames[] = {"aaa0MapJ1Cineca5xlarge.txt","aaa0RSJ1Cineca5xlarge.txt",
	"aaa0.json"}; //to be replaced with conf content once web serice is ready to process it
	private String scenario;
	private String initialMarking;
	
	public DICEWrap(){
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
					try {
						buildStormAnalyzableModel(c.getDtsmPath());
						genGSPN(); 
						FileManager.getInstance().renameFiles(c,extractId());
					} catch (Exception e) {
						System.out.println(e.getMessage());
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
		
		//generateJson();
		//sendModel();
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
	
	public void genGSPN() throws Exception{
		File targetFolder = new File(FileManager.getInstance().getPath());
		GenerateGspn gspn = new GenerateGspn(((PetriNetDoc)result.getModel().get(0)).getNets().get(0),targetFolder, new ArrayList<EObject>());
		gspn.doGenerate(new BasicMonitor());
		System.out.println("GSPN generated");
	}
	
	public void sendModel(){
		String path = FileManager.getInstance().getPath();
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
	
	public void generateJson(){
		conf = Configuration.getCurrent(); //TODO: REMOVE
		InstanceDataMultiProvider data = InstanceDataMultiProviderGenerator.build();
		
		data.setId(conf.getID());
		
		//Set MapJobProfile
		Map classdesc = new HashMap<String,Map>();
		for(ClassDesc c : conf.getClasses()){
			Map alternatives = new HashMap<String,Map>();
			for (String alt: c.getAltDdsm().keySet()){
				String split[] = alt.split("-");
				
				Map profile = new HashMap<>();
				profile.put("datasize", new Float(148.0));
				profile.put("mavg", new Float(148.0));
				profile.put("mmax", new Float(148.0));
				profile.put("nm", new Float(148.0));
				profile.put("nr", new Float(148.0));
				profile.put("ravg", new Float(148.0));
				profile.put("rmax", new Float(148.0));
				profile.put("shbytesavg", new Float(148.0));
				profile.put("shbytesmax", new Float(148.0));
				profile.put("shtypavg", new Float(148.0));
				profile.put("shtypmax", new Float(148.0));
				
				Map profilemap = new HashMap<String,Map>();
				profilemap.put("profileMap", profile);
				
				Map size = new HashMap<String, Map>();
				size.put(split[1], profilemap);
				
				alternatives.put(split[0], size);
			}
			classdesc.put(String.valueOf(c.getId()), alternatives);
		}
		data.setMapJobProfiles(new JobProfilesMap(classdesc));
		
		//Set MapClassParameter
		classdesc = new HashMap<String, ClassParameters>();
		for(ClassDesc c : conf.getClasses()){
			ClassParameters clpm = ClassParametersGenerator.build(7);
			clpm.setD(500000.0);
			clpm.setPenalty(6.0);
			clpm.setThink(10000.0);
			clpm.setHlow(1);
			clpm.setHup(1);
			clpm.setM(6.0);
			clpm.setV(0.0);
			classdesc.put(String.valueOf(c.getId()), clpm);
		}
		
		data.setMapClassParameters(new ClassParametersMap(classdesc));
		
		if(!conf.getIsPrivate()){
			//Set PublicCloudParameters
			classdesc = new HashMap<String,Map>();
			for(ClassDesc c : conf.getClasses()){
				Map alternatives = new HashMap<String,Map>();
				for (String alt: c.getAltDdsm().keySet()){
					String split[] = alt.split("-");
					
					PublicCloudParameters params = PublicCloudParametersGenerator.build(2);
					params.setR(11);
					params.setEta(0.22808514894233367);
					
					Map size = new HashMap<String, Map>();
					size.put(split[1], params);
					
					alternatives.put(split[0], size);
				}
				classdesc.put(String.valueOf(c.getId()), alternatives);
			}
			
			PublicCloudParametersMap pub = PublicCloudParametersMapGenerator.build();
			pub.setMapPublicCloudParameters(classdesc);
			
			data.setMapPublicCloudParameters(pub);
			data.setPrivateCloudParameters(null);
		}
		else{
			//TODO: private case
		}
		
		//Set mapJobMLProfile
		JobMLProfilesMap jML = JobMLProfilesMapGenerator.build();
		jML.setMapJobMLProfile(null);
		data.setMapJobMLProfiles(jML);
		
		//Set MapVMConfigurations
		
		data.setMapVMConfigurations(null);
		
		//Generate Json
		ObjectMapper mapper = new ObjectMapper();
		
		String s="";
		try {
			s = mapper.writeValueAsString(data);
			mapper.writerWithDefaultPrettyPrinter().writeValue(new File(conf.getID()+".json"), data);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(s);
		
	}
}
