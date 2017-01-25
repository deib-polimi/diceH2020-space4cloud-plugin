package it.polimi.deib.dspace.control;

import java.io.File;
import java.io.FileOutputStream;
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

public class DICEWrap {
	private static DICEWrap diceWrap;
	private ModelResult result;
	private Configuration conf;
	
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
		if(conf.getTechnology().equals("Storm")){
			for (ClassDesc c : conf.getClasses()){
				try {
					buildAnalyzableModel(c.getDtsmPath());
					genGSPN();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
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
		
		String id;
		
		System.out.println("TRAVERSING LIST");
		for(Trace i: result.getTraceSet().getTraces()){
			if (i.getFromDomainElement() instanceof Device  && i.getToAnalyzableElement() instanceof Place){
				id = ((Place)i.getToAnalyzableElement()).getId();
				System.out.println(id);
				System.out.println(((Place)i.getToAnalyzableElement()).getInitialMarking().getText());
			}
		}
	}
	
	public void genGSPN() throws Exception{
		File targetFolder = new File(System.getProperty("user.dir"));
		GenerateGspn gspn = new GenerateGspn(((PetriNetDoc)result.getModel().get(0)).getNets().get(0),targetFolder, new ArrayList<EObject>());
		gspn.doGenerate(new BasicMonitor());
	}
}
