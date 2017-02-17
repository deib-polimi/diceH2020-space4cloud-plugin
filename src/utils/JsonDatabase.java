package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import it.polimi.deib.dspace.net.NetworkManager;

public class JsonDatabase {
	private static JsonDatabase instance;
	
	public static JsonDatabase getInstance(){
		if(instance != null){
			return instance;
		}
		return new JsonDatabase();
	}
	private JsonDatabase(){
		startupCheckings();
	}
	
	private void startupCheckings(){
		File jsonDbFile = new File("db.json");
		if(!jsonDbFile.exists()){
			refreshDbContents();
		}
	}
	public String[] refreshDbContents() {
		JSONArray array = NetworkManager.getInstance().fetchAlternatives();
		String[] alternatives = digestAlternatives(array);
		try {
			FileWriter writer = new FileWriter("db.json");
			writer.write(array.toJSONString());
			writer.close();
			return alternatives;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	private String[] digestAlternatives(JSONArray json){
		String[] returnObject = new String[json.size()];
		Iterator<?> it = json.iterator();
		JSONObject object = new JSONObject();
		String name,type;
		int i = 0;
		while(it.hasNext()){
			object = (JSONObject) it.next();
			name = ((JSONObject) object.get("provider")).get("name").toString();
			type = object.get("type").toString();
			returnObject[i] = name+"-"+type;
			i++;
		}
		return returnObject;
	}
	public String[] getAlternatives(){
		JSONParser parser = new JSONParser();
		try {
			JSONArray parsed = (JSONArray) parser.parse(new FileReader("db.json"));
			return digestAlternatives(parsed);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
