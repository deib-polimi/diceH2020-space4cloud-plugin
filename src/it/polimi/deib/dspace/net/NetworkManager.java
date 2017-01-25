package it.polimi.deib.dspace.net;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
	private static String modelUploadEndpoint = rootEndpoint+"/files/upload";
	
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
	
	public void sendModel(File file) throws IOException{
		
	}
	
	
}
