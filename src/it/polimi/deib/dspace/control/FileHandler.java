package it.polimi.deib.dspace.control;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.deib.dspace.net.NetworkManager;

public class FileHandler {
	private List<File> files;
	private File folder;
	private String scenario;
	
	public void setFolder(String path){
		folder=new File(path);
	}
	
	private void getFilesFromFolder(){
		this.files=new ArrayList<File>();
		for(File f:folder.listFiles()){
			files.add(f);
		} 
	}
	
	public void setScenario(String scenario){
		this.scenario = scenario;
	}
	
	public void sendFile(){
		this.getFilesFromFolder();
		try {
			NetworkManager.getInstance().sendModel(files, scenario);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setScenario(boolean isPrivate,boolean isELTC){
		if(!isPrivate){
			if(isELTC){
			}else{
			}
		}
		
	}

}
