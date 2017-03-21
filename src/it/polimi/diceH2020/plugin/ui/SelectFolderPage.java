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

import java.io.File;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class SelectFolderPage extends WizardPage{
	private Composite container;
	private GridLayout layout;
	private Label folder;
	private boolean canFlip;
	private String selectedFolder;
	private Button browse;

	protected SelectFolderPage(String pageName) {
		super(pageName);
		canFlip=false;
	}

	@Override
	public void createControl(Composite arg0) {
		container = new Composite(arg0, SWT.NONE);
		layout = new GridLayout();
		container.setLayout(layout);
		browse = new Button(container, SWT.PUSH);
		browse.setLayoutData(new GridData(SWT.BEGINNING, SWT.END, false, false));
		browse.setText("Select Folder which contains the data...");
		folder = new Label(container, SWT.NONE);
		folder.setLayoutData(new GridData(SWT.BEGINNING, SWT.END, false, false));

		browse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			}
		});

		setPageComplete(false);
		this.setControl(container);
	}

	@Override
	public boolean canFlipToNextPage(){
		return this.canFlip;
	}

	public String getSelectedFolder(){
		return this.selectedFolder;
	}

	public void checkFolder(){
		File fold=new File(this.selectedFolder);

		for(File f:fold.listFiles()){
			if(f.getName().contains(".json")){
				this.folder.setText(this.selectedFolder);
				this.canFlip=true;
				this.browse.setText("Select another folder...");
				return;
			}
		}

		folder.setText("The chosen folder does not contain a json file");
		this.browse.setText("Select another folder...");
	}
}
