package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import configuration.ConfigurationProperties;
import servlets.EventReservationServlet;

public class Reservations {
	
	/**
	 * Load all reservations from DB and store in collection
	 * @param startDate
	 * @param duration
	 * @return
	 * @throws SQLException 
	 */
	public HashMap<String, HashMap<String, Integer>> getReservations(String startDate) throws SQLException {
		HashMap<String, HashMap<String, Integer>> result = new HashMap<String, HashMap<String, Integer>>();
		Connection conn = null;
		try {
			Context context = new InitialContext();
	
	        DataSource dataSourse = (DataSource) context.lookup(ConfigurationProperties.DATA_SOURCE_NAME);
	        conn = dataSourse.getConnection();
	
	        PreparedStatement query = this.createSelectQuery(conn, startDate);
			ResultSet rs = query.executeQuery();
			while (rs.next()) {
				String date = rs.getString("DATE");
				
				// Key is date of event and value is start time or null (when both time slots are reserved)
				// Only two reservations per day are allowed
				if (result.containsKey(date)) {
					result.put(date, null);
				} else {
					HashMap<String, Integer> time = new HashMap<String, Integer>();
					// In DB, time is in format e.g. 09:00:00 - so i need first number as hour
					time.put("START_TIME", Integer.parseInt(rs.getString("START_TIME").split(":")[0]));
					time.put("END_TIME", Integer.parseInt(rs.getString("END_TIME").split(":")[0]));
					result.put(date, time);
				}
	        }
		} catch (NumberFormatException e) {
			EventReservationServlet.logger.catching(e);
		} catch (NamingException e) {
			EventReservationServlet.logger.catching(e);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		EventReservationServlet.logger.info("Found " + result.size() + " reservations for date " + startDate);
		return result;
	}
	
	/**
	 * Create statement to select all reservations from DB
	 * @param startDate
	 * @return
	 * @throws SQLException 
	 */
	private PreparedStatement createSelectQuery(Connection connection, String startDate) throws SQLException {
		String selectQuery = 
				"(SELECT DATE, START_TIME, END_TIME FROM " + 
				ConfigurationProperties.DB_SCHEMA + "." + 
				ConfigurationProperties.TABLE_EVENT + 
				" WHERE DATE >= ? " +
				" UNION " + 
				"SELECT DATE, START_TIME, END_TIME FROM " + 
				ConfigurationProperties.DB_SCHEMA + "." + 
				ConfigurationProperties.TABLE_RESERVATION + 
				" WHERE DATE >= ?) ORDER BY DATE";
		
		PreparedStatement statement = connection.prepareStatement(selectQuery);
		statement.setString(1, startDate);
		statement.setString(2, startDate);
		
		return statement;
	}
}
