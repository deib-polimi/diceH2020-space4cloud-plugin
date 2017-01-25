package it.polimi.deib.dspace.net;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Manages interaction with the backend
 * @author Giorgio Pea
 */
public class NetworkManager {
	
	private static NetworkManager instance;
	private static String rootEndpoint = "http://specclient1.dei.polimi.it:8080";
	private static String alternativesEndpoint = rootEndpoint+"/alternatives";
	
	public static NetworkManager getInstance(){
		if(instance != null){
			return instance;
		}
		instance = new NetworkManager();
		return instance;
		
	}
	private HttpURLConnection openConnection(String endpoint) throws IOException{
		URL url = new URL(endpoint);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		return connection;
	}
	private void closeConnection(HttpURLConnection connection){
		connection.disconnect();
	}
	
	public JSONObject fetchAlternatives() throws IOException, ParseException{
		int responseCode;
		BufferedReader in;
		StringBuilder sb;
		String line;
		JSONParser parser;
		JSONObject json;
		HttpURLConnection connection = this.openConnection(alternativesEndpoint);
		connection.setRequestMethod("GET");
		responseCode = connection.getResponseCode();
		if(responseCode != 200){
			System.err.println("Error: GET request for "+alternativesEndpoint+" has not been succesfull -> response code "+responseCode);
			return null;
		}
		 in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		 sb = new StringBuilder();
		 while((line = in.readLine()) != null){
			 sb.append(line);
		 }
		 parser = new JSONParser();
		 json = (JSONObject) parser.parse(sb.toString());
		return json;
		
	}
	
	public void sendModel(){
		
	}
	
	
}
