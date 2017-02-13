package it.polimi.deib.dspace.control;

import java.util.HashMap;

public class ClassDesc {
	private final int id;
	private HashMap<String, String> altDdsm;
	private String dtsmPath;
	
	public ClassDesc(int id){
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public HashMap<String, String> getAltDdsm() {
		return altDdsm;
	}
	
	public void setAltDdsm(HashMap<String, String> altDdsm){
		this.altDdsm = new HashMap<String, String>(altDdsm);
	}
	
	public void setDtsmPath(String dtsmPath){
		this.dtsmPath = dtsmPath;
	}
	
	public String getDtsmPath(){
		return dtsmPath;
	}
}
