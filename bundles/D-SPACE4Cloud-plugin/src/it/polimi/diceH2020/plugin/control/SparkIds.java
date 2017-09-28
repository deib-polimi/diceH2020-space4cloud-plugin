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
package it.polimi.diceH2020.plugin.control;

public class SparkIds {

	public static final String DEVICES_2_RESOURCES = "devices2resources";
	public static final String USERS = "Users";
	public static final String NUMBER_OF_CONCURRENT_USERS = "NumberOfConcurrentUsers";

	String devices2resources; 
	String users; 
	String numberOfConcurrentUsers; 

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
