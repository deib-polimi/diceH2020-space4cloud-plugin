package it.polimi.diceH2020.plugin.control;

public class VmClass {
	private String name;
	private String provider="inHouse";
	private double core;
	private double cost;
	private double memory;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProvider() {
		return provider;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public double getCore() {
		return core;
	}
	public void setCore(double core) {
		this.core = core;
	}
	public double getMemory() {
		return memory;
	}
	public void setMemory(double memory) {
		this.memory = memory;
	}
	
	
	
	

}
