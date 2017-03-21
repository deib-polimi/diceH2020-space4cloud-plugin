package it.polimi.diceH2020.plugin.startup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.ui.IStartup;

import it.polimi.diceH2020.plugin.control.ResultCheck;

public class StartUpThread implements IStartup{
	private int time;
	ResultCheck check;
	@Override
	public void earlyStartup() {
		loadConfiguration();
		TimerTask timerTask = new ResultCheck("results");
		// running timer task as daemon thread
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(timerTask, 0, time * 1000);
   	}
	private void loadConfiguration(){
		String filePath="ConfigFile.txt";
		int defaultTime=5;
		File f = new File(filePath);
		if(!(f.exists())) { 
			this.time=defaultTime;
		}else{
			BufferedReader br=null;
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
			    this.time=Integer.parseInt(sp[1]);
			    
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

}
