package it.polimi.deib.dspace.control;

import java.util.HashMap;

public class ClassDesc {
	private final int id;
	private HashMap<String, String> altDtsm;
	private String ddsmPath;
	
	public ClassDesc(int id){
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public HashMap<String, String> getAltDtsm() {
		return altDtsm;
	}
	
	public void setAltDtsm(HashMap<String, String> altDtsm){
		this.altDtsm = new HashMap<String, String>(altDtsm);
	}
	
	public void setDdsmPath(String ddsmPath){
		this.ddsmPath = ddsmPath;
	}
	
	public String getDdsmPath(){
		return ddsmPath;
	}
}
