package it.polimi.deib.dspace.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.Optional;

import org.eclipse.core.internal.jobs.ObjectMap;
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
import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.unizar.disco.pnml.m2m.builder.StormActivityDiagram2PnmlResourceBuilder;
import es.unizar.disco.pnml.m2t.templates.gspn.GenerateGspn;
import es.unizar.disco.simulation.models.builders.IAnalyzableModelBuilder.ModelResult;
import es.unizar.disco.simulation.models.datatypes.PrimitiveVariableAssignment;
import es.unizar.disco.simulation.models.traces.Trace;
import fr.lip6.move.pnml.ptnet.PetriNet;
import fr.lip6.move.pnml.ptnet.PetriNetDoc;
import fr.lip6.move.pnml.ptnet.Place;
import it.polimi.deib.dspace.net.NetworkManager;
import it.polimi.diceH2020.SPACE4Cloud.shared.generators.ClassParametersGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.InstanceDataMultiProviderGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.JobMLProfileGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.JobMLProfilesMapGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.PublicCloudParametersGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.PublicCloudParametersMapGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.ClassParameters;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.ClassParametersMap;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.InstanceDataMultiProvider;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.JobMLProfile;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.JobMLProfilesMap;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.JobProfile;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.JobProfilesMap;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.PublicCloudParameters;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.PublicCloudParametersMap;
import it.polimi.diceH2020.SPACE4Cloud.shared.settings.Scenarios;

public class DICEWrap {
	private static DICEWrap diceWrap;
	private ModelResult result;
	private Configuration conf;
	private String path;
//	private String fileNames[] = {"1_h8_D500000.0MapJ1Cineca5xlarge.txt","1_h8_D500000.0RSJ1Cineca5xlarge.txt",
//	"1_h8_D500000.0.json"}; //to be replaced with conf content once web serice is ready to process it
	private String fileNames[] = {"aaa0MapJ1Cineca5xlarge.txt","aaa0RSJ1Cineca5xlarge.txt",
	"aaa0.json"}; //to be replaced with conf content once web serice is ready to process it
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
				break;
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
	
	public void generateJson(){
		InstanceDataMultiProvider data = InstanceDataMultiProviderGenerator.build();
		
		data.setId("aaa0");
		
		
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
		size.put("5xlarge", profilemap);
		Map provider = new HashMap<String,Map>();
		provider.put("Cineca", size);
		Map classdesc = new HashMap<String,Map>();
		classdesc.put("1", provider);
		
		data.setMapJobProfiles(new JobProfilesMap(classdesc));
		
		Map classdesc2 = new HashMap<String, ClassParameters>();
		ClassParameters clpm = ClassParametersGenerator.build(7);
		clpm.setD(500000.0);
		clpm.setPenalty(6.0);
		clpm.setThink(10000.0);
		clpm.setHlow(1);
		clpm.setHup(1);
		clpm.setM(6.0);
		clpm.setV(0.0);
		classdesc2.put("1", clpm);
		
		data.setMapClassParameters(new ClassParametersMap(classdesc2));
		
		PublicCloudParameters params = PublicCloudParametersGenerator.build(2);
		params.setR(11);
		params.setEta(0.22808514894233367);
		
		size = new HashMap<String,PublicCloudParameters>();
		size.put("5xlarge", params);
		
		provider = new HashMap<String,Map>();
		provider.put("Cineca", size);
		
		classdesc = new HashMap<String,Map>();
		classdesc.put("1", provider);
		
		PublicCloudParametersMap pub = PublicCloudParametersMapGenerator.build();
		pub.setMapPublicCloudParameters(classdesc);
		
		data.setMapPublicCloudParameters(pub);
		
		JobMLProfilesMap mlpm = JobMLProfilesMapGenerator.build();
		mlpm.setMapJobMLProfile(null);
		
		data.setMapJobMLProfiles(mlpm);
		
		if(data.validate()){
			System.out.println("VALID");
		}
		
		data.setMapVMConfigurations(null);
		data.setPrivateCloudParameters(null);
		
		ObjectMapper mapper = new ObjectMapper();
		
		String s="";
		try {
			s = mapper.writeValueAsString(data);
			mapper.writerWithDefaultPrettyPrinter().writeValue(new File("aaa0.json"), data);
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
