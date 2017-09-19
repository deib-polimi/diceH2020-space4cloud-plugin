/*
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
package it.polimi.diceH2020.plugin.ui.handlers;

import java.nio.file.Paths;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;

import it.polimi.diceH2020.plugin.control.Configuration;
import it.polimi.diceH2020.plugin.control.DICEWrap;
import it.polimi.diceH2020.plugin.control.PrivateConfiguration;
import it.polimi.diceH2020.plugin.preferences.Preferences;
import it.polimi.diceH2020.plugin.ui.DSpaceWizard;

public class DSpaceOptimizationWizardHandler extends AbstractHandler{
	
	@Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("default dir: " + Paths.get("").toAbsolutePath().toString());
		System.out.println("saving dir: " + Preferences.getSavingDir());
		System.out.println("Simulator: " + Preferences.getSimulator());
		
		Configuration.getCurrent().reset();
		PrivateConfiguration.getCurrent().clear();
		WizardDialog dialog = new WizardDialog(null, new DSpaceWizard());
		dialog.open();
		DICEWrap.getWrapper().start();
		
        return null;
    }

}
