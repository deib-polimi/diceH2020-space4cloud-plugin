package it.polimi.diceH2020.plugin.control;

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
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.polimi.diceH2020.SPACE4Cloud.shared.generators.ClassParametersGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.InstanceDataMultiProviderGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.JobMLProfileGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.JobMLProfilesMapGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.JobProfileGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.JobProfilesMapGenerator;
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

/**
 * Contains all the methods related with file generation/transformation that we 
 * weren't forced to put elsewhere
 * @author kom
 *
 */
public class FileManager {
	private static FileManager fm;
	private String path;
	private String placeHolder = "@@CORES@@";

	private FileManager(){
		path = GeneralConfig.getCurrent().getSavingDir()+"/";; // to be replaced by fetching this info in tools
	}

	public static FileManager getInstance(){
		if(fm == null){
			fm = new FileManager();
		}
		return fm;
	}

	/**
	 * Renames files to be standard compliant and puts placeholder
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
				if(conf.getTechnology().equals("Hadoop"))
					putPlaceHolder("(starta ", f.getName(), "def");
				f.renameTo(new File(path + conf.getID() + "J" + cdid + alt.replaceAll("-", "") + ".def"));
				f.delete();
			}
			if(f.getName().endsWith(".net")){
				System.out.println("Renaming " + f.getName());
				putPlaceHolder(s, f.getName(), "net");
				f.renameTo(new File(path + conf.getID() + "J" + cdid + alt.replaceAll("-", "") + ".net"));
				f.delete();
			}
		}
	}

	/**
	 * Scans the input file and replaces id String with a placeholder
	 * @param id String to be replaced
	 * @param file Input file path
	 */
	public void putPlaceHolder(String id, String file, String ext){
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
			in.close();

			lines = s.split("\n");
			for (i=0; i< lines.length; i++){
				if(lines[i].contains(id)){
					words = ext.equals("net") ? lines[i].split(" ") : lines[++i].split("-");
					break;
				}
			}

			if (words == null){
				System.err.println("ID not found, couldn't put placehoder");
				return;
			}

			words[1] = ext.equals("net") ? "@@CORES@@" : "@@CONCURRENCY@@}";

			lines[i] = ext.equals("net") ? String.join(" ", words) : String.join("-", words);
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

	/**
	 * Builds the JSON representation of the current Configuration and eventually dumps it on a file.
	 */
	public void generateInputJson(){
		Configuration conf = Configuration.getCurrent(); //TODO: REMOVE
		InstanceDataMultiProvider data = InstanceDataMultiProviderGenerator.build();

		data.setId(conf.getID());

		setMapJobProfile(data, conf);
		setMapClassParameters(data,conf);

		if(!conf.getIsPrivate()){
			data.setPrivateCloudParameters(null);
			if(conf.getHasLtc()){
				setEtaR(data, conf);
			}
			else{
				data.setMapPublicCloudParameters(null);
			}
		}
		else{
			//TODO: private case
		}	
		setMachineLearningProfile(data, conf);
		//Set MapVMConfigurations
		data.setMapVMConfigurations(null);

		//Generate Json
		ObjectMapper mapper = new ObjectMapper();

		try {
			mapper.writerWithDefaultPrettyPrinter().writeValue(new File(path + conf.getID()+".json"), data);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setMapJobProfile(InstanceDataMultiProvider data, Configuration conf){
		//Set MapJobProfile
		JobProfilesMap classdesc = JobProfilesMapGenerator.build();

		Map<String,Map<String, Map<String, JobProfile>>> classMap = new HashMap<String,Map<String, Map<String, JobProfile>>>();

		for(ClassDesc c : conf.getClasses()){
			Map<String,Map<String, JobProfile>> alternative = new HashMap<String, Map<String, JobProfile>>();
			for (String alt: c.getAltDtsm().keySet()){

				TreeMap<String, Double> profile = new TreeMap<String, Double>();
				String split[] = alt.split("-");

				JobProfile jp;
				if(conf.getTechnology().equals("Hadoop")){
					jp = JobProfileGenerator.build(c.getAltDtsmHadoop().get(alt).keySet().size()-1);
					for(String par : c.getAltDtsmHadoop().get(alt).keySet()){
						if(!par.equals("file")){
							profile.put(par, Double.parseDouble(c.getAltDtsmHadoop().get(alt).get(par)));
						}
					}
				}
				else{
					jp = JobProfileGenerator.build(3); //TODO: how many parameters do we need?
					profile.put("datasize", 66.6);
					profile.put("mavg", 666.6);
					profile.put("mmax", 666.6);
				}

				jp.setProfileMap(profile);


				Map<String,JobProfile> profilemap = new HashMap<String,JobProfile>();
				profilemap.put(split[1], jp);

				alternative.put(split[0], profilemap);
			}
			classMap.put(String.valueOf(c.getId()), alternative);
			//classdesc.put(String.valueOf(c.getId()), alternatives);
		}
		classdesc.setMapJobProfile(classMap);
		data.setMapJobProfiles(classdesc);
	}

	private void setMapClassParameters(InstanceDataMultiProvider data, Configuration conf) {
		//Set MapClassParameter
		Map<String, ClassParameters> classdesc1 = new HashMap<String, ClassParameters>();
		if(conf.getTechnology().contains("Hadoop")){
			for(ClassDesc c : conf.getClasses()){
				ClassParameters clpm = ClassParametersGenerator.build(c.getHadoopParUD().size());
				clpm.setD(Double.parseDouble(c.getHadoopParUD().get("d")));
				clpm.setHlow(Integer.parseInt(c.getHadoopParUD().get("hlow")));
				clpm.setHup(Integer.parseInt(c.getHadoopParUD().get("hup")));
				clpm.setThink(Double.parseDouble(c.getHadoopParUD().get("think")));
				classdesc1.put(String.valueOf(c.getId()), clpm);
			}
		}else{
			for(ClassDesc c : conf.getClasses()){
				ClassParameters clpm = ClassParametersGenerator.build(7);
				clpm.setD(c.getStormU());
				clpm.setPenalty(6.0);
				clpm.setThink(10000.0);
				clpm.setHlow(1);
				clpm.setHup(1);
				clpm.setM(6.0);
				clpm.setV(0.0);
				classdesc1.put(String.valueOf(c.getId()), clpm);
			}
		}
		data.setMapClassParameters(new ClassParametersMap(classdesc1));
	}

	private void setEtaR(InstanceDataMultiProvider data, Configuration conf) {
		//Set PublicCloudParameters
		Map<String,Map<String,Map<String, PublicCloudParameters>>> classdesc2 = new HashMap<String,Map<String,Map<String, PublicCloudParameters>>>();
		for(ClassDesc c : conf.getClasses()){
			Map<String,Map<String, PublicCloudParameters>> alternatives = new HashMap<String,Map<String, PublicCloudParameters>>();
			for (String alt: c.getAltDtsm().keySet()){
				String split[] = alt.split("-");

				PublicCloudParameters params = PublicCloudParametersGenerator.build(2);
				params.setR(conf.getR());
				params.setEta(conf.getSpsr());

				Map<String, PublicCloudParameters> size = new HashMap<String, PublicCloudParameters>();
				size.put(split[1], params);

				alternatives.put(split[0], size);
			}
			classdesc2.put(String.valueOf(c.getId()), alternatives);
		}

		PublicCloudParametersMap pub = PublicCloudParametersMapGenerator.build();
		pub.setMapPublicCloudParameters(classdesc2);

		data.setMapPublicCloudParameters(pub);
	}

	private void setMachineLearningProfile(InstanceDataMultiProvider data, Configuration conf) {
		//Set mapJobMLProfile - MACHINE LEARNING
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

		
	}

	public String getPath(){
		return path;
	}

	/**
	 * Builds the list of files to be sent to the web service
	 * @return
	 */
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

	/**
	 * Generates the JSON file used to compose the result
	 */
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

	/**
	 * Scans Hadoop model to extract information
	 * @param fileName XML file path
	 * @return A map containing the parameters extracted from the model 
	 */
	public Map<String, String> parseDOMXmlFile(String fileName){
		File src = new File(fileName);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Map<String, String> res = new HashMap<String, String>();
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(src);
			NodeList group = doc.getElementsByTagName("group");

			for(int i = 0; i < group.getLength(); i++){
				Element e = (Element)group.item(i);
				if(e.getAttribute("name").equals("Reducer")){
					String data = group.item(i).getTextContent().trim();
					res.put("rTasks", data.substring(data.indexOf('[') + 1, data.indexOf(']')));
					res.put("rDemand", data.substring(data.indexOf('x') + 4, data.indexOf('u') - 1));
				}
				if(e.getAttribute("name").equals("Mapper")){
					String data = group.item(i).getTextContent().trim();
					res.put("mTasks", data.substring(data.indexOf('[') + 1, data.indexOf(']')));
					res.put("mDemand", data.substring(data.indexOf('x') + 4, data.indexOf('u') - 1));
				}
			}
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
		return res;
	}

	//TODO: may be useless
	//	public Map<String, String> parseXmlFile(){
	//		Map<String, String> res = new HashMap<String, String>();
	//		SAXParserFactory factory = SAXParserFactory.newInstance();
	//		
	//		
	//		try {
	//			SAXParser saxParser = factory.newSAXParser();
	//			DefaultHandler handler = new DefaultHandler() {
	//
	//				boolean ntasks = false;
	//				boolean hostdemand = false;
	//				boolean population = false;
	//				
	//				public void startElement(String uri, String localName,String qName,
	//			                Attributes attributes) throws SAXException {
	//					//System.out.println("Start Element :" + qName);
	//
	//					if (qName.equalsIgnoreCase("ntasks")) {
	//						ntasks = true;
	//					}
	//					if(qName.equalsIgnoreCase("hadooppopulation")){
	//						population = true;
	//					}
	//					if(qName.equalsIgnoreCase("hostdemand")){
	//						hostdemand = true;
	//					}
	//				}
	//
	//				public void endElement(String uri, String localName,
	//					String qName) throws SAXException {
	//					//System.out.println("End Element :" + qName);
	//
	//				}
	//
	//				public void characters(char ch[], int start, int length) throws SAXException {
	//					String sp[];
	//					
	//					if (ntasks) {
	//						res.put("nTasks", new String(ch, start, length));
	////						String s = new String(ch, start, length);
	////						if (s.contains("hadoopPopulation")){
	////							sp = s.split(";");System.err.println("hadoopPopulation = " + 
	////									sp[0].substring(sp[0].indexOf('[') + 1, sp[0].indexOf(']') - 1));System.err.println("hadoopPopulation = " + sp[0].charAt(s.indexOf('=') + 2));
	////						}
	////						if (s.contains("nTasks")){
	////							sp = s.split(";");
	////							System.err.println("nTasks = " + 
	////									sp[0].substring(sp[0].indexOf('[') + 1, sp[0].indexOf(']')));
	////						}
	//						ntasks = false;
	//					}
	//					if(population){
	//						res.put("hadoopPopulation", new String(ch, start, length));
	//						population = false;
	//					}
	//					if(hostdemand){
	//						res.put("hostDemand", new String(ch, start, length));
	//						hostdemand = false;
	//					}
	//
	//				}
	//
	//			     };
	//			     
	//			     saxParser.parse("/home/kom/it.polimi.deib.dspace/input_models/hadoop/model_1_class.uml", handler);
	//
	//		} catch (ParserConfigurationException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		} catch (SAXException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		} catch (IOException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//		
	//		for(String i : res.keySet()){
	//			System.err.println(i + "\t" + res.get(i));
	//		}
	//		
	//		return res;
	//		
	//	}
}
