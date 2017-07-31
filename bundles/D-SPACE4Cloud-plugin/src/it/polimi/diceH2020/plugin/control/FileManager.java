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
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import it.polimi.diceH2020.SPACE4Cloud.shared.generators.ClassParametersGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.InstanceDataMultiProviderGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.JobMLProfileGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.JobMLProfilesMapGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.JobProfileGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.JobProfilesMapGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.PrivateCloudParametersGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.PublicCloudParametersGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.PublicCloudParametersMapGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.VMConfigurationsGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.generatorsDataMultiProvider.VMConfigurationsMapGenerator;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.ClassParameters;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.ClassParametersMap;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.InstanceDataMultiProvider;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.JobMLProfile;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.JobMLProfilesMap;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.JobProfile;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.JobProfilesMap;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.PrivateCloudParameters;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.PublicCloudParameters;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.PublicCloudParametersMap;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.SVRFeature;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.VMConfiguration;
import it.polimi.diceH2020.SPACE4Cloud.shared.inputDataMultiProvider.VMConfigurationsMap;
import it.polimi.diceH2020.plugin.preferences.Preferences;

/**
 * Contains all the methods related with file generation/transformation that we
 * weren't forced to put elsewhere
 * 
 * @author kom
 *
 */
public class FileManager {

	/**
	 * Renames files to be standard compliant and puts placeholder
	 * 
	 * @param cdid
	 *            ClassDesc id
	 * @param alt
	 *            Alternative name
	 * @param hadoopId
	 *            String be replaced
	 */
	public static void editFiles(int cdid, String alt, String hadoopId) {
		String savingDir = Preferences.getSavingDir();
		Configuration conf = Configuration.getCurrent();
		
		File folder = new File(savingDir + "tmp/");
		File files[] = folder.listFiles();
		String outputFilePath;
		
		File netFile = null;
		File defFile = null;
		
		for (File f : files) {
			if (f.getName().endsWith(".net")) {
				netFile = f;
			} else if (f.getName().endsWith(".def")) {
				defFile = f;
			}
		}
		
		if (Configuration.getCurrent().getIsPrivate()) {
			outputFilePath = savingDir + conf.getID() + "J" + cdid + "inHouse" + alt;
		} else {
			outputFilePath = savingDir + conf.getID() + "J" + cdid + alt.replaceAll("-", "");
		}
		
		String hup = String.valueOf(Configuration.getCurrent().getHup());
		
		System.out.println("Putting placeholder over " + defFile);
		SparkFileManager.putPlaceHolder("Ra{1-", "@@CORES@@", defFile);
		SparkFileManager.putPlaceHolder("sa{1-", "@@CONCURRENCY@@", defFile);
		SparkFileManager.putPlaceHolder("ia{1-", hup, defFile);
		
		
		System.out.println("Putting placeholder over " + netFile);
		SparkFileManager.putPlaceHolder(hadoopId, "@@CORES@@", netFile);
		
		SparkFileManager.moveFile(netFile, outputFilePath, "net");
		SparkFileManager.moveFile(defFile, outputFilePath, "def");

	}

	/**
	 * Builds the JSON representation of the current Configuration and
	 * eventually dumps it on a file.
	 */
	public static void generateInputJson() {
		Configuration conf = Configuration.getCurrent(); // TODO: REMOVE
		InstanceDataMultiProvider data = InstanceDataMultiProviderGenerator.build();

		data.setId(conf.getID());

		setMapJobProfile(data, conf);
		setMapClassParameters(data, conf);

		if (conf.getIsPrivate()) {

			data.setMapPublicCloudParameters(null);
			setPrivateParameters(data);

		} else {
			// Set MapVMConfigurations
			data.setMapVMConfigurations(null);
			data.setPrivateCloudParameters(null);

			if (conf.getHasLtc()) {
				setEtaR(data, conf);
			} else {
				data.setMapPublicCloudParameters(null);
			}
		}

		setMachineLearningProfile(data, conf);

		if (!Configuration.getCurrent().canSend()) {
			return;
		}

		// Generate Json
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());

		try {
			mapper.writerWithDefaultPrettyPrinter()
					.writeValue(new File(Preferences.getSavingDir() + conf.getID() + ".json"), data);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void setPrivateParameters(InstanceDataMultiProvider data) {
		PrivateCloudParameters pr = PrivateCloudParametersGenerator.build();
		pr.setE(PrivateConfiguration.getCurrent().getPriE());
		pr.setM(PrivateConfiguration.getCurrent().getPriM());
		pr.setN(PrivateConfiguration.getCurrent().getPriN());
		pr.setV(PrivateConfiguration.getCurrent().getPriV());
		data.setPrivateCloudParameters(pr);

		VMConfigurationsMap priMap = VMConfigurationsMapGenerator.build();

		Map<String, VMConfiguration> mapVMConfigurations = new HashMap<String, VMConfiguration>();
		for (VmClass v : PrivateConfiguration.getCurrent().getVmList()) {
			VMConfiguration vmConf = VMConfigurationsGenerator.build(2);
			vmConf.setCore(v.getCore());
			vmConf.setMemory(v.getMemory());
			vmConf.setCost(Optional.of(v.getCost()));
			mapVMConfigurations.put(v.getName(), vmConf);
		}
		priMap.setMapVMConfigurations(mapVMConfigurations);

		data.setMapVMConfigurations(priMap);
	}

	private static void setMapJobProfile(InstanceDataMultiProvider data, Configuration conf) {
		// Set MapJobProfile
		JobProfilesMap classdesc = JobProfilesMapGenerator.build();

		Map<String, Map<String, Map<String, JobProfile>>> classMap = new HashMap<>();

		for (ClassDesc c : conf.getClasses()) {
			Map<String, Map<String, JobProfile>> alternative = new HashMap<>();

			for (String alt : c.getAltDtsm().keySet()) {
				TreeMap<String, Double> profile = new TreeMap<>();
				String split[] = alt.split("-");

				JobProfile jp;
				if (conf.getTechnology().equals("Hadoop Map-reduce") || conf.getTechnology().equals("Spark")) {
					jp = JobProfileGenerator.build(c.getAltDtsmHadoop().get(alt).keySet().size() - 1);

					for (String par : c.getAltDtsmHadoop().get(alt).keySet()) {
						if (!par.equals("file")) {
							profile.put(par, Double.parseDouble(c.getAltDtsmHadoop().get(alt).get(par)));
						}
					}
				} else {
					jp = JobProfileGenerator.build(3); // TODO: how many
														// parameters do we
														// need?
					profile.put("datasize", 66.6);
					profile.put("mavg", 666.6);
					profile.put("mmax", 666.6);
				}

				jp.setProfileMap(profile);

				final String provider = Configuration.getCurrent().getIsPrivate() ? "inHouse" : split[0];
				final String vmType = Configuration.getCurrent().getIsPrivate() ? split[0] : split[1];

				Map<String, JobProfile> profilemap = new HashMap<>();
				profilemap.put(vmType, jp);

				alternative.merge(provider, profilemap, (oldValue, newValue) -> {
					oldValue.putAll(newValue);
					return oldValue;
				});
			}

			classMap.put(String.valueOf(c.getId()), alternative);
		}

		classdesc.setMapJobProfile(classMap);
		data.setMapJobProfiles(classdesc);
	}

	private static void setMapClassParameters(InstanceDataMultiProvider data, Configuration conf) {
		// Set MapClassParameter
		Map<String, ClassParameters> classdesc1 = new HashMap<String, ClassParameters>();

		if (conf.getTechnology().contains("Hadoop Map-reduce") || conf.getTechnology().contains("Spark")) {
			for (ClassDesc c : conf.getClasses()) {
				ClassParameters clpm = ClassParametersGenerator.build(c.getHadoopParUD().size());
				
				clpm.setD(Double.parseDouble(c.getHadoopParUD().get("d")));
				clpm.setHlow(Integer.parseInt(c.getHadoopParUD().get("hlow")));
				clpm.setHup(Integer.parseInt(c.getHadoopParUD().get("hup")));
				clpm.setThink(Double.parseDouble(c.getHadoopParUD().get("think")));
				clpm.setM(0.0);
				clpm.setV(1.0);
				
				if (Configuration.getCurrent().getIsPrivate()) {
					clpm.setPenalty(Double.parseDouble(c.getHadoopParUD().get("penalty")));
				}
				classdesc1.put(String.valueOf(c.getId()), clpm);
			}
		} else {
			for (ClassDesc c : conf.getClasses()) {
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

	private static void setEtaR(InstanceDataMultiProvider data, Configuration conf) {
		// Set PublicCloudParameters
		Map<String, Map<String, Map<String, PublicCloudParameters>>> classdesc2 = new HashMap<String, Map<String, Map<String, PublicCloudParameters>>>();
		for (ClassDesc c : conf.getClasses()) {
			Map<String, Map<String, PublicCloudParameters>> alternatives = new HashMap<String, Map<String, PublicCloudParameters>>();

			for (String alt : c.getAltDtsm().keySet()) {
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

	private static void setMachineLearningProfile(InstanceDataMultiProvider data, Configuration conf) {
		if (conf.getTechnology().contains("Hadoop Map-reduce") || conf.getTechnology().contains("Spark")) {
			Configuration.getCurrent().setCanSend(setMachineLearningHadoop(data));
		} else {
			// Set mapJobMLProfile - MACHINE LEARNING
			Map<String, JobMLProfile> jmlMap = new HashMap<String, JobMLProfile>();
			List<String> par = new ArrayList<String>();
			par.add("h");
			par.add("x");

			for (ClassDesc cd : conf.getClasses()) {
				JobMLProfile jmlProfile = JobMLProfileGenerator.build(par);
				jmlMap.put(String.valueOf(cd.getId()), jmlProfile);
			}

			JobMLProfilesMap jML = JobMLProfilesMapGenerator.build();
			jML.setMapJobMLProfile(jmlMap);
			data.setMapJobMLProfiles(jML);
		}
	}

	/**
	 * Builds the list of files to be sent to the web service
	 * 
	 * @return
	 */
	public static List<File> selectFiles() {
		File folder = new File(Preferences.getSavingDir());
		File files[] = folder.listFiles();
		List<File> toSend = new ArrayList<File>();

		for (File f : files) {
			if (f.getName().startsWith(Configuration.getCurrent().getID()) && !f.getName().contains("OUT")) {
				toSend.add(f);
			}
		}

		return toSend;
	}

	/**
	 * Generates the JSON file used to compose the result
	 */
	public static void generateOutputJson() {
		Map<Integer, String> map = new HashMap<Integer, String>();

		for (ClassDesc cd : Configuration.getCurrent().getClasses()) {
			map.put(cd.getId(), cd.getDdsmPath());
		}

		JSONObject json = new JSONObject(map);
		ObjectMapper mapper = new ObjectMapper();

		try {
			mapper.writerWithDefaultPrettyPrinter().writeValue(
					new File(Preferences.getSavingDir() + Configuration.getCurrent().getID() + "OUT.json"), json);
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
	 * 
	 * @param fileName
	 *            XML file path
	 * @return A map containing the parameters extracted from the model
	 */
	public static Map<String, String> parseDOMXmlFile(String fileName) {
		File src = new File(fileName);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Map<String, String> res = new HashMap<String, String>();

		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(src);
			NodeList group = doc.getElementsByTagName("group");

			for (int i = 0; i < group.getLength(); i++) {
				Element e = (Element) group.item(i);

				if (e.getAttribute("name").equals("Reducer")) {
					String data = group.item(i).getTextContent().trim();
					res.put("rTasks", data.substring(data.indexOf('[') + 1, data.indexOf(']')));
					res.put("rDemand", data.substring(data.indexOf('x') + 4, data.indexOf('u') - 1));
				}

				if (e.getAttribute("name").equals("Mapper")) {
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

	private static boolean setMachineLearningHadoop(InstanceDataMultiProvider data) {
		// Set mapJobMLProfile - MACHINE LEARNING
		Map<String, JobMLProfile> jmlMap = new HashMap<String, JobMLProfile>();
		JSONParser parser = new JSONParser();

		try {
			for (ClassDesc cd : Configuration.getCurrent().getClasses()) {
				Map<String, SVRFeature> map = new HashMap<String, SVRFeature>();

				Object obj = parser.parse(new FileReader(cd.getMlPath()));
				JSONObject jsonObject = (JSONObject) obj;
				JSONObject parameter = (JSONObject) jsonObject.get("mlFeatures");

				Map<String, String> toCheck = cd.getAltDtsmHadoop()
						.get(cd.getAltDtsmHadoop().keySet().iterator().next());
				for (String st : toCheck.keySet()) {
					if (!st.equals("file")) {
						if (!parameter.containsKey(st)) {
							JOptionPane.showMessageDialog(null,
									"Missing field in machine learning file: " + st + "\n" + "for class: " + cd.getId(),
									"Error: ", JOptionPane.ERROR_MESSAGE);
							return false;
						}
					}
				}

				double b = (double) jsonObject.get("b");
				double mu_t = (double) jsonObject.get("mu_t");
				double sigma_t = (double) jsonObject.get("sigma_t");

				if (!parameter.containsKey("x")) {
					JOptionPane.showMessageDialog(null,
							"Missing field in machine learning file: " + "x " + "\n" + "for class: " + cd.getId(),
							"Error: ", JOptionPane.ERROR_MESSAGE);
					return false;
				}

				if (!parameter.containsKey("h")) {
					JOptionPane.showMessageDialog(null,
							"Missing field in machine learning file: " + "h" + "\n" + "for class: " + cd.getId(),
							"Error: ", JOptionPane.ERROR_MESSAGE);
					return false;
				}

				Iterator<?> iterator = parameter.keySet().iterator();
				while (iterator.hasNext()) {
					String key = (String) iterator.next();

					if (!toCheck.containsKey(key) && !key.equals("x") && !key.equals("h"))
						continue;

					JSONObject locObj = (JSONObject) parameter.get(key);
					SVRFeature feat = new SVRFeature();
					feat.setMu((double) locObj.get("mu"));
					feat.setSigma((double) locObj.get("sigma"));
					feat.setW((double) locObj.get("w"));
					map.put(key, feat);
				}

				jmlMap.put(String.valueOf(cd.getId()), new JobMLProfile(map, b, mu_t, sigma_t));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		JobMLProfilesMap jML = JobMLProfilesMapGenerator.build();
		jML.setMapJobMLProfile(jmlMap);
		data.setMapJobMLProfiles(jML);

		return true;
	}

}
