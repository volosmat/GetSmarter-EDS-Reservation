package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import configuration.ConfigurationProperties;
import servlets.EventReservationServlet;

public class User {
	
	private ArrayList<String> userData;
	
	public User(String loginEmail) {
		super();
		EventReservationServlet.logger.info("BluePages API call for " + loginEmail);
		this.userData = this.getUserData(loginEmail);
	}
	
	/**
	 * Get real user name from BluePages
	 * @return
	 */
	public String getName() {
		return this.findRowByKey(ConfigurationProperties.NAME_KEY);
	}
	
	/**
	 * Get user phone number from BluePages
	 * @return
	 */
	public String getPhone() {
		String contactNumber = this.findRowByKey(ConfigurationProperties.MOBILE_KEY);
		
		if (contactNumber == null) {
			contactNumber = this.findRowByKey(ConfigurationProperties.PHONE_KEY);
		}
		
		return contactNumber;
	}
	
	/**
	 * Iterate data from BluePages and find row with given key
	 * @param key
	 * @return
	 */
	private String findRowByKey(String key) {
		for (String row: this.userData) {
			String[] rowTokens = row.split(":");
			if (rowTokens[0].equals(key) && rowTokens.length > 1) {
				// Return correct row value without first char (first char is always whitespace)
				return rowTokens[1].substring(1);
			}
		}
		
		return null;
	}
	
	/**
	 * Get user data from BluePages through WSAPI (HTTP API)
	 * For more information about WSAPI see 
	 * https://w3-connections.ibm.com/wikis/home?lang=en-us#!/wiki/W1f849f7604cc_43a5_a6d9_2ad1fcbc532e/page/WSAPI
	 * @param loginEmail
	 * @return
	 */
	private ArrayList<String> getUserData(String loginEmail) {
		ArrayList<String> result = new ArrayList<String>();
		try {
			// Prepare API URL
			URL url = new URL(ConfigurationProperties.BLUEPAGES_API_URL + loginEmail);
			// Make HTTP call
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			// Get input stream
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				result.add(line);
			}
			reader.close();
		} catch (IOException e) {
			EventReservationServlet.logger.catching(e);
		}
		
		return result;
	}
}
