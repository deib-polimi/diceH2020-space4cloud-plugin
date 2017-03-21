package it.polimi.deib.dspace.control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.FilteredBeanPropertyWriter;

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
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.JobProfilesMap;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.PublicCloudParameters;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.PublicCloudParametersMap;

public class FileManager {
	private static FileManager fm;
	private String path;
	private String placeHolder = "@@CORES@@";
	
	private FileManager(){
		path = "/home/kom/eclipse/java-neon/eclipse/"; // to be replaced by fetching this info in tools
	}
	
	public static FileManager getInstance(){
		if(fm == null){
			fm = new FileManager();
		}
		return fm;
	}
	
	/**
	 * Rename files to be standard compliant and put placeholder
	 * @param cdid ClassDesc id
	 * @param alt Alternative name
	 * @param s String be replaced
	 */
	public void editFiles(int cdid, String alt, String s){
		Configuration conf = Configuration.getCurrent();
		File folder = new File(path+"tmp/");
		File files[] = folder.listFiles();
		for(File f : files){
			if(f.getName().endsWith(".def")){
				System.out.println("Renaming " + f.getName());
				f.renameTo(new File(path + conf.getID() + "J" + cdid + alt.replaceAll("-", "") + ".def"));
				f.delete();
			}
			if(f.getName().endsWith(".net")){
				System.out.println("Renaming " + f.getName());
				putPlaceHolder(s, f.getName());
				f.renameTo(new File(path + conf.getID() + "J" + cdid + alt.replaceAll("-", "") + ".net"));
				f.delete();
			}
		}
	}
	
	public void putPlaceHolder(String id, String file){
		File f = new File(path + "tmp/" + file);
		System.out.println("Putting placeholder over "+ id +" in file " + file);
		try {
			int i;
			String newLine;
			String s = "";
			String lines[];
			String words[] = null;
			BufferedReader in = new BufferedReader(new FileReader(f));
			
			newLine = in.readLine();
			while(newLine != null){
				s = s + "\n" + newLine;
				newLine = in.readLine();
			}
			
			lines = s.split("\n");
			for (i=0; i< lines.length; i++){
				if(lines[i].contains(id)){
					words = lines[i].split(" ");
					break;
				}
			}
			
			if (words == null){
				System.err.println("ID not found, couldn't put placehoder");
				return;
			}
			
			words[1] = placeHolder;
			
			lines[i] = String.join(" ", words);
			s = String.join("\n", lines);
			
			BufferedWriter out = new BufferedWriter(new FileWriter(f));
			out.write(s);
			
			out.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("File: "+f.getAbsolutePath()+ " not found!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void generateInputJson(){
		Configuration conf = Configuration.getCurrent(); //TODO: REMOVE
		InstanceDataMultiProvider data = InstanceDataMultiProviderGenerator.build();
		
		data.setId(conf.getID());
		
		//Set MapJobProfile
		Map classdesc = new HashMap<String,Map>();
		for(ClassDesc c : conf.getClasses()){
			Map alternatives = new HashMap<String,Map>();
			for (String alt: c.getAltDtsm().keySet()){
				//createTxtFiles(c,alt);
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
				for (String alt: c.getAltDtsm().keySet()){
					String split[] = alt.split("-");
					
					PublicCloudParameters params = PublicCloudParametersGenerator.build(2);
					params.setR(conf.getR());
					params.setEta(conf.getSpsr());
					
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
		Map<String, JobMLProfile> jmlMap = new HashMap<String, JobMLProfile>();
		List<String> par = new ArrayList<String>();
		par.add("h");
		par.add("x");
		
		for(ClassDesc cd : conf.getClasses()){
			JobMLProfile jmlProfile = JobMLProfileGenerator.build(par);
			jmlMap.put(String.valueOf(cd.getId()), jmlProfile);
		}
		
		JobMLProfilesMap jML = JobMLProfilesMapGenerator.build();
		jML.setMapJobMLProfile(jmlMap);
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
	
	public String getPath(){
		return path;
	}

	public List<File> selectFiles() {
		File folder = new File(path);
		File files[] = folder.listFiles();
		List<File> toSend = new ArrayList<File>();
		
		for(File f : files){
			if(f.getName().startsWith(Configuration.getCurrent().getID()) && !f.getName().contains("OUT")){
				toSend.add(f);
			}
		}
		
		return toSend;
	}

	public void generateOutputJson() {
		Map<Integer, String> map = new HashMap<Integer, String>();
		
		for(ClassDesc cd : Configuration.getCurrent().getClasses()){
			map.put(cd.getId(), cd.getDdsmPath());
		}
		
		JSONObject json = new JSONObject(map);
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			mapper.writerWithDefaultPrettyPrinter().writeValue(
					new File(Configuration.getCurrent().getID() + "OUT.json"), json);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Map<String, String> parseXmlFile(){
		Map<String, String> res = new HashMap<String, String>();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		
		
		try {
			SAXParser saxParser = factory.newSAXParser();
			DefaultHandler handler = new DefaultHandler() {

				boolean ntasks = false;
				boolean hostdemand = false;
				boolean population = false;
				
				public void startElement(String uri, String localName,String qName,
			                Attributes attributes) throws SAXException {
					//System.out.println("Start Element :" + qName);

					if (qName.equalsIgnoreCase("ntasks")) {
						ntasks = true;
					}
					if(qName.equalsIgnoreCase("hadooppopulation")){
						population = true;
					}
					if(qName.equalsIgnoreCase("hostdemand")){
						hostdemand = true;
					}
				}

				public void endElement(String uri, String localName,
					String qName) throws SAXException {
					//System.out.println("End Element :" + qName);

				}

				public void characters(char ch[], int start, int length) throws SAXException {
					String sp[];
					
					if (ntasks) {
						res.put("nTasks", new String(ch, start, length));
//						String s = new String(ch, start, length);
//						if (s.contains("hadoopPopulation")){
//							sp = s.split(";");System.err.println("hadoopPopulation = " + 
//									sp[0].substring(sp[0].indexOf('[') + 1, sp[0].indexOf(']') - 1));System.err.println("hadoopPopulation = " + sp[0].charAt(s.indexOf('=') + 2));
//						}
//						if (s.contains("nTasks")){
//							sp = s.split(";");
//							System.err.println("nTasks = " + 
//									sp[0].substring(sp[0].indexOf('[') + 1, sp[0].indexOf(']')));
//						}
						ntasks = false;
					}
					if(population){
						res.put("hadoopPopulation", new String(ch, start, length));
						population = false;
					}
					if(hostdemand){
						res.put("hostDemand", new String(ch, start, length));
						hostdemand = false;
					}

				}

			     };
			     
			     saxParser.parse("/home/kom/it.polimi.deib.dspace/input_models/hadoop/model_1_class.uml", handler);

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(String i : res.keySet()){
			System.err.println(i + "\t" + res.get(i));
		}
		
		return res;
		
	}
}
