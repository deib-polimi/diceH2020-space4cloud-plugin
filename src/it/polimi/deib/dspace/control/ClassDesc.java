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
	
	//Hadoop-only parameters
	private Map<String, Map<String,String>> altDtsmHadoop; //Parameters from DTSM files
	private Map<String, String> hadoopParUD; //User defined parameters
	
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
		if(!Configuration.getCurrent().getTechnology().equals("Hadoop")){
			return null;
		}
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

	public Map<String, String> getHadoopPar() {
		if(!Configuration.getCurrent().getTechnology().equals("Hadoop")){
			return null;
		}
		return hadoopParUD;
	}

	public void setHadoopPar(Map<String, String> hadoopPar) {
		this.hadoopParUD = hadoopPar;
	}
 
}
