package it.polimi.diceH2020.plugin.control;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import it.polimi.diceH2020.plugin.preferences.Preferences;

public class SparkFileManager {

	public static Document getDocument(String filename) {
		File inputFile = new File(filename);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(inputFile);
		} catch (SAXException | ParserConfigurationException | IOException e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * Returns a list of Ids that cothe value of the attribute rule in a
	 * given trc file
	 * 
	 * @param rule
	 *            the value of the attribute rule of the field that you want to
	 *            find the id
	 * @param filename
	 *            the name of the trc file
	 * @return
	 */
	public static List<String> getIdsFromTrc(String rule, String filename) {
		Document doc = getDocument(filename);
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr;
		NodeList nl = null;
		try {
			expr = xpath.compile("//traces[@rule=\"" + rule + "\"]");
			nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> ids = new ArrayList<>();
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			Element e1 = (Element) e.getElementsByTagName("toAnalyzableElement").item(0);
			String href = e1.getAttribute("href");
			String[] href_list = href.split("#");
			ids.add(href_list[1]);
		}
		return ids;
	}

	/**
	 * returns the first id that corresponds to a transaction in the pnml file
	 * among all the candidateIds
	 * 
	 * @param candidateIds
	 * @return the value of the transition id found. It will be one among the
	 *         candidateIds
	 */
	public static String getTransitionId(List<String> candidateIds, String filename) {
		Document doc = getDocument(filename);
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr;
		NodeList nl = null;
		for (String id : candidateIds) {
			try {
				expr = xpath.compile("//transition[@id=\"" + id + "\"]");
				nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
				if (nl.getLength() > 0) {
					return id;
				}
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void putPlaceHolders(String[] ids, String placeholder, String inputFilename, String outputFileName) {
		try {
			// I copy the content of the file in the String content
			String content = new String(Files.readAllBytes(Paths.get(inputFilename)));
			String[] lines = content.split("\n"); // I get the lines of the file
			String line;
			for (int i = 0; i < lines.length; i++) {
				line = lines[i];

				// if one among the ids is contained inside the line
				if (Arrays.stream(ids).parallel().anyMatch(line::contains)) {
					String[] words = line.split(" ");
					words[1] = placeholder;
					lines[i] = String.join(" ", words) + " "; // reunite the
																// line
				}

			}
			String finalContent = String.join("\n", lines); // reunite the file
															// content
			try (PrintWriter out = new PrintWriter(outputFileName)) {
				out.println(finalContent);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * I substitute the placeholder in the word after id in the file
	 * 
	 * @param id
	 * @param placeholder
	 *            the word after id will be substituted with this value
	 * @param inputFilename
	 * @param outputFileName
	 */
	public static void putPlaceHolders(String id, String placeholder, String inputFilename, String outputFileName) {
		String[] ids = { id };
		putPlaceHolders(ids, placeholder, inputFilename, outputFileName);
	}

	public static void editFiles(int cdid, String alt, SparkIds sparkIds) {
		String savingDir = Preferences.getSavingDir();
		Configuration conf = Configuration.getCurrent();
		File folder = new File(savingDir + "tmp/");
		File files[] = folder.listFiles();
		String outputFilePath;
		if (Configuration.getCurrent().getIsPrivate()) {
			outputFilePath = savingDir + conf.getID() + "J" + cdid + "inHouse" + alt;
		} else {
			outputFilePath = savingDir + conf.getID() + "J" + cdid + alt.replaceAll("-", "");
		}
		File netFile = null;
		File defFile = null;
		for (File f : files) {
			if (f.getName().endsWith(".net")) {
				netFile = f;
			} else if (f.getName().endsWith(".def")){
				defFile = f;
			}
		}
		moveDefFile(defFile, outputFilePath);
		
		String pnmlFilePath = outputFilePath + ".pnml";
		
		// These Id must be replaced with @@CORES@@
		System.out.println("UsersId: " + sparkIds.getUsers());
		System.out.println("devices2resourcesId: " + sparkIds.getDevices2resources());
		
		// Find Id of the Last Transaction		
		String transitionId = getTransitionId(sparkIds.getNumberOfConcurrentUsers(), pnmlFilePath);
		System.out.println("Last Transition Id: " + transitionId);
		
		String[] idsToReplace = { sparkIds.getDevices2resources(), sparkIds.getUsers() };
		String netFilePath = outputFilePath + ".net";
		System.out.println("Putting placeholders over net file");
		putPlaceHolders(idsToReplace, "@@CORES@@", netFile.getAbsolutePath(), netFilePath);

		// I delete the file because I have a copy with placeholders in the savindDir
		netFile.delete();
	}

	private static void moveDefFile(File defFile, String outputFilePath) {
		Path input = Paths.get(defFile.getAbsolutePath());
		Path output = Paths.get(outputFilePath + ".def");
		try {
			Files.move(input, output, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
