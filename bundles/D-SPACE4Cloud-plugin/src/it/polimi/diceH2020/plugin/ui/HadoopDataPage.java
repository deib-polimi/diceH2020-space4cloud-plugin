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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import it.polimi.diceH2020.plugin.control.Configuration;

public class HadoopDataPage extends WizardPage {
	private Composite container;
	private GridLayout layout;
	private int thinkTime;
	private int hlow;
	private int hup;
	private float penalty;
	Label l5;
	private Map<String, String> hadoopParUD;
	private double hadoopD;
	private Text thinkTextField, hlowTextField, hupTextField, hadoopDTextField, penaltyTextField;
	private Configuration conf = Configuration.getCurrent();

	protected HadoopDataPage(String pageName) {
		super("Select data for hadoop Technology");
		hadoopParUD = new HashMap<String, String>();
		setTitle(pageName);		
		resetParameters();
	}

	private void resetParameters() {
		thinkTime = -1;
		hlow = -1;
		hup = -1;
		penalty = -1;
		hadoopD = -1;
	}
	
	public void updateThinkTextField(){
		thinkTextField.setText(ClassPage.thinkTime);
		
		if (ClassPage.thinkTime.equals("0")){
			hadoopParUD.put("think", "1'");
			conf.setThinkTime(1);
		}
		else {
			hadoopParUD.put("think", ClassPage.thinkTime);
			conf.setThinkTime(Integer.parseInt(ClassPage.thinkTime));
		}
		return;
	}

	@Override
	public void createControl(Composite arg0) {
		
		container = new Composite(arg0, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);

		Label l1 = new Label(container, SWT.None);
		l1.setText("Set Think Time [ms]");
		this.thinkTextField = new Text(container, SWT.BORDER);
		thinkTextField.setEnabled(false);
			
		Label l2 = new Label(container, SWT.None);
		l2.setText("Set deadline [ms]");
		this.hadoopDTextField = new Text(container, SWT.BORDER);
		
		hadoopDTextField.setEditable(true);
		hadoopDTextField.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				try {
					hadoopD = Double.parseDouble(hadoopDTextField.getText());
					hadoopParUD.put("d", hadoopDTextField.getText());
					conf.setHadoopD(hadoopD);

				} catch (NumberFormatException e) {

				}
				getWizard().getContainer().updateButtons();
			}
		});

		Label l3 = new Label(container, SWT.None);
		l3.setText("Set minimum level of concurrency");
		hlowTextField = new Text(container, SWT.BORDER);
		

			hlowTextField.setEditable(true);
			hlowTextField.addModifyListener(new ModifyListener() {
	
				@Override
				public void modifyText(ModifyEvent arg0) {
					try {
						hlow = Integer.parseInt(hlowTextField.getText());
						hadoopParUD.put("hlow", hlowTextField.getText());
						conf.setHlow(hlow);
						
					} catch (NumberFormatException e) {
	
					}
					getWizard().getContainer().updateButtons();
				}
			});


		Label l4 = new Label(container, SWT.None);
		l4.setText("Set maximum level of concurrency");
		hupTextField = new Text(container, SWT.BORDER);
		
		
			hupTextField.setEditable(true);
			hupTextField.addModifyListener(new ModifyListener() {
	
				@Override
				public void modifyText(ModifyEvent arg0) {
					try {
						hup = Integer.parseInt(hupTextField.getText());
						hadoopParUD.put("hup", hupTextField.getText());
						conf.setHup(hup);
					} catch (NumberFormatException e) {
					}
					getWizard().getContainer().updateButtons();
				}
			});

		l5 = new Label(container, SWT.None);
		l5.setText("Set job penalty cost");
		penaltyTextField = new Text(container, SWT.BORDER);
		penaltyTextField.setEditable(true);
		penaltyTextField.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				try {
					penalty = Float.parseFloat(penaltyTextField.getText());
					hadoopParUD.put("penalty", penaltyTextField.getText());
				} catch (NumberFormatException e) {
				}
				getWizard().getContainer().updateButtons();
			}
		});

		setControl(container);
		setPageComplete(false);
	}

	@Override
	public boolean canFlipToNextPage() {
		if (hadoopD != -1 && thinkTime != -1 && hlow != -1 && hup != -1){
			if (Configuration.getCurrent().getIsPrivate() && penalty == -1) {
				return false;
			}
			return true;
		}
		return false;
	}

	public Map<String, String> getHadoopParUD() {
		return hadoopParUD;
	}

	public void reset() {
		this.hadoopParUD.clear();
		resetParameters();
		updateThinkTextField();
		hlowTextField.setText("");
		hupTextField.setText("");
		penaltyTextField.setText("");
		hadoopDTextField.setText("");
		if (Configuration.getCurrent().getIsPrivate()) {
			privateCase();
		} else {
			publicCase();
		}
	}

	public void privateCase() {
		l5.setVisible(true);
		penaltyTextField.setVisible(true);
	}

	public void publicCase() {
		l5.setVisible(false);
		penaltyTextField.setVisible(false);
	}
}
