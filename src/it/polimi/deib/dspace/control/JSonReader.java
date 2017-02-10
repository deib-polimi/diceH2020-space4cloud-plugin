package it.polimi.deib.dspace.control;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JSonReader {
	private String provider;
	private	Map<String,Long> classNumVM;
	private Map<String,String> classTypeVM;
	private List<String> classes;
	private Map<String,String> idClassUmlFile;
	public JSonReader(Map<String,String> idClassUmlFile){
		setProvider("");
		setClassNumVM(new HashMap<String,Long>());
		setClassTypeVM(new HashMap<String,String>());
		classes=new ArrayList<String>();
		this.idClassUmlFile=idClassUmlFile;
	}
	
	
	//Takes as input the file path of the JSon file 
	public void read(String filePath){
			
		 // read the json file
		FileReader reader;
		try {
			reader = new FileReader(filePath);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
			provider=(String)jsonObject.get("provider");
			JSONArray lang= (JSONArray) jsonObject.get("lstSolutions");
			Iterator<JSONObject> i = lang.iterator();
			// take each value from the json array separately
			while (i.hasNext()) {
				JSONObject innerObj = (JSONObject) i.next();
			  	String idClass=(String) innerObj.get("id");
			  	long numVm=(long) innerObj.get("numberVM");
			  	JSONObject jsonOb = (JSONObject) innerObj.get("typeVMselected");
			  	String type=(String) jsonOb.get("id");  
			  	this.classNumVM.put(idClass, numVm);
			  	this.classTypeVM.put(idClass,type);
			  	this.classes.add(idClass);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	   }

	private void writeUml(String filePath,String id){
try {	
			
		   File inputFile = new File(filePath);
	         DocumentBuilderFactory dbFactory 
	            = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(inputFile);
	        doc.getDocumentElement().normalize();
	        Element root=doc.getDocumentElement();
	        NodeList nodes=root.getElementsByTagName("DDSM:DdsmVm");
	        Node n=nodes.item(0);
	        NamedNodeMap atributes=n.getAttributes();
	        Node nodeAttrprov = atributes.getNamedItem("provider");
	 		nodeAttrprov.setTextContent(provider);
	        Node nodeAttrNumVm=atributes.getNamedItem("instances");
	        nodeAttrNumVm.setTextContent(this.classNumVM.get(id).toString());
	        Node nodeAttrType=atributes.getNamedItem("genericSize");
	        nodeAttrType.setTextContent(this.classTypeVM.get(id));
	      // write the content into xml file
	 		TransformerFactory transformerFactory = TransformerFactory.newInstance();
	 		Transformer transformer = transformerFactory.newTransformer();
	 		DOMSource source = new DOMSource(doc);
	 		StreamResult result = new StreamResult(new File(filePath));
	 		transformer.transform(source, result);
	         
		} catch (Exception e) {
	         e.printStackTrace();
	      }
		
	}
	
	public void write(){
		for(String id:this.classes){
			this.writeUml(this.idClassUmlFile.get(id),id);
		}
	}
	

	public String getProvider() {
		return provider;
	}



	public void setProvider(String provider) {
		this.provider = provider;
	}



	public Map<String,Long> getClassNumVM() {
		return classNumVM;
	}



	public void setClassNumVM(Map<String,Long> classNumVM) {
		this.classNumVM = classNumVM;
	}



	public Map<String,String> getClassTypeVM() {
		return classTypeVM;
	}



	public void setClassTypeVM(Map<String,String> classTypeVM) {
		this.classTypeVM = classTypeVM;
	}
	
}
