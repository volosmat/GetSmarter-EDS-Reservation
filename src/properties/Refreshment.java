package properties;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

import configuration.CaseProperties;

public class Refreshment extends Property {

	public Refreshment(JSONArray inputProperties) {
		super(inputProperties);
	}

	/**
	 * Update refreshment properties
	 * @param inputProperties
	 * @return
	 */
	public JSONArray updateRefreshment() {
		JSONObject paynmentProperty = this.getPropertyByName(inputProperties, CaseProperties.PROP_REFRESHMENT_PAYNMENT);
		JSONObject departmentProperty = this.createProperty(CaseProperties.PROP_REFRESHMENT_DEPARTMENT, false);
		JSONObject approverProperty = this.createProperty(CaseProperties.PROP_REFRESHMENT_APPROVER, false);
		
		JSONArray result = new JSONArray();
		if (paynmentProperty.get("value") != null && paynmentProperty.get("value").toString().equals("Department")) {
			departmentProperty.put("hidden", false);
			departmentProperty.put("required", true);			
			approverProperty.put("hidden", false);
			approverProperty.put("required", true);
		} else {
			departmentProperty.put("hidden", true);
			departmentProperty.put("value", null);
			departmentProperty.put("required", false);			
			approverProperty.put("hidden", true);
			approverProperty.put("value", null);
			approverProperty.put("required", false);
		}
		
		result.add(departmentProperty);
		result.add(approverProperty);
		
		return result;
	}
}
