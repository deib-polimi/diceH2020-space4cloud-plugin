package it.polimi.deib.dspace.net;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

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
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Manages interaction with the backend
 * @author Giorgio Pea <giorgio.pea@mail.polimi.it>
 */
public class NetworkManager {
	
	private static NetworkManager instance;
	private static String rootEndpoint = "http://localhost:8000";
	private static String vmConfigsEndpoint = "http://localhost:8080/vm-types";
	private static String modelUploadEndpoint = rootEndpoint+"/files/view/upload";

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
	 * Fetches vm configurations from the web
	 * @return A json object representing the fetched vm configurations
	 */
	public JSONArray fetchVmConfigs(){
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(vmConfigsEndpoint);
		CloseableHttpResponse response;
		String body;
		JSONParser parser;
		try {
			response = httpclient.execute(httpget);
			if(response.getStatusLine().getStatusCode() != 200){
				return null;
			}
			else{
				//Converts response stream into string in order to parse everything int json
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
	 * @param files The model files
	 * @param scenario The scenario parameter
	 * @throws UnsupportedEncodingException 
	 */
	public void sendModel(List<File> files, String scenario) throws UnsupportedEncodingException{
		HttpClient httpclient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
		HttpResponse response;
		HttpPost post = new HttpPost(modelUploadEndpoint);
		
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();  
		builder.addPart("scenario",new StringBody(scenario,ContentType.DEFAULT_TEXT));
		for(File file:files){
			builder.addPart("file[]", new FileBody(file));
		}
	    post.setEntity(builder.build());
	    try {
	    	response = httpclient.execute(post);
			if(response.getStatusLine().getStatusCode() != 302){
				System.err.println("Error: POST not succesfull");
			}
			else{
				//response.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String[] getTechnologies(){
		String s[] = {"Storm", "MapReduce", "Hadoop"};
		return s;
	}
	
}
