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

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.diceH2020.plugin.net.NetworkManager;

public class FileHandler {
	private List<File> files;
	private File folder;
	private String scenario;

	public void setFolder(String path){
		folder=new File(path);
	}

	private void getFilesFromFolder(){
		this.files=new ArrayList<File>();
		for(File f:folder.listFiles()){
			files.add(f);
		} 
	}

	public void setScenario(String scenario){
		this.scenario = scenario;
	}

	public void sendFile(){
		this.getFilesFromFolder();
		try {
			NetworkManager.getInstance().sendModel(files, scenario);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setScenario(boolean isPrivate,boolean isELTC){
		if(!isPrivate){
			if(isELTC){
				this.scenario="PublicPeakWorkload";
			}else{
				this.scenario="PublicAvgWorkLoad";
			}
		}
	}

}
