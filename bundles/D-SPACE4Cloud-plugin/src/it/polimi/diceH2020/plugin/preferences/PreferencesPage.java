/*
Copyright 2017 Marco Ieni
Copyright 2017 Niccolò Raspa

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

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import it.polimi.diceH2020.plugin.Activator;

public class PreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public PreferencesPage() {
        super(GRID);
    }

    public void createFieldEditors() {
        addField(new StringFieldEditor(Preferences.FRONT_END_ADDRESS, "Front End address:", getFieldEditorParent()));
        addField(new IntegerFieldEditor(Preferences.FRONT_END_PORT, "Front End port:", getFieldEditorParent()));
        addField(new StringFieldEditor(Preferences.BACK_END_ADDRESS, "Back End address:", getFieldEditorParent()));
        addField(new IntegerFieldEditor(Preferences.BACK_END_PORT, "Back End port:", getFieldEditorParent()));
        addField(new StringFieldEditor(Preferences.DAG_SOLVER_ADDRESS, "dagSim Extension address:", getFieldEditorParent()));
        addField(new IntegerFieldEditor(Preferences.DAG_SOLVER_PORT, "dagSim Extension port:", getFieldEditorParent()));
        addField(new DirectoryFieldEditor(Preferences.SAVING_DIR, "Working Directory:", getFieldEditorParent()));
        addField(new DirectoryFieldEditor(Preferences.JMT_PARSER, "Path to PNML Preprocessor:", getFieldEditorParent()));
        
        addField(new RadioGroupFieldEditor(Preferences.SIMULATOR, "Simulator:", 1, 
        		 new String[][] { { Preferences.GSPN, Preferences.GSPN }, { Preferences.JMT, Preferences.JMT }, 
                                  { Preferences.DAG_SIM, Preferences.DAG_SIM } }, getFieldEditorParent()));}
 
    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription("Set the preferences for this plugin");
    }
}
