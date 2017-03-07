package it.polimi.diceH2020.plugin.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import it.polimi.diceH2020.plugin.control.PrivateConfiguration;
import it.polimi.diceH2020.plugin.control.VmClass;

public class PrivateConfigPage extends WizardPage{
	private Composite container;
	private GridLayout layout;
	private Button addConfig;
	private Button removeConfig;
	private Button saveConfig;
	private Button loadConfig;
	private int classes = 0;
	private int alternatives;
	private Text nNodesText,cpuText,costNodeText,memNodeText;
	private List vmConfigsList;
	private int numNodes;
	private double cpuForNode;
	private double memForNode;
	private double costNode;
	private VmClass selectedVmConfig;
	
	protected PrivateConfigPage(String pageName) {
		super("Select cluster parameters");
		setTitle(pageName);
		numNodes=-1;
		cpuForNode=-1;
		memForNode=-1;
		costNode=-1;
	}

	@Override
	public void createControl(Composite arg0) {
		// TODO Auto-generated method stub
		container = new Composite(arg0, SWT.NONE);
		layout = new GridLayout();
		container.setLayout(layout);
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = true;
        //Creation
        Label nNodeLab=new Label(container,SWT.NONE);
        nNodeLab.setText("Set number of nodes");
        Label cpuLabel=new Label(container,SWT.NONE);
        cpuLabel.setText("Cpu per cluster");        
        nNodesText=new Text(container,SWT.BORDER);
        cpuText=new Text(container,SWT.BORDER);
        Label memLabel=new Label(container,SWT.NONE);
        memLabel.setText("Set memory per node");
        Label costLabel=new Label(container,SWT.NONE);
        costLabel.setText("Set cost per node");
        memNodeText=new Text(container,SWT.BORDER);
        costNodeText=new Text(container,SWT.BORDER);
        new Label(container,SWT.NONE);
        new Label(container,SWT.NONE);
        vmConfigsList=new List(container,SWT.BORDER);
        vmConfigsList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        vmConfigsList.add("something");
        addConfig=new Button(container,SWT.PUSH);
        addConfig.setText("Add new configuration");
        new Label(container,SWT.NONE);
        removeConfig=new Button(container,SWT.PUSH);
        removeConfig.setText("Remove configuration");
        new Label(container,SWT.NONE);
        new Label(container,SWT.NONE);
        new Label(container,SWT.NONE);
        GridLayout inLayout=new GridLayout();
        inLayout.numColumns=2;
        Composite butComp=new Composite(container,SWT.NONE);
        butComp.setLayout(inLayout);
        saveConfig=new Button(butComp,SWT.NONE);
        saveConfig.setText("Save");
        loadConfig=new Button(butComp,SWT.NONE);
        loadConfig.setText("Load");
        
        //Listeners
        
        
        nNodesText.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				try{
					numNodes=Integer.parseInt(nNodesText.getText());
					}catch(NumberFormatException e){
						numNodes=-1;
					}
				getWizard().getContainer().updateButtons();
			}
        });
        cpuText.addModifyListener(new ModifyListener(){
        	@Override
        	public void modifyText(ModifyEvent arg0){
        		try{
        			cpuForNode=Double.parseDouble(cpuText.getText());
        		}catch(NumberFormatException e){
        			cpuForNode=-1;
        		}
        		getWizard().getContainer().updateButtons();
        	}
        });
        
        memNodeText.addModifyListener(new ModifyListener(){
        	@Override
        	public void modifyText(ModifyEvent arg0){
        		try{
        			memForNode=Double.parseDouble(memNodeText.getText());
        		}catch(NumberFormatException e){
        			memForNode=-1;
        		}
        		getWizard().getContainer().updateButtons();
        	}
        });
        
        costNodeText.addModifyListener(new ModifyListener(){
        	@Override
        	public void modifyText(ModifyEvent arg0){
        		try{
        			costNode=Double.parseDouble(costNodeText.getText());
        		}catch(NumberFormatException e){
        			costNode=-1;
        		}
        		getWizard().getContainer().updateButtons();
        	}
        });
        
        
        addConfig.addSelectionListener(new SelectionListener(){
        	public void widgetSelected(SelectionEvent e) {
        		String name="";
        		double mem=0;
        		double cost=0;
        		double cores=0;
        		GridBagConstraints c = new GridBagConstraints();
        		JTextField coreField = new JTextField(5);
        	      JTextField memField = new JTextField(5);
        	      JTextField costField= new JTextField(5);
        	      JTextField nameField=new JTextField(5);
        	      JPanel myPanel = new JPanel();
        	      myPanel.add(new JLabel("Name of VM :"),c);
        	      myPanel.add(nameField,c);
        	      myPanel.add(new JLabel("Num Cores :"),c);
        	      myPanel.add(coreField);
        	      myPanel.add(new JLabel("Memory :"),c);
        	      myPanel.add(memField);
        	      myPanel.add(new JLabel("Cost :"),c);
        	      myPanel.add(costField);
        	      
        	      int result = JOptionPane.showConfirmDialog(null, myPanel, 
        	               "Please Enter VM parameters", JOptionPane.OK_CANCEL_OPTION);
        	      if (result == JOptionPane.OK_OPTION) {
        	    	  boolean canCreate=true;
        	    	  name=nameField.getText();
        	    	  try{
              			mem=Double.parseDouble(memField.getText());
              		}catch(NumberFormatException e1){
              			canCreate=false;
              		}
        	    	  try{
                			cores=Double.parseDouble(coreField.getText());
                		}catch(NumberFormatException e1){
                			canCreate=false;
                		}
        	    	  try{
                			cost=Double.parseDouble(costField.getText());
                		}catch(NumberFormatException e1){
                			canCreate=false;
                		}
        	    	  if(!name.isEmpty()&&canCreate){
        	    		  PrivateConfiguration.getCurrent().addVmConfig(new VmClass(name,cores,cost,mem));
        	    		  vmConfigsList.add(name);
        	    	  }else{
        	    		  JOptionPane.showConfirmDialog(null, "Some of the parameters are wrong.. VM not created");
        	    	  }
        	      
        	      }
        		
            }

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}
        	
        });
        
        
        removeConfig.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			//	vmConfigsList.remove(selectedVmConfig);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				
			}
        
        
        
        
        
        });
        
        
        
        
        
        
        
        
        
        
        setControl(container);
        setPageComplete(false);
	}
	
	
	
	

	public VmClass getVmList() {
		return selectedVmConfig;
	}

	private void populateList(){
		for(VmClass vm:PrivateConfiguration.getCurrent().getVmList()){
			vmConfigsList.add(vm.getName());
		}
	}
	@Override
	public boolean canFlipToNextPage(){
		if(this.memForNode!=-1&&this.costNode!=-1&&this.cpuForNode!=-1&&this.numNodes!=-1&&this.vmConfigsList.getItemCount()!=0){
			return true;
		}
		return false;
	}


}
