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

package it.polimi.diceH2020.plugin.net;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import it.polimi.diceH2020.plugin.control.Configuration;
import it.polimi.diceH2020.plugin.preferences.Preferences;
import it.polimi.diceH2020.SPACE4Cloud.shared.settings.*;

/**
 * Manages interaction with the backend
 * 
 * @author Giorgio Pea <giorgio.pea@mail.polimi.it>
 */
public class NetworkManager {

	private static NetworkManager instance;

	public static NetworkManager getInstance() {
		if (instance != null) {
			return instance;
		}
		instance = new NetworkManager();
		return instance;
	}

	private String retrieveUploadEndpoint() {
		if (Preferences.getSimulator().equals(Preferences.DAG_SIM))
			return Preferences.getDagSolverUrl() + "/files/upload";
		else 
			return Preferences.getFrontEndUrl() + "/files/upload";
	}

	private String retrieveVMConfigsEndpoint() {
		return Preferences.getBackEndUrl() + "/vm-types";
	}

	private NetworkManager() {
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
		// System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");
	}

	/**
	 * Fetches vm configurations from the web
	 * 
	 * @return A json object representing the fetched vm configurations
	 */
	public JSONArray fetchVmConfigs() {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(retrieveVMConfigsEndpoint());
		CloseableHttpResponse response;
		String body;
		JSONParser parser;

		try {
			response = httpclient.execute(httpget);
			if (response.getStatusLine().getStatusCode() != 200) {
				return null;
			} else {
				// Converts response stream into string in order to parse
				// everything int json
				body = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
				parser = new JSONParser();
				response.close();
				return ((JSONArray) parser.parse(body));
			}
		} catch (UnsupportedOperationException | ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Sends to the backend the models to be simulated
	 * 
	 * @param files
	 *            The model files
	 *            The scenario parameter
	 * @throws UnsupportedEncodingException
	 */
	public void sendModel(List<File> files, Scenario scenario) throws UnsupportedEncodingException {
		HttpClient httpclient = HttpClients.createDefault();
		HttpResponse response;
		HttpPost post = new HttpPost(retrieveUploadEndpoint());
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addPart("scenario", new StringBody(scenario.getStringRepresentation(), ContentType.TEXT_PLAIN));

		for (File file : files) {
			builder.addPart("file[]", new FileBody(file));
		}

		post.setEntity(builder.build());
		try {
			response = httpclient.execute(post);
			String json = EntityUtils.toString(response.getEntity());
			HttpPost repost = new HttpPost(this.getLink(json));
			response = httpclient.execute(repost);
			String response_text = EntityUtils.toString(response.getEntity());
			parseResponseForResultLinks(response_text);
			for (File file : files) {
				System.out.println(file.getName() + " successfully sent.");
			}
			
			System.out.println("Code : " + response.getStatusLine().getStatusCode());

			if (response.getStatusLine().getStatusCode() != 200) {
				System.err.println("Error: POST not succesfull");
			} else {
			}
			System.out.println(Configuration.getCurrent().getID());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String[] getTechnologies() {
		String s[] = {"Storm", "Spark", "Hadoop/MapReduce"};
		return s;
	}

	
	private void parseResponseForResultLinks(String response){
		
		Pattern pattern = Pattern.compile("\\{\"href\":\"(.*?)\"\\}");
        Matcher matcher = pattern.matcher(response);
        while (matcher.find()) {
            String link = matcher.group(1);
            System.out.println("Resuls will be available at: " + link);
        }
	}
	

	private String getLink(String string) {
		JSONParser parser = new JSONParser();
		String link = "";

		try {
			JSONObject json = (JSONObject) parser.parse(string);
			JSONObject lin = (JSONObject) json.get("_links");
			JSONObject sub = (JSONObject) lin.get("submit");
			link = (String) sub.get("href");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return link;
	}
}
