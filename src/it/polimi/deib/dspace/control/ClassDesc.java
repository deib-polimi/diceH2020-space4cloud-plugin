package it.polimi.deib.dspace.control;


public class ClassDesc {
	private final int id;
	private String[] alternatives;
	private String umlPath;
	
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
	
	public void setUmlPath(String umlPath){
		this.umlPath = umlPath;
	}
	
	public String getUmlPath(){
		return umlPath;
	}
}
