package it.polimi.deib.dspace.control;

import java.io.File;

public class FileHandler {
	private File folder;
	public void setFolder(String path){
		folder=new File(path);
	}
	private void getFilesFromFolder(){
		folder.listFiles();
	}
	public void setScenario(String scenario){
	}
	
	public void sendFile(){
		this.getFilesFromFolder();
//		try {
//			NetworkManager.getInstance().sendModel(files, scenario);
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	public void setScenario(boolean isPrivate,boolean isELTC){
		if(!isPrivate){
			if(isELTC){
			}else{
			}
		}
		
	}

}
