package it.polimi.deib.dspace.control;

import java.util.Vector;

public class Configuration {
	private static Configuration currentConf;
	
	private Vector<ClassDesc> classes;
	private int numClasses;
	private boolean isPrivate;
	private String technology;
	
	public Configuration(){
		classes = new Vector<ClassDesc>();
	}
	
	public static Configuration getCurrent(){
		if (currentConf == null){
			currentConf = new Configuration();
		}
		return currentConf;
	}

	public int getNumClasses() {
		return numClasses;
	}

	public void setNumClasses(int numClasses) {
		this.numClasses = numClasses;
	}

	public Vector<ClassDesc> getClasses() {
		return classes;
	}
	
	public ClassDesc getCurrentClass(){
		return classes.lastElement();
	}
	
	public void dump(){
		for (ClassDesc c : classes){
			System.out.println("Class: "+c.getId());
			System.out.println(" "+c.getDtsmPath());
			System.out.println(" "+c.getDdsmPath());
			for (String alt : c.getAlternatives()){
				System.out.println(" "+alt);
			}
		}
	}
	
	public void setTechnology(String tech){
		technology = tech;
	}
	
	public void setPrivate(boolean isPrivate){
		this.isPrivate = isPrivate;
	}
	
	public String getTechnology(){
		return technology;
	}
	
	public boolean getIsPrivate(){
		return isPrivate;
	}
	
	public boolean isComplete(){
		return(numClasses == classes.size() && numClasses > 0 && technology != null);
	}
}
