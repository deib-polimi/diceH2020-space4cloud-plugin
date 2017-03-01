package it.polimi.deib.dspace.control;

import java.util.HashMap;
import java.util.Map;

/**
 * Class descriptor. Holds all information about a class (i.e. parameters, alternatives).
 * @author kom
 *
 */
public class ClassDesc {
	private final int id;
	private Map<String, String> altDtsm;
	private String ddsmPath;
	private Map<String, Map<String,String>> altDtsmHadoop;
	
	public ClassDesc(int id){
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public Map<String, String> getAltDtsm() {
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

	public Map<String, Map<String,String>> getAltDtsmHadoop() {
		return altDtsmHadoop;
	}
	
	/**
	 * Expands alternatives with given Hadoop parameters
	 * @param alt Alternative to expand with Hadoop params
	 * @param exp Map of Hadoop parameters
	 */
	public void expandAltDtsmHadoop(String alt, Map<String, String> exp){
		if (altDtsmHadoop == null){
			altDtsmHadoop = new HashMap<String, Map<String, String>>();
		}
		exp.put("file", altDtsm.get(alt));
		altDtsmHadoop.put(alt, exp);
	}
 
}
