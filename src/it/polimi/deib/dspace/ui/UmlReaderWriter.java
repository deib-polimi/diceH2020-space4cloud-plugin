package it.polimi.deib.dspace.ui;



import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UmlReaderWriter {

	public static void read(String filePath){
		try {	
	         File inputFile = new File(filePath);
	         DocumentBuilderFactory dbFactory 
	            = DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	         Document doc = dBuilder.parse(inputFile);
	         doc.getDocumentElement().normalize();
	         Element root=doc.getDocumentElement();
	         NodeList nodes=root.getElementsByTagName("DDSM:DdsmVm");
	         Node n=nodes.item(0);
	         NamedNodeMap atributes=n.getAttributes();
	         System.out.println("ktu\n");
	         System.out.println(atributes.getNamedItem("provider").toString());
		} catch (Exception e) {
	         e.printStackTrace();
	      }
	   }
}
