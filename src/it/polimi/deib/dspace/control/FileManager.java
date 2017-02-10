package it.polimi.deib.dspace.control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import com.fasterxml.jackson.databind.ser.impl.FilteredBeanPropertyWriter;

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
	
	public void renameFiles(ClassDesc cd){
		Configuration conf = Configuration.getCurrent();
		File folder = new File(path);
		File files[] = folder.listFiles();
		
		for(File f : files){
			if(f.getName().endsWith(".def") && !f.getName().startsWith(conf.getID())){
				f.renameTo(new File(path+conf.getID()+"J"+cd.getId()+".def"));
				continue;
			}
			if(f.getName().endsWith(".net") && !f.getName().startsWith(conf.getID())){
				f.renameTo(new File(path+conf.getID()+"J"+cd.getId()+".net"));
			}
		}
	}
	
	public void putPlaceHolder(String id, String file){
		File f = new File(path + file);
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
	
	public String getPath(){
		return path;
	}

}
