package properties;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

import configuration.CaseProperties;

public class Case {
	
	protected JSONArray inputProperties;
	
	public Case(JSONArray inputProperties) {
		this.inputProperties = inputProperties;
	}
	
	/**
	 * Initialize all properties when case was created
	 * @param inputProperties
	 * @return
	 */
	public JSONArray caseInitialization() {
		JSONArray properties = new JSONArray();
		// Dependents refreshment payment
		properties.add(this.createProperty(CaseProperties.PROP_REFRESHMENT_PAYNMENT, true));
		// Dependents newsletter
		properties.add(this.createProperty(CaseProperties.PROP_NEWSLETTER, true));
		// Dependents materials
		properties.add(this.createProperty(CaseProperties.PROP_MAT_TYPE, true));
		// Dependents required date
		properties.add(this.createProperty(CaseProperties.PROP_REQUIRED_DATE, true));
		
		// Available time slots
		JSONObject timeSlots = this.createProperty(CaseProperties.PROP_AVAILABLE_SLOTS, true);
		timeSlots.put("hidden", true);
		properties.add(timeSlots);
		
		// Set properties hidden/required
		properties.addAll(this.caseUpdate());
		// Set applicant fields (only during case initialization)
		properties.addAll(new Applicant(inputProperties).updateApplicant());
		
		return properties;
	}
	
	/**
	 * Update all properties when property with dependent property was changed
	 * @param inputProperties
	 * @return
	 */
	public JSONArray caseUpdate() {
		JSONArray outputProperties = new JSONArray();
		outputProperties.addAll(new Refreshment(inputProperties).updateRefreshment());
		outputProperties.addAll(new Newsletter(inputProperties).updateNewsletter());
		outputProperties.addAll(new Materials(inputProperties).updateMaterials());
		outputProperties.add(new AvailableTimeSlots(inputProperties).updateAvailableTimeSlots());
		
		return outputProperties;
	}
	
	/**
	 * Create new property with property name
	 * @param propertyName
	 * @param hasDependentProp
	 * @return
	 */
	protected JSONObject createProperty(String propertyName, boolean hasDependentProp) {
		JSONObject property = new JSONObject();
		property.put("symbolicName", propertyName);
		property.put("hasDependentProperties", hasDependentProp);
		
		return property;
	} 
}
