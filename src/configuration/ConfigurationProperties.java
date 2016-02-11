package configuration;

public class ConfigurationProperties {
	// Timezone of date given from JSON
	public static final String REQUIRED_DATE_TIMEZONE = "UTC";
	// Database timezone
	public static final String PRAGUE_TIMEZONE = "Europe/Prague";
	
	// Working date format
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	
	// Date format in received json (case manager send date with this format)
	public static final String CASE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	
	// Database
	public static final String DATA_SOURCE_NAME = "JNDI/TEC_DB2";
	public static final String DB_SCHEMA = "db2admin";
	public static final String TABLE_EVENT = "dwh_iic_event";
	public static final String TABLE_RESERVATION = "dwh_iic_reservation";
	
	// Request mode types
	public static final String MODE_INITIAL_NEW = "initialNewObject";
	public static final String MODE_INITIAL_EXISTING = "initialExistingObject";
	public static final String MODE_IN_PROGRESS = "inProgressChanges";
	public static final String MODE_FINAL_NEW = "finalNewObject";
	public static final String MODE_FINAL_EXISTING = "finalExistingObject";
	
	// BluePages API url
	public static final String BLUEPAGES_API_URL = "https://9.17.186.253/BpHttpApisv3/wsapi?byInternetAddr=";
	public static final String NAME_KEY = "NAME";
	public static final String PHONE_KEY = "XPHONE";
	public static final String MOBILE_KEY = "CELLULAR";
}
