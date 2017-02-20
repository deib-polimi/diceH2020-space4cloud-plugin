package it.polimi.deib.dspace.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class ConfigurationDialog extends Dialog {
	private Shell shell;
	private Text serverId;
	private Text timeToCheck;
	private String savingDir;
	private String server;
	private Button save;
	private boolean changed;
	private int timeToWait;
	private final String filePath="ConfigFile.txt";
	private Button selSave;
	
	public ConfigurationDialog(Shell parent) {
		super(parent);
		// TODO Auto-generated constructor stub
		this.shell=parent;
		changed=false;
		server="";
	}
	public void setView(){
		GridLayout layout=new GridLayout();
		layout.numColumns=1;
		shell.setLayout(layout);
		Label l1=new Label(shell,SWT.FILL);
		l1.setText("Set server address:");
		serverId=new Text(shell,SWT.NONE);
		serverId.setText(server);
		serverId.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent arg0) {
				server=serverId.getText();
				changed=true;
			}
        });
		Label l2=new Label(shell,SWT.FILL);
		l2.setText("Set server address:");
		timeToCheck=new Text(shell,SWT.NONE);
		timeToCheck.setText(""+this.timeToWait);
		timeToCheck.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent arg0) {
				timeToWait=Integer.parseInt(timeToCheck.getText());
				changed=true;
			}
        });
		this.selSave = new Button(shell, SWT.PUSH);
		selSave.setLayoutData(new GridData(SWT.BEGINNING, SWT.END, false, false));
		selSave.setText("Select Folder where to save the data...");
		Label l3 = new Label(shell, SWT.NONE);
		selSave.setLayoutData(new GridData(SWT.BEGINNING, SWT.END, false, false));
		
		selSave.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {


            	JFileChooser j = new JFileChooser();
            	j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            	int choice = j.showOpenDialog(null);
            	
            	if (choice!= JFileChooser.APPROVE_OPTION) return;
            	savingDir=j.getSelectedFile().getAbsolutePath();
            	l3.setText(savingDir);
            	shell.update();
             }
        });
		new Label(shell,SWT.FILL);
		new Label(shell,SWT.FILL);
		new Label(shell,SWT.FILL);
		new Label(shell,SWT.FILL);
		new Label(shell,SWT.FILL);
		new Label(shell,SWT.FILL);
		new Label(shell,SWT.FILL);
		new Label(shell,SWT.FILL);
		new Label(shell,SWT.FILL);
		new Label(shell,SWT.FILL);
		new Label(shell,SWT.FILL);
		save=new Button(shell,SWT.PUSH);
		save.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            save();
            shell.close();
             }
		});
		save.setText("Save");
		save.setVisible(true);
		shell.setVisible(true);
	}
	
	public void load() {
		String defaultId="http://specclient1.dei.polimi.it:8018/";
		File f = new File(filePath);
		System.out.println(f.getAbsolutePath());
		if(!f.exists()) { 
			try{
			    PrintWriter writer = new PrintWriter(filePath, "UTF-8");
			    writer.println(defaultId);
			    writer.close();
			} catch (IOException e) {
			   // do something
			}
			this.server=defaultId;
		}else{
			BufferedReader br=null;
			System.out.println("here");
			try {
				br = new BufferedReader(new FileReader(filePath));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
			    StringBuilder sb = new StringBuilder();
			    String line = br.readLine();

			    while (line != null) {
			        sb.append(line+"\n");
			        line = br.readLine();
			    }
			    String everything = sb.toString();
			    String[] sp=everything.split("\n");
			    this.server=sp[0];
			    this.timeToWait=Integer.parseInt(sp[1]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
			    try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	
	}
	
	private void save(){
		if(!changed){
			return;
		}
		System.out.println(server);
		try{
		    PrintWriter writer = new PrintWriter(filePath, "UTF-8");
		    writer.println(this.server);
		   writer.println(this.timeToWait);
		   writer.println(this.savingDir);
		    writer.close();
		} catch (IOException e) {
		   // do something
		}
	}
	
	
	
}
