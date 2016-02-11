package properties;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

import configuration.CaseProperties;

public class Newsletter extends Property {
	
	public Newsletter(JSONArray inputProperties) {
		super(inputProperties);
	}

	/**
	 * Update newsletter properties
	 * @param inputProperties
	 * @return
	 */
	public JSONArray updateNewsletter() {
		JSONObject newsletterProp = this.getPropertyByName(inputProperties, CaseProperties.PROP_NEWSLETTER);
		JSONObject eventTypeProp = this.createProperty(CaseProperties.PROP_EVENT_TYPE, false);
		JSONObject descProp = this.createProperty(CaseProperties.PROP_SHORT_DESC, false);
		JSONObject agendaProp = this.createProperty(CaseProperties.PROP_AGENDA, false);
		
		JSONArray result = new JSONArray();
		
		if (newsletterProp.get("value") != null && newsletterProp.get("value").toString().equals("true")) {
			eventTypeProp.put("hidden", false);
			eventTypeProp.put("required", true);			
			descProp.put("hidden", false);
			descProp.put("required", true);
			agendaProp.put("hidden", false);
			agendaProp.put("required", true);
		} else {
			eventTypeProp.put("hidden", true);
			eventTypeProp.put("value", null);
			eventTypeProp.put("required", false);			
			descProp.put("hidden", true);
			descProp.put("value", null);
			descProp.put("required", false);
			agendaProp.put("hidden", true);
			agendaProp.put("value", null);
			agendaProp.put("required", false);
		}
		
		result.add(eventTypeProp);
		result.add(descProp);
		result.add(agendaProp);
		
		return result;
	}
}
