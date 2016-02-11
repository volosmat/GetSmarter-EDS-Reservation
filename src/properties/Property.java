package properties;

import java.util.Map;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class Property extends Case {

	public Property(JSONArray inputProperties) {
		super(inputProperties);
	}
	
	/**
	 * Find property in properties array by name
	 * @param properties
	 * @param propertyName
	 * @return
	 */
	protected JSONObject getPropertyByName(JSONArray properties, String propertyName) {
		for (int i=0; i<properties.size(); i++) {
			JSONObject property = (JSONObject)properties.get(i);
			if (property.get("symbolicName").equals(propertyName)) {
				return property;
			}
		}
		return null;
	}
	
	/**
	 * Find key by value
	 * @param map
	 * @param value
	 * @return
	 */
	protected Object getKeyFromValue(Map<?, ?> map, Object value) {
		for (Object o : map.keySet()) {
			if (map.get(o).equals(value)) {
				return o;
			}
		}
		return null;
	 } 
}
