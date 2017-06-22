package it.polimi.diceH2020.plugin.control;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import it.polimi.diceH2020.plugin.preferences.Preferences;

public class SparkFileManager {

	/**
	 * I substitute the placeholder in the word after id in the file
	 * 
	 * @param id
	 * @param placeholder
	 *            the word after id will be substituted with this value
	 * @param inputFilename
	 * @param outputFileName
	 */
	public static void putPlaceHolder(String id, String placeholder, File inputFile) {
		try {
			Path path = Paths.get(inputFile.getAbsolutePath());
			String content = new String(Files.readAllBytes(path));

			String regex = id + " \\d";
			String replacement = id + " " + placeholder;
			String finalContent = content.replaceAll(regex, replacement);

			try (PrintWriter out = new PrintWriter(inputFile)) {
				out.println(finalContent);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

		moveFile(defFile, outputFilePath, "def");

		System.out.println("UsersId: " + sparkIds.getUsers());
		System.out.println("devices2resourcesId: " + sparkIds.getDevices2resources());

		System.out.println("Putting placeholders over net file");

		putPlaceHolder(sparkIds.getDevices2resources(), "@@CORES@@", netFile);
		putPlaceHolder(sparkIds.getUsers(), "@@CORES@@", netFile);

		moveFile(netFile, outputFilePath, "net");

	}

	private static void moveFile(File InputFile, String outputFilePath, String extension) {
		Path input = Paths.get(InputFile.getAbsolutePath());
		Path output = Paths.get(outputFilePath + "." + extension);
		try {
			Files.move(input, output, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}