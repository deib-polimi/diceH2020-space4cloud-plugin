/*
Copyright 2017 Marco Ieni

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
package it.polimi.diceH2020.plugin.preferences;

import it.polimi.diceH2020.plugin.Activator;
import java.util.concurrent.locks.ReentrantLock;

public class Preferences {
	protected static final String SAVING_DIR = "SavingDir";
	protected static final String FRONT_END_ADDRESS = "FrontEndAddress";
	protected static final String FRONT_END_PORT = "FrontEndPort";
	protected static final String BACK_END_ADDRESS = "BackEndAddress";
	protected static final String BACK_END_PORT = "BackEndPort";
	protected static final String DAG_SOLVER_ADDRESS = "DagSolverAddress";
	protected static final String DAG_SOLVER_PORT = "DagSolverPort";
	protected static final String JMT_PARSER = "JmtParser";
	protected static final String SIMULATOR = "Simulator";
	
	public static final String DAG_SIM = "dagSim";
	public static final String GSPN = "GSPN";
	public static final String JMT = "JMT";
	
	public static ReentrantLock resultsLock = new ReentrantLock();

	public static String getFrontEndAddress() {
		return Activator.getDefault().getPreferenceStore().getString(FRONT_END_ADDRESS);
	}

	public static int getFrontEndPort() {
		return Activator.getDefault().getPreferenceStore().getInt(FRONT_END_PORT);
	}

	public static String getDagSolverAddress() {
		return Activator.getDefault().getPreferenceStore().getString(DAG_SOLVER_ADDRESS);
	}
	
	public static int getDagSolverPort() {
		return Activator.getDefault().getPreferenceStore().getInt(DAG_SOLVER_PORT);
	}
	
	public static String getBackEndAddress() {
		return Activator.getDefault().getPreferenceStore().getString(BACK_END_ADDRESS);
	}

	public static int getBackEndPort() {
		return Activator.getDefault().getPreferenceStore().getInt(BACK_END_PORT);
	}

	public static String getFrontEndUrl() {
		return "http://" + getFrontEndAddress() + ":" + getFrontEndPort() + "/";
	}

	public static String getBackEndUrl() {
		return "http://" + getBackEndAddress() + ":" + getBackEndPort() + "/";
	}
	
	public static String getDagSolverUrl() {
		return "http://" + getDagSolverAddress() + ":" + getDagSolverPort() + "/";
	}

	public static String getSavingDir() {
		String savDir = Activator.getDefault().getPreferenceStore().getString(SAVING_DIR);
		if (!savDir.endsWith("/")) {
			savDir += "/";
		}
		return savDir;
	}

	public static String getJmTPath() {
		String savDir = Activator.getDefault().getPreferenceStore().getString(JMT_PARSER);
		if (!savDir.endsWith("/")) {
			savDir += "/";
		}
		return savDir;
	}

	public static String getSimulator() {
		return Activator.getDefault().getPreferenceStore().getString(SIMULATOR);
	}
	
	public static boolean simulatorIsGSPN(){
		return Preferences.getSimulator().equals(Preferences.GSPN);
	}
	
	public static boolean simulatorIsJMT(){
		return Preferences.getSimulator().equals(Preferences.JMT);
	}
	
	public static boolean simulatorIsDAGSIM(){
		return Preferences.getSimulator().equals(Preferences.DAG_SIM);
	}
	
	
}
