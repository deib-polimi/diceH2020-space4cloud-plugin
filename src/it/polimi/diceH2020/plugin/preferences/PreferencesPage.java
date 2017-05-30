package it.polimi.diceH2020.plugin.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import it.polimi.diceH2020.plugin.Activator;

public class PreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencesPage() {
		super(GRID);
	}

	public void createFieldEditors() {
		addField(new StringFieldEditor(Preferences.FRONT_END_ADDRESS, "A &Front End address:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(Preferences.FRONT_END_PORT, "A &Front End port:", getFieldEditorParent()));
		addField(new StringFieldEditor(Preferences.BACK_END_ADDRESS, "A &Back End address:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(Preferences.BACK_END_PORT, "A &Back End port:", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Set the preferences for this plugin");
	}

}
