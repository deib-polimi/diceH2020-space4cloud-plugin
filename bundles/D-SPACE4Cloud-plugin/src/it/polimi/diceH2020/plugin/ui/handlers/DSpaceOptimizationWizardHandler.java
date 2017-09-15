package it.polimi.diceH2020.plugin.ui.handlers;

import java.nio.file.Paths;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import it.polimi.diceH2020.plugin.control.Configuration;
import it.polimi.diceH2020.plugin.control.DICEWrap;
import it.polimi.diceH2020.plugin.control.PrivateConfiguration;
import it.polimi.diceH2020.plugin.preferences.Preferences;
import it.polimi.diceH2020.plugin.ui.DSpaceWizard;

public class DSpaceOptimizationWizardHandler {
	/**
	 * The constructor.
	 */
	public DSpaceOptimizationWizardHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		char mode = '1'; // TODO this is just for testing faster. change to 2 or
							// 3 for taking for your filesystem a serialized
							// version of the configuration
		System.out.println("default dir: " + Paths.get("").toAbsolutePath().toString());
		System.out.println("saving dir: " + Preferences.getSavingDir());
		System.out.println("Simulator: " + Preferences.getSimulator());

		if (mode == '1') {
			Configuration.getCurrent().reset();
			PrivateConfiguration.getCurrent().clear();
			WizardDialog dialog = new WizardDialog(null, new DSpaceWizard());
			dialog.open();
			DICEWrap.getWrapper().start();
		} else if (mode == '2') {
			DICEWrap.trySparkFork1();
		} else if (mode == '3') {
			DICEWrap.trySparkFork2();
		}
		return null;
	}

}
