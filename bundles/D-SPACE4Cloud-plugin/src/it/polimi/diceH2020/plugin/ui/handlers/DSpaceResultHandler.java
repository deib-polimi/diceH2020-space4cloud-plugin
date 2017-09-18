package it.polimi.diceH2020.plugin.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import it.polimi.diceH2020.plugin.preferences.Preferences;
import it.polimi.diceH2020.plugin.ui.ResultPage;

public class DSpaceResultHandler extends AbstractHandler{
	
	@Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
		ResultPage p = new ResultPage(new Shell());
		p.displayUrl(Preferences.getFrontEndUrl() + "resPub");
        return null;
    }

}
