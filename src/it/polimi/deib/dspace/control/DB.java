package it.polimi.deib.dspace.control;

public class DB {
	private static DB dataBase;
	
	public static DB getDB(){
		if (dataBase == null){
			dataBase = new DB();
		}
		return dataBase;
	}
	
	public String[] fetchAlternatives(){
		String[] a = {"a","b","c","d","e"}; 
		return(a);
	}
	
	public String[] fetchTechnology(){
		String[] a = {"MapReduce","Storm","Spark"};
		return a;
	}
}
