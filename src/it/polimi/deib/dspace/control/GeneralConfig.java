package it.polimi.deib.dspace.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GeneralConfig {
	private static GeneralConfig currentConf;
	private String savingDir;
	private String serverID;
	private int timeToWait;
	public GeneralConfig(){
		
		loadConfiguration();
			}
	
	public static GeneralConfig getCurrent(){
		if (currentConf == null){
			currentConf = new GeneralConfig();
		}
		return currentConf;
	}
	public void setServerID(String serverId){
		this.serverID=serverId;
	}
	public String getServerID(){
		return this.serverID;
	}
	
	public String getSavingDir(){
		return this.savingDir;
	}
	public void setSavingDir(String savingDir){
		this.savingDir=savingDir;
	}
	public int getTime(){
		return this.timeToWait;
	}
	
	
	
	private void loadConfiguration(){
		String filePath="ConfigFile.txt";
		int defaultTime=10;
		String defaultPath="";
		File f = new File(filePath);
		if(!(f.exists())) { 
			this.timeToWait=defaultTime;
			this.serverID="http://specclient1.dei.polimi.it:8018/";
			Path currentRelativePath = Paths.get("");
			String s = currentRelativePath.toAbsolutePath().toString();
			this.savingDir=s;
			 PrintWriter writer;
			try {
				writer = new PrintWriter(filePath, "UTF-8");
				writer.println(serverID);
			    writer.println(Integer.toString(defaultTime));
			    writer.println(s);
			    writer.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			    
		}else{
			BufferedReader br=null;
			try {
				br = new BufferedReader(new FileReader(filePath));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
			    StringBuilder sb = new StringBuilder();
			    String line = br.readLine();

			    while (line != null) {
			        sb.append(line+"\n");
			        line = br.readLine();
			    }
			    String everything = sb.toString();
			    String[] sp=everything.split("\n");
			    this.serverID=sp[0];
			    this.timeToWait=Integer.parseInt(sp[1]);
			    this.savingDir=sp[2];
			    
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
			    try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
}
