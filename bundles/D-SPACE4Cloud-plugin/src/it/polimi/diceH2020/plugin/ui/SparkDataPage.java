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
import it.polimi.diceH2020.plugin.preferences.Preferences;

public class SparkDataPage extends WizardPage {
    private Composite container;
    private GridLayout layout;
    private Label thinkLabel, hlowLabel, hupLabel, deadlineLabel, jobPenaltyLabel;
    private Text thinkText, hlowText, hupText, deadlineText, jobPenaltyText;
    
    // Spark Parameters
    private int thinkTime, hlow, hup, deadline;
    private float jobPenalty;
    private Map<String, String> parameters;
    private Configuration currentConf;
    
    protected SparkDataPage(String pageName) {
        super("Select data for Spark Technology");
        currentConf = Configuration.getCurrent();
        parameters = new HashMap<String, String>();
        setTitle(pageName);
        resetParameters();
    }
    
	@Override
    public void createControl(Composite arg0) {
    	
    	/*
		 * Create Control
		 */
    	
        container = new Composite(arg0, SWT.NONE);
        layout = new GridLayout();
        layout.numColumns = 1;
        container.setLayout(layout);
        
        thinkLabel = new Label(container, SWT.None);
        thinkLabel.setText("Set Think Time [s]");
        thinkText = new Text(container, SWT.BORDER);
        
        deadlineLabel = new Label(container, SWT.None);
        deadlineLabel.setText("Set deadline [s]");
        deadlineText = new Text(container, SWT.BORDER);
        deadlineText.setEditable(true);

        hlowLabel = new Label(container, SWT.None);
        hlowLabel.setText("Set minimum level of concurrency");
        hlowText = new Text(container, SWT.BORDER);
        
        hupLabel = new Label(container, SWT.None);
        hupLabel.setText("Set maximum level of concurrency");
        hupText = new Text(container, SWT.BORDER);
        
        jobPenaltyLabel = new Label(container, SWT.None);
        jobPenaltyLabel.setText("Set job penalty cost [$/job]");
        jobPenaltyText = new Text(container, SWT.BORDER);
		jobPenaltyText.setEditable(true);

        if (Preferences.simulatorIsDAGSIM()){
        	
        	thinkText.setEnabled(true);
        	thinkText.setEditable(true);
        	
        	hlowText.setEnabled(true);
        	hupText.setEnabled(true);
        	hlowText.setEditable(true);
        	hupText.setEditable(true);
        }
        else {
        	
        	thinkText.setEnabled(false);
        	hlowText.setEnabled(false);
        	hupText.setEnabled(false);
        	
            hlowText.setText("1");
            hupText.setText("1");
            
            hlow = 1;
            hup = 1; 
        }
        
        /*
		 * Listeners
		 */
    	
        thinkText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent arg0) {
            	if (!thinkText.getText().isEmpty())
            		thinkTime = Integer.parseInt(thinkText.getText()) * 1000;
            	getWizard().getContainer().updateButtons();
            }
        });
        
        deadlineText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent arg0) {
            	if (!deadlineText.getText().isEmpty())
            		deadline = Integer.parseInt(deadlineText.getText()) * 1000; 
            	getWizard().getContainer().updateButtons();
            }
        });
        
        hlowText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent arg0) {
            	if (!hlowText.getText().isEmpty())
            		hlow = Integer.parseInt(hlowText.getText());
            	getWizard().getContainer().updateButtons();
            }
        });
        
        hupText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent arg0) {
            	if (!hupText.getText().isEmpty())
            		hup = Integer.parseInt(hupText.getText());
            	getWizard().getContainer().updateButtons();
            }
        });
        
        jobPenaltyText.addModifyListener(new ModifyListener() {

        	@Override
        	public void modifyText(ModifyEvent arg0) {
        		try {
        			if (!jobPenaltyText.getText().isEmpty()){
        				jobPenalty = Float.parseFloat(jobPenaltyText.getText());
        				setErrorMessage(null);
        			}
        		}
        		catch (NumberFormatException e){ 
        			setErrorMessage("Incorrect Job Penalty cost");
        			jobPenaltyText.setFocus();
        		}
                getWizard().getContainer().updateButtons();
            }
         });
        
        
        thinkText.addListener (SWT.Verify, e -> {
			String string = e.text;
			char [] chars = new char [string.length ()];
			string.getChars (0, chars.length, chars, 0);
			for (int i=0; i<chars.length; i++) {
				if (!(('0' <= chars [i] && chars [i] <= '9'))) {
					e.doit = false;
					return;
				}
			}
		});
        
        deadlineText.addListener (SWT.Verify, e -> {
			String string = e.text;
			char [] chars = new char [string.length ()];
			string.getChars (0, chars.length, chars, 0);
			for (int i=0; i<chars.length; i++) {
				if (!(('0' <= chars [i] && chars [i] <= '9'))) {
					e.doit = false;
					return;
				}
			}
		});
        
        hupText.addListener (SWT.Verify, e -> {
			String string = e.text;
			char [] chars = new char [string.length ()];
			string.getChars (0, chars.length, chars, 0);
			for (int i=0; i<chars.length; i++) {
				if (!(('0' <= chars [i] && chars [i] <= '9'))) {
					e.doit = false;
					return;
				}
			}
		});
        
        hlowText.addListener (SWT.Verify, e -> {
			String string = e.text;
			char [] chars = new char [string.length ()];
			string.getChars (0, chars.length, chars, 0);
			for (int i=0; i<chars.length; i++) {
				if (!(('0' <= chars [i] && chars [i] <= '9'))) {
					e.doit = false;
					return;
				}
			}
		});
        
        jobPenaltyText.addListener (SWT.Verify, e -> {
			String string = e.text;
			char [] chars = new char [string.length ()];
			string.getChars (0, chars.length, chars, 0);
			for (int i=0; i<chars.length; i++) {
				if (!(('0' <= chars [i] && chars [i] <= '9') || chars[i] == '.')) {
					e.doit = false;
					return;
				}
			}
		});
        
        setControl(container);
        setPageComplete(false);
    }
	
   @Override
   public boolean canFlipToNextPage() {

      if (deadline > 0 && thinkTime > 0 && hup > 0 && hlow > 0 && hlow <= hup){

         if (Configuration.getCurrent().isPrivate()) {

            if (Configuration.getCurrent().hasAdmissionControl() && jobPenalty == -1)
               return false;

            if (!Configuration.getCurrent().hasAdmissionControl() && hlow != hup) {
               setErrorMessage("Minimum and maximum level of concurrency must coincide");
               return false;
            }

            setErrorMessage(null);
            setParameters();
            return true;
         }
         else {
            // Public
            if (hlow != hup) {
               setErrorMessage("Minimum and maximum level of concurrency must coincide");
               return false;
            }
            setParameters();
            return true;
         }
      }

      return false;
   }
    
	public Map<String, String> getParameters() {
		return parameters;
	}
	
	public void setParameters(){
		
		parameters.put("d", Integer.toString(deadline));
		parameters.put("hlow", Integer.toString(hlow));
		parameters.put("hup", Integer.toString(hup));
		parameters.put("think", Integer.toString(thinkTime));
		
		if (currentConf.isPrivate())
			parameters.put("penalty", Float.toString(jobPenalty));
	}
    	
   
    public void reset() {
        resetParameters();
        
        if (Preferences.simulatorIsDAGSIM()){
        	hlowText.setText("");
            hupText.setText("");
            thinkText.setText("");
        }
        
        jobPenaltyText.setText("");
        deadlineText.setText("");
        
        if (Configuration.getCurrent().isPrivate())
            privateCase();
        else 
            publicCase();
    }
    
    private void resetParameters() {
    	deadline = -1;
        jobPenalty = -1;
    	if (Preferences.simulatorIsDAGSIM()){
    		thinkTime = -1;
    		hlow = -1;
            hup = -1;
    	}
    	else {
    		hlow = 1;
            hup = 1;
    	}
    }
    
    public void setThinkTime(String thinkTimeInput){
    	
    	thinkText.setText(thinkTimeInput);
		thinkText.setEnabled(false);
		thinkText.setEditable(false);
		
		if (thinkTimeInput.equals("0")){
			thinkTime = 1;		 
		}
		else {
			System.out.println(thinkTimeInput);
			thinkTime = Integer.parseInt(thinkTimeInput) * 1000;
		}	
		return;
	}

    public void publicCase() {
    	jobPenaltyLabel.setVisible(false);
    	jobPenaltyText.setVisible(false);
    }
    
	public void privateCase() {
		jobPenaltyLabel.setVisible(true);
    	jobPenaltyText.setVisible(true);
    	
		if (Configuration.getCurrent().getScenario().getAdmissionControl() == false){
			jobPenaltyText.setEditable(false);
			jobPenaltyText.setEnabled(false);
		}
	}
	
}
