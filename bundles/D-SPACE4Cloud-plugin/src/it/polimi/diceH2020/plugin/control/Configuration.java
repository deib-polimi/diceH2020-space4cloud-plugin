/*
Copyright 2017 Arlind Rufi
Copyright 2017 Gianmario Pozzi
Copyright 2017 Giorgio Pea

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package it.polimi.diceH2020.plugin.control;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import it.polimi.diceH2020.SPACE4Cloud.shared.settings.*;
import it.polimi.diceH2020.plugin.preferences.Preferences;

/**
 * Holds current run information (i.e. classes list, parameters, technology)
 * @author kom
 *
 */
public class Configuration implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Configuration currentConfig;

	private int numClasses;
	
	private ArrayList<ClassDesc> classes;
	
	private Scenario scenario;
	
	private String ID;
	private int thinkTime;
	private int hlow;
	private int hup;
	private double hadoopD;
	private int numM;
	private int numR;
	private int population;
	private double stormU;
	private  boolean canSend;

	private float spotRatio = -1;

	public Configuration(){
		classes = new ArrayList<ClassDesc>();
		ID = generateName();
		canSend = true;
	}

	public static Configuration getCurrent(){
		if (currentConfig == null){
			currentConfig = new Configuration();
		}
		return currentConfig;
	}

	public String getID(){
		return ID;
	}
	
	private String generateName() {
		return String.valueOf(ThreadLocalRandom.current().nextInt(1000, 9998 + 1));
	}
	
	public void setScenario(Technology technology, CloudType cloudType, Boolean longTermContract, Boolean admissionControl){
		this.scenario = new Scenario(technology, cloudType, longTermContract, admissionControl);
	}
	
	public boolean isHadoop(){
		return currentConfig.scenario.getTechnology() == Technology.HADOOP;
	}
	
	public boolean isSpark(){
		return currentConfig.scenario.getTechnology() == Technology.SPARK;
	}
	
	public boolean isStorm(){
		return currentConfig.scenario.getTechnology() == Technology.STORM;
	}
	
	public boolean isPublic(){
		return currentConfig.scenario.getCloudType() == CloudType.PUBLIC;
	}
	
	public Scenario getScenario(){
		return scenario;
	}
	
	public boolean isPrivate(){
		return currentConfig.scenario.getCloudType() == CloudType.PRIVATE;
	}
	
	public boolean hasLTC(){
		return currentConfig.scenario.getLongTermCommitment();
	}
	
	public boolean hasAdmissionControl(){
		return currentConfig.scenario.getAdmissionControl();
	}
	
	public String getFilename(int cdid, String alt){
		String filename = "";
		
		if (currentConfig.isPrivate()) 
            filename = Preferences.getSavingDir() + currentConfig.getID() + "J" + cdid + "inHouse" + alt;
         else 
            filename = Preferences.getSavingDir() + currentConfig.getID() + "J" + cdid + alt.replaceAll("-", "");
      
		return filename;
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

	public boolean isComplete(){
		return(numClasses == classes.size() && numClasses > 0);
	}

	
	public float getSpotRatio(){
		return spotRatio;
	}
	
	public void setSpotRatio(float r){
		this.spotRatio = r;
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

	public boolean canSend(){
		return canSend;
	}

	public void setCanSend(boolean canS){
		this.canSend=canS;
	}

	public static void setConfiguration(Configuration conf) {
		currentConfig = conf;
	}    
}
