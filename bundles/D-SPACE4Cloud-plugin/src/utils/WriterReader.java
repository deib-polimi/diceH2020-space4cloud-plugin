/*
Copyright 2017 Marco Ieni

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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import it.polimi.diceH2020.plugin.preferences.Preferences;

public class WriterReader {

	public static void writeClassDesc(Serializable obj) {
		try {
			FileOutputStream f = new FileOutputStream(new File(Preferences.getSavingDir() + "WriteObj.txt"));
			ObjectOutputStream o = new ObjectOutputStream(f);
			o.writeObject(obj);
			o.close();
			f.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Object readObject(String filename) {
		FileInputStream fi;
		Object obj = null;
		try {
			fi = new FileInputStream(new File(Preferences.getSavingDir() + filename));

			ObjectInputStream oi = new ObjectInputStream(fi);

			// Read objects
			obj = oi.readObject();

			oi.close();
			fi.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}
	
	

}
