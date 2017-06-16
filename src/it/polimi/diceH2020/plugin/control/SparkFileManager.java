package it.polimi.diceH2020.plugin.control;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
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
			} else if (f.getName().endsWith(".def")) {
				defFile = f;
			}
		}
		moveDefFile(defFile, outputFilePath);

		// These Id must be replaced with @@CORES@@
		System.out.println("UsersId: " + sparkIds.getUsers());
		System.out.println("devices2resourcesId: " + sparkIds.getDevices2resources());

		// TODO Id of the Last Transaction not used
		System.out.println("Last Transition Id: " + sparkIds.getNumberOfConcurrentUsers());

		String[] idsToReplace = { sparkIds.getDevices2resources(), sparkIds.getUsers() };
		String netFilePath = outputFilePath + ".net";
		System.out.println("Putting placeholders over net file");
		putPlaceHolders(idsToReplace, "@@CORES@@", netFile.getAbsolutePath(), netFilePath);

		// I delete the file because I have a copy with placeholders in the
		// savindDir
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
