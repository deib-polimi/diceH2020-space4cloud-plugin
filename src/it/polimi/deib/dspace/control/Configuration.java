package it.polimi.deib.dspace.control;

import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class Configuration {
	private static Configuration currentConf;
	
	private Vector<ClassDesc> classes;
	private int numClasses;
	private boolean isPrivate = false;
	private String technology;
	private boolean hasLTC; //Long Term Contract already existing
	private String ID;
	private int r = -1;
	private float spsr = -1;
	private String serverID;
	private String savingDir;

	
	public Configuration(){
		classes = new Vector<ClassDesc>();
		ID = generateName();
	}
	
	public static Configuration getCurrent(){
		if (currentConf == null){
			currentConf = new Configuration();
		}
		return currentConf;
	}
	
	private String generateName() {
		return String.valueOf(ThreadLocalRandom.current().nextInt(1000, 9998 + 1));
	}
	
	public String getID(){
		return ID;
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
		System.out.println("Technology:\t"+technology);
		System.out.println(isPrivate ? "Private" : !hasLTC ? "No LTC" : "r: " + r + "\t" + "spsr: " + spsr);
		for (ClassDesc c : classes){
			System.out.println("Class: "+c.getId());
			System.out.println(" "+c.getDdsmPath());
			for(String alt : c.getAltDtsm().keySet()){
				System.out.println("\t"+alt+"\t"+c.getAltDtsm().get(alt));
			}
		}
	} //TODO replace with dump on JSON
	
	public void setTechnology(String tech){
		technology = tech;
	}
	
	public void setPrivate(boolean isPrivate){
		this.isPrivate = isPrivate;
	}
	
	public void setLTC(boolean hasLTC){
		this.hasLTC = hasLTC;
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
	public boolean getHasLtc(){
		return hasLTC;
	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}

	public float getSpsr() {
		return spsr;
	}

	public void setSpsr(float spsr) {
		this.spsr = spsr;
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
	public void reset(){
		this.classes.clear();
		ID = generateName();
	}
	
}
