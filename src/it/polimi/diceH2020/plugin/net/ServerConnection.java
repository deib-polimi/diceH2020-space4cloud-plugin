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

package it.polimi.diceH2020.plugin.net;

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
