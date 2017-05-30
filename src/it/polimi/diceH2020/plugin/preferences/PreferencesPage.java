package it.polimi.diceH2020.plugin.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
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
		addField(new StringFieldEditor(Preferences.FRONT_END_ADDRESS, "&Front End address:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(Preferences.FRONT_END_PORT, "&Front End port:", getFieldEditorParent()));
		addField(new StringFieldEditor(Preferences.BACK_END_ADDRESS, "&Back End address:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(Preferences.BACK_END_PORT, "&Back End port:", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(Preferences.SAVING_DIR, "&Directory where to save data:",
				getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Set the preferences for this plugin");
	}

}
