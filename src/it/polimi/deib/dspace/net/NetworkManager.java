package it.polimi.deib.dspace.net;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Manages interaction with the backend
 * @author Giorgio Pea
 */
public class NetworkManager {
	
	private static NetworkManager instance;
	private static String rootEndpoint = "http://specclient1.dei.polimi.it:8018";
	private static String alternativesEndpoint = rootEndpoint+"/alternatives";
	private static String modelUploadEndpoint = rootEndpoint+"/files/upload";
	private static String simulationSetupEndpoint = rootEndpoint+"/launch/simulationSetup";
	
	public static NetworkManager getInstance(){
		if(instance != null){
			return instance;
		}
		instance = new NetworkManager();
		return instance;
		
	}
	public NetworkManager() {
		System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.SimpleLog");
		System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");
	}
	/**
	 * Fetches alternatives from the backend
	 * @return A json object representing the fetched alternatives
	 */
	public JSONObject fetchAlternatives(){
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(alternativesEndpoint);
		CloseableHttpResponse response;
		String body;
		JSONParser parser;
		JSONObject json;
		try {
			response = httpclient.execute(httpget);
			if(response.getStatusLine().getStatusCode() != 200){
				return null;
			}
			else{
				//Converts response stream into string in order to parse everything int json
				body = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
				parser = new JSONParser();
				json = (JSONObject) parser.parse(body);
				response.close();
				return json;
			}
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClientProtocolException e1) {
			//Connection problem
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	/**
	 * Sends to the backend the models to be simulated
	 * @param file The model file
	 * @param scenario The scenario parameter
	 * @throws UnsupportedEncodingException 
	 */
	public void sendModel(File[] files, String scenario, String initialMarking) throws UnsupportedEncodingException{
		HttpClient httpclient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
		HttpResponse response;
		HttpPost post = new HttpPost(modelUploadEndpoint);
		
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();  
		builder.addPart("scenario",new StringBody(scenario,ContentType.DEFAULT_TEXT));
		builder.addPart("initialMarking",new StringBody(scenario,ContentType.DEFAULT_TEXT));
		for(File file:files){
			builder.addPart("file[]", new FileBody(file));
		}
	    post.setEntity(builder.build());
	    try {
	    	response = httpclient.execute(post);
			if(response.getStatusLine().getStatusCode() != 302){
				System.err.println("Error: POST not succesfull");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void simulationSetup(){
		HttpClient httpclient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
		HttpGet httpget = new HttpGet(simulationSetupEndpoint);
		HttpResponse response;
		try {
			response = httpclient.execute(new HttpPost(modelUploadEndpoint));
			if(response.getStatusLine().getStatusCode() != 200){
				//
			}
			else{
				//response.close();
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String[] getAlternatives(){
		String s[] = {"a","b","c","d"};
		return s;
	}
	
	public String[] getTechnologies(){
		String s[] = {"Storm", "MapReduce", "Aaaaa","Hadoop"};
		return s;
	}
	
}
