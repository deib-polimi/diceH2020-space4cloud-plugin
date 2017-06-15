package it.polimi.diceH2020.plugin.preferences;

import it.polimi.diceH2020.plugin.Activator;

public class Preferences {
	protected static final String SAVING_DIR = "SavingDir";
	protected static final String FRONT_END_ADDRESS = "FrontEndAddress";
	protected static final String FRONT_END_PORT = "FrontEndPort";
	protected static final String BACK_END_ADDRESS = "BackEndAddress";
	protected static final String BACK_END_PORT = "BackEndPort";

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
}
