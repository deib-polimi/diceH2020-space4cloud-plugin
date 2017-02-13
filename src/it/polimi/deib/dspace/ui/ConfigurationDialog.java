package it.polimi.deib.dspace.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class ConfigurationDialog extends Dialog {
	private Shell shell;
	private Text serverId;
	private String server;
	private Button save;
	private boolean changed;
	private final String filePath="configFile/ConfigFile.txt";
	
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
		serverId=new Text(shell,SWT.FILL);
		serverId.setText(server);
		serverId.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent arg0) {
				server=serverId.getText();
				changed=true;
			}
        });
		
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
			        sb.append(line);
			        sb.append(System.lineSeparator());
			        line = br.readLine();
			    }
			    String everything = sb.toString();
			    this.server=everything;
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
		    writer.close();
		} catch (IOException e) {
		   // do something
		}
	}
	
	
	
}
