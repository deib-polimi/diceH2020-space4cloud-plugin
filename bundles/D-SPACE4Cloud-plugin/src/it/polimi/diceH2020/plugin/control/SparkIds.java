package it.polimi.diceH2020.plugin.control;

public class SparkIds {

	public static final String DEVICES_2_RESOURCES = "devices2resources";
	public static final String USERS = "Users";
	public static final String NUMBER_OF_CONCURRENT_USERS = "NumberOfConcurrentUsers";

	String devices2resources; // must be replaced with @@CORES@@
	String users; // must be replaced with @@CORES@@
	String numberOfConcurrentUsers; // last transaction id 

	public SparkIds(String devices2resources, String users, String numberOfConcurrentUsers) {
		this.devices2resources = devices2resources;
		this.users = users;
		this.numberOfConcurrentUsers = numberOfConcurrentUsers;
	}

	public String getDevices2resources() {
		return devices2resources;
	}

	public String getUsers() {
		return users;
	}

	public String getNumberOfConcurrentUsers() {
		return numberOfConcurrentUsers;
	}

}
