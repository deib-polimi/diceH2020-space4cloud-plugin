
package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class WriterReader {

	public static void writeClassDesc(Serializable obj) {
		try {
			FileOutputStream f = new FileOutputStream(new File("WriteObj.txt"));
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
			fi = new FileInputStream(new File(filename));

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
