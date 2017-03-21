/*
Copyright 2017 Arlind Rufi
Copyright 2017 Gianmario Pozzi
Copyright 2017 Giorgio Pea

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

package it.polimi.diceH2020.plugin.ui;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;

import it.polimi.diceH2020.plugin.control.EmbeddedBrowser;

public class ResultPage extends Dialog{

	private EmbeddedBrowser browser;
	private Shell shell;

	public ResultPage(Shell name) {
		super(name);
		shell=name;
	}

	public void displayUrl(String URL){
		browser=new EmbeddedBrowser(URL);
		browser.launch(shell);
		shell.setSize(800, 800);
		shell.open();	
	}
}
