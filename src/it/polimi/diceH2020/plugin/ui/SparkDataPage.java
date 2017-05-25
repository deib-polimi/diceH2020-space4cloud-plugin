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

public class SparkDataPage extends WizardPage{
	private Composite container;
	private GridLayout layout;
	private int thinkTime;
	private int hlow;
	private int hup;
	private Map<String,String> hadoopParUD;
	private double hadoopD;
	private Text thinkTextField,hlowTextField,hupTextField,hadoopDTextField;

	protected SparkDataPage(String pageName) {
		super("Select data for spark Technology");
		hadoopParUD=new HashMap<String,String>();
		setTitle(pageName);
		thinkTime=-1;
		hlow=-1;
		hup=-1;
		hadoopD=-1;
	}

	@Override
	public void createControl(Composite arg0) {
		container = new Composite(arg0, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns=1;
		container.setLayout(layout);

		Label l1 = new Label(container, SWT.None);
		l1.setText("Set Think Time");
		this.thinkTextField = new Text(container, SWT.BORDER);
		thinkTextField.setEditable(true);

		thinkTextField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				try{
					thinkTime=Integer.parseInt(thinkTextField.getText());
					hadoopParUD.put("think", thinkTextField.getText());
				}catch(NumberFormatException e){

				}
				getWizard().getContainer().updateButtons();
			}
		});

		Label l2 = new Label(container,SWT.None);
		l2.setText("Set deadline");
		this.hadoopDTextField=new Text(container,SWT.BORDER);
		hadoopDTextField.setEditable(true);
		hadoopDTextField.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent arg0) {
				try{
					hadoopD=Double.parseDouble(hadoopDTextField.getText());
					hadoopParUD.put("d", hadoopDTextField.getText());
				}catch(NumberFormatException e){

				}
				getWizard().getContainer().updateButtons();
			}
		});

		Label l3 = new Label(container,SWT.None);
		l3.setText("Set minimum level of concurrency");
		hlowTextField=new Text(container,SWT.BORDER);
		hlowTextField.setEditable(true);
		hlowTextField.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent arg0) {
				try{
					hlow=Integer.parseInt(hlowTextField.getText());
					hadoopParUD.put("hlow", hlowTextField.getText());
				}catch(NumberFormatException e){

				}
				getWizard().getContainer().updateButtons();
			}
		});

		Label l4 = new Label(container,SWT.None);
		l4.setText("Set maximum level of concurrency");
		hupTextField=new Text(container,SWT.BORDER);
		hupTextField.setEditable(true);
		hupTextField.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent arg0) {
				try{
					hup=Integer.parseInt(hupTextField.getText());
					hadoopParUD.put("hup", hupTextField.getText());
				}catch(NumberFormatException e){
				}
				getWizard().getContainer().updateButtons();
			}
		});

		setControl(container);
		setPageComplete(false);	
	}

	@Override
	public boolean canFlipToNextPage(){
		if(this.hadoopD!=-1&&this.thinkTime!=-1&&this.hlow!=-1&&this.hup!=-1){
			return true;
		}
		return false;
	}

	public int getThinkTime() {
		return thinkTime;
	}

	public void setThinkTime(int thinkTime) {
		this.thinkTime = thinkTime;
	}

	public int getHlow() {
		return hlow;
	}

	public double getHadoopD() {
		return hadoopD;
	}

	public int getHup() {
		return hup;
	}

	public Map<String,String> getHadoopParUD() {
		return hadoopParUD;
	}

	public void reset(){
		this.hadoopParUD.clear();
		thinkTime=-1;
		hlow=-1;
		hup=-1;
		hadoopD=-1;
		thinkTextField.setText("");
		hlowTextField.setText("");
		hupTextField.setText("");;
		hadoopDTextField.setText("");
	}
}
