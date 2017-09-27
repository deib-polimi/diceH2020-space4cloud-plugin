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

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import it.polimi.diceH2020.plugin.Activator;

public class PreferencesInitializer extends AbstractPreferenceInitializer {

	public PreferencesInitializer() {}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(Preferences.FRONT_END_ADDRESS, "localhost");
		store.setDefault(Preferences.FRONT_END_PORT, 8000);
		store.setDefault(Preferences.BACK_END_ADDRESS, "localhost");
		store.setDefault(Preferences.BACK_END_PORT, 8080);
		store.setDefault(Preferences.SAVING_DIR, ResourcesPlugin.getWorkspace().getRoot().getLocation().toString());
		store.setDefault(Preferences.JMT_PARSER, ResourcesPlugin.getWorkspace().getRoot().getLocation().toString()+"/PNML_Pre_processor");
		store.setDefault(Preferences.SIMULATOR, Preferences.GSPN);
	}
}
