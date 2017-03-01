package it.polimi.deib.dspace.control;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Holds current run information (i.e. classes list, parameters, technology)
 * @author kom
 *
 */
public class Configuration {
	private static Configuration currentConf;
	
	private ArrayList<ClassDesc> classes;
	private int numClasses;
	private boolean isPrivate = false;
	private String technology;
	private boolean hasLTC; //Long Term Contract already existing
	private String ID;
	private int thinkTime;
	private int hlow;
	private int hup;
	private double hadoopD;
	private int numM;
	private int numR;
	private int population;
	private double stormU;
	private int r = -1;
	private float spsr = -1;

	
	public Configuration(){
		classes = new ArrayList<ClassDesc>();
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

	public ArrayList<ClassDesc> getClasses() {
		return classes;
	}
	
	public ClassDesc getCurrentClass(){
		return classes.get(classes.size() - 1);
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
	
	//TODO: is this method any useful?
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

	public void reset(){
		this.classes.clear();
		ID = generateName();
	}

	public int getThinkTime() {
		return thinkTime;
	}

	public void setThinkTime(int thinkTime) {
		this.thinkTime = thinkTime;
	}

	public int getHlow() {
		return hlow;
	}

	public void setHlow(int hlow) {
		this.hlow = hlow;
	}

	public int getHup() {
		return hup;
	}

	public void setHup(int hup) {
		this.hup = hup;
	}

	public double getHadoopD() {
		return hadoopD;
	}

	public void setHadoopD(double d) {
		this.hadoopD = d;
	}

	public int getNumR() {
		return numR;
	}

	public void setNumR(int numR) {
		this.numR = numR;
	}

	public int getNumM() {
		return numM;
	}

	public void setNumM(int numM) {
		this.numM = numM;
	}

	public int getPopulation() {
		return population;
	}

	public void setPopulation(int population) {
		this.population = population;
	}

	public double getStormU() {
		return stormU;
	}

	public void setStormU(double stormU) {
		this.stormU = stormU;
	}
	
}
