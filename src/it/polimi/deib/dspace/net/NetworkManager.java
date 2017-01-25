package it.polimi.deib.dspace.net;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
	private static String rootEndpoint = "http://specclient1.dei.polimi.it:8080";
	private static String alternativesEndpoint = rootEndpoint+"/alternatives";
	private static String modelUploadEndpoint = rootEndpoint+"/files/upload";
	
	public static NetworkManager getInstance(){
		if(instance != null){
			return instance;
		}
		instance = new NetworkManager();
		return instance;
		
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
	public void sendModel(File file_1,File file_2, String scenario) throws UnsupportedEncodingException{
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(modelUploadEndpoint);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();  
		builder.addTextBody("scenario",scenario);
	    builder.addPart("file[]", new FileBody(file_1));
	    builder.addPart("file[]", new FileBody(file_2));
	    httppost.setEntity(builder.build());
	    CloseableHttpResponse response;
	    try {
			response = httpclient.execute(httppost);
			if(response.getStatusLine().getStatusCode() != 200){
				System.err.println("Error");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
