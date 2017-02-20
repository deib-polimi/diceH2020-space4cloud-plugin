package it.polimi.deib.dspace.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import javax.swing.JOptionPane;

public class ResultCheck extends TimerTask{
	private final int BUFFER_SIZE = 4096;
	//file path that contains all the links to go check for solution
	String filePath;
	List<String> urls;
	
	ResultCheck(String filePath){
		this.filePath=filePath;
		urls=new ArrayList<String>();
	}
	@Override
	public void run() {
		urls.clear();
		load();
		this.checkSolExcistence();
	}
	private void load(){
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
		    String[] st=everything.split("\n");
		    for(String s:st){
		    	this.urls.add(s);
		    }
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
	//TODO add about default saving directory
	private void checkSolExcistence(){
		for(String s:this.urls){
			try {
				String fPath=this.downloadFile(s);
				if(!fPath.equals("")){
					  JOptionPane.showMessageDialog(null, "Results availble", "InfoBox: " , JOptionPane.INFORMATION_MESSAGE);
			          JSonReader j=new JSonReader(fPath);
			          //TODO add string here
			          j.createMap("Add String here");
			          j.read();
			          j.write();
			          
				}
			} catch (IOException e) {
				
			}
		}
	}
	
	
	private String downloadFile(String urlString) throws IOException{
		String toReturn;
		URL url = new URL(urlString);
	        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
	        int responseCode = httpConn.getResponseCode();
	 
	        // always check HTTP response code first
	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            String fileName = "";
	            String disposition = httpConn.getHeaderField("Content-Disposition");
	 
	            if (disposition != null) {
	                // extracts file name from header field
	                int index = disposition.indexOf("filename=");
	                if (index > 0) {
	                    fileName = disposition.substring(index + 10,
	                            disposition.length() - 1);
	                }
	            } else {
	                // extracts file name from URL
	                fileName = urlString.substring(urlString.lastIndexOf("/") + 1,
	                		urlString.length());
	            }
	 
	            // opens input stream from the HTTP connection
	            InputStream inputStream = httpConn.getInputStream();
	            String saveFilePath = Configuration.getCurrent().getSavingDir() + File.separator + fileName;
	             
	            // opens an output stream to save into file
	            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
	 
	            int bytesRead = -1;
	            byte[] buffer = new byte[BUFFER_SIZE];
	            while ((bytesRead = inputStream.read(buffer)) != -1) {
	                outputStream.write(buffer, 0, bytesRead);
	            }
	 
	            outputStream.close();
	            inputStream.close();
	 
	            toReturn =saveFilePath;
	        } else {
	        	toReturn ="";
	        }
	        httpConn.disconnect();
	        return toReturn;
	}
	private boolean checkSolutionExists(String urlString){
		boolean toReturn;
		URL url=null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	        HttpURLConnection httpConn=null;
			try {
				httpConn = (HttpURLConnection) url.openConnection();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        int responseCode;
			try {
				responseCode = httpConn.getResponseCode();
			} catch (IOException e) {
				return false;
			}
	        if(responseCode == HttpURLConnection.HTTP_OK){
	        	return true;
	        }else{
	        	return false;
	        }
	 
	}
	

}
