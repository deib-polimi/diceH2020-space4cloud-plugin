package it.polimi.diceH2020.plugin.preferences;

import java.io.File;
import java.nio.file.Paths;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import it.polimi.diceH2020.plugin.Activator;

public class PreferencesInitializer extends AbstractPreferenceInitializer {

	public PreferencesInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(Preferences.FRONT_END_ADDRESS, "localhost");
		store.setDefault(Preferences.FRONT_END_PORT, 8000);
		store.setDefault(Preferences.BACK_END_ADDRESS, "localhost");
		store.setDefault(Preferences.BACK_END_PORT, 8080);
		store.setDefault(Preferences.SAVING_DIR, Paths.get("").toAbsolutePath().toString() + File.separator);
		store.setDefault(Preferences.SIMULATOR, Preferences.GSPN);
	}

}