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

package utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import it.polimi.diceH2020.plugin.net.NetworkManager;

/**
 * Contains methods for dealing with the different vm configurations local database
 * @author Giorgio Pea <giorgio.pea@mail.polimi.it>
 */
public class JsonDatabase {
	private static JsonDatabase instance;

	public static JsonDatabase getInstance(){
		if(instance != null){
			return instance;
		}
		instance = new JsonDatabase();
		return instance;
	}

	private JsonDatabase(){
		startupCheckings();
	}

	/**
	 * Checks if the vm configurations local database is available, if not
	 * it creates and populates it
	 */
	private void startupCheckings(){
		File jsonDbFile = new File("vmconfigs.json");
		if(!jsonDbFile.exists()){
			refreshDbContents();
		}
	}

	/**
	 * Fetches vm configurations from the web and populates a database with them
	 * @return The different vm configurations available
	 */
	public String[] refreshDbContents() {
		JSONArray array = NetworkManager.getInstance().fetchVmConfigs();

		if(array != null) {
			String[] alternatives = digestAlternatives(array);
			try {
				//Writes the db
				FileWriter writer = new FileWriter("vmconfigs.json");
				writer.write(array.toJSONString());
				writer.close();
				return alternatives;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * Parses the received json in order to output a list of vm configurations
	 * @param json A json representing vm configurations with several parameters
	 * @return The different vm configurations available
	 */
	private String[] digestAlternatives(JSONArray json){
		String[] returnObject = new String[json.size()];
		Iterator<?> it = json.iterator();
		JSONObject object;
		String name,type;
		int i = 0;

		while(it.hasNext()){
			object = (JSONObject) it.next();
			name = ((JSONObject) object.get("provider")).get("name").toString();
			type = object.get("type").toString();
			returnObject[i] = name+"-"+type;
			++i;
		}

		return returnObject;
	}

	/**
	 * Fetches the different vm configurations from the database
	 * @return the different vm configurations available
	 */
	public String[] getVmConfigs(){
		JSONParser parser = new JSONParser();

		try {
			JSONArray parsed = (JSONArray) parser.parse(new FileReader("vmconfigs.json"));
			return digestAlternatives(parsed);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
