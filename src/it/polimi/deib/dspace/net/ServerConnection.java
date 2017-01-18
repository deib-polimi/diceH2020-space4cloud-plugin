package it.polimi.deib.dspace.net;

import java.net.InetAddress;
import java.net.Socket;

public class ServerConnection {
	private static ServerConnection connection;
	private String url = "www.google.it";
	
	private ServerConnection(){}

	public static ServerConnection getConnection(){
		if (connection == null){
			connection = new ServerConnection();
		}
		return connection;
	}
	
	public boolean connect(){
		try {
		     InetAddress addr;
		     Socket sock = new Socket(url, 80);
		     addr = sock.getInetAddress();
		     System.out.println("Connected to " + addr);
		     sock.close();
		     return true;
		}catch (java.io.IOException e) {
		     System.out.println("Can't connect to server");
		     System.out.println(e);
		}
		return false;
	}
}
