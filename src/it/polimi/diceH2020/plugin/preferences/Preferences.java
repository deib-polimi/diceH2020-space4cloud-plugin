package it.polimi.diceH2020.plugin.preferences;

import it.polimi.diceH2020.plugin.Activator;

public class Preferences {
	protected static final String SAVING_DIR = "SavingDir";
	protected static final String FRONT_END_ADDRESS = "FrontEndAddress";
	protected static final String FRONT_END_PORT = "FrontEndPort";
	protected static final String BACK_END_ADDRESS = "BackEndAddress";
	protected static final String BACK_END_PORT = "BackEndPort";
	protected static final String JMT_PARSER = "JmtParser";
	protected static final String SIMULATOR = "Simulator";

	// The following string are public becuase you have to compare these strings
	// to the value of the simulator in order to know which simulator is used
	public static final String DAG_SIM = "dagSim";
	public static final String GSPN = "GSPN";
	public static final String JMT = "JMT";

	public static String getFrontEndAddress() {
		return Activator.getDefault().getPreferenceStore().getString(FRONT_END_ADDRESS);
	}

	public static int getFrontEndPort() {
		return Activator.getDefault().getPreferenceStore().getInt(FRONT_END_PORT);
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
}
