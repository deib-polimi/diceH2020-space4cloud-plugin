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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class StormDataPage extends WizardPage{
	private Composite container;
	private GridLayout layout;
	private double stormU;
	private Label errLabel;
	private Text stormUTextField;
	protected StormDataPage(String pageName) {
		super("Select data for Storm Technology");
		setTitle(pageName);
		stormU=-1;
	}

	@Override
	public void createControl(Composite arg0) {
		container = new Composite(arg0, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns=1;
		container.setLayout(layout);
		Label l1;
		l1 = new Label(container, SWT.None);
		l1.setText("Set Utilization threshhold (in %)");
		this.stormUTextField = new Text(container, SWT.BORDER);
		errLabel=new Label(container,SWT.None);
		errLabel.setText("Not acceptable value for utilization");
		errLabel.setVisible(false);
		stormUTextField.setEditable(true);

		stormUTextField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				try{
					stormU=(double)Integer.parseInt(stormUTextField.getText())/100;
					System.out.println(stormU);
					if(stormU>1||stormU<0){
						stormU=-1;
						errLabel.setVisible(true);
						
					}else{
						errLabel.setVisible(false);
					}
					
				}catch(NumberFormatException e){
					errLabel.setVisible(true);
					stormU=-1;
					errLabel.setText("Not acceptable value for utilization");
				}
				getWizard().getContainer().updateButtons();
			}

		});
		setControl(container);
		setPageComplete(false);	

	}

	@Override
	public boolean canFlipToNextPage(){
		if(stormU!=-1){
			return true;
		}
		return false;
	}

	public void reset(){
		this.stormU=-1;
		this.stormUTextField.setText("");
	}
	public double getStormU(){
		return this.stormU;
	}
}
