package it.polimi.diceH2020.plugin.control;

import java.util.ArrayList;
import java.util.List;

public class PrivateConfiguration {
	
	private static PrivateConfiguration current;
	private double priV;
	private double priE;
	private int priN;
	private double priM;
	private List<VmClass> vmList;
	public PrivateConfiguration(){
		vmList=new ArrayList<VmClass>();
	}
	
	public static PrivateConfiguration getCurrent(){
		if (current == null){
			current = new PrivateConfiguration();
		}
		return current;
	}
	
	
	
	
	public double getPriV() {
		return priV;
	}
	public void setPriV(double priV) {
		this.priV = priV;
	}
	public double getPriE() {
		return priE;
	}
	public void setPriE(double priE) {
		this.priE = priE;
	}
	public int getPriN() {
		return priN;
	}
	public void setPriN(int priN) {
		this.priN = priN;
	}
	public double getPriM() {
		return priM;
	}
	public void setPriM(double priM) {
		this.priM = priM;
	}

	public List<VmClass> getVmList() {
		return vmList;
	}

	public void addVmConfig(VmClass vmConf){
		this.vmList.add(vmConf);
	}
	public VmClass getVmFromName(String name){
		for(VmClass v:this.vmList){
			if(v.getName().equals(name)){
				return v;
			}
		}
		return null;
	}
	
	public void removeVmConfig(String name){
		vmList.remove(getVmFromName(name));
	}
	
	public void clear(){
		this.vmList.clear();
	}
	
	
	
}
