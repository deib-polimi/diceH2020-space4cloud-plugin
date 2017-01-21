package it.polimi.deib.dspace.control;


public class ClassDesc {
	private final int id;
	private String[] alternatives;
	private String ddsmPath;
	private String dtsmPath;
	
	public ClassDesc(int id){
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public String[] getAlternatives() {
		return alternatives;
	}
	
	public void setAlternatives(String[] alt){
		alternatives = alt;
	}
	
	public void setDdsmPath(String ddsmPath){
		this.ddsmPath = ddsmPath;
	}
	
	public String getDdsmPath(){
		return ddsmPath;
	}
	
	public void setDtsmPath(String dtsmPath){
		this.dtsmPath = dtsmPath;
	}
	
	public String getDtsmPath(){
		return dtsmPath;
	}
}
