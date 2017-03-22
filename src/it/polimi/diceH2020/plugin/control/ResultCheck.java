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

package it.polimi.diceH2020.plugin.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import javax.swing.JOptionPane;

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
			fileNames.clear();
			load();
			this.checkSolExistence();
		}
	}

	private void load(){
		BufferedReader br = null;

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
	private void checkSolExistence(){
		for(int i=0;i<this.urls.size();i++){
			try {
				String fPath=this.downloadFile(urls.get(i));

				if(!fPath.equals("")){
					JOptionPane.showMessageDialog(null, "Results available", "InfoBox: " , JOptionPane.INFORMATION_MESSAGE);

					JSonReader j = new JSonReader(fPath);
					j.createMap(this.fileNames.get(i));
					j.read();
					j.write();
					this.removeLine(urls.get(i));
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

			// opens an output stream to save into file
			String saveFilePath = fileName;
			FileOutputStream outputStream = new FileOutputStream(saveFilePath);

			int bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			outputStream.close();
			inputStream.close();

			toReturn = saveFilePath;
		} else {
			toReturn = "";
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
