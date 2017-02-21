package it.polimi.deib.dspace.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import javax.swing.JOptionPane;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
public class ResultCheck extends TimerTask{
	private static final int BUFFER_SIZE = 4096;
	//file path that contains all the links to go check for solution
	String filePath;
	List<String> urls;
	List<String> fileNames;
	
	public ResultCheck(String filePath){
		this.filePath=filePath;
		urls=new ArrayList<String>();
		fileNames=new ArrayList<String>();
	}
	@Override
	public void run() {
		File f = new File("results");
		if(f.exists()){
		urls.clear();
		load();
		this.checkSolExcistence();
		}
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
		    for(int i=0;i<st.length;i++){
		    	if(i%2==0){
		    		this.fileNames.add(st[i]+"OUT.json");
		    	}else{
		    		this.urls.add(st[i]);	
		    	}
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
		for(int i=0;i<this.urls.size();i++){
			try {
				String fPath=this.downloadFile(urls.get(i));
				 
				if(!fPath.equals("")){
					  this.removeLine(urls.get(i));
					  JOptionPane.showMessageDialog(null, "Results availble", "InfoBox: " , JOptionPane.INFORMATION_MESSAGE);
					  
					  JSonReader j=new JSonReader(fPath);
			          j.createMap(this.fileNames.get(i));
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
	            //Configuration.getCurrent().getSavingDir() + File.separator;
	            String saveFilePath =  fileName;
	             
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
	
	private void removeLine(String url){
		BufferedReader br=null;
		int urlPos=-1;
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
		    for(int i=0;i<st.length;i++){
		    	if(st[i].equals(url)){
		    		urlPos=i/2;
		    		break;
		    	}
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
		 PrintWriter writer;
		try {
			writer = new PrintWriter("results", "UTF-8");
			 for(int i=0;i<this.urls.size();i++){
				 if(i==urlPos)
					 continue;
				 writer.println(this.fileNames.get(i));
				 writer.println(this.urls.get(i));
				 writer.close();
			 }
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
