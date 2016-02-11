package properties;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

import configuration.CaseProperties;

public class Materials extends Property {

	public Materials(JSONArray inputProperties) {
		super(inputProperties);
	}

	/**
	 * Update materials properties
	 * @param inputProperties
	 * @return
	 */
	public JSONArray updateMaterials() {
		JSONObject matType = this.getPropertyByName(inputProperties, CaseProperties.PROP_MAT_TYPE);
		JSONObject matDep = this.createProperty(CaseProperties.PROP_MAT_DEPARTMENT, false);
		JSONObject matCopies = this.createProperty(CaseProperties.PROP_MAT_COPIES, false);
		JSONObject matOwner = this.createProperty(CaseProperties.PROP_MAT_OWNER, false);
		
		JSONArray result = new JSONArray();
		JSONArray materials = (JSONArray)matType.get("value");
		
		// Paper materials
		if (materials != null && materials.contains("Paper")) {
			matDep.put("hidden", false);
			matDep.put("required", true);			
			matCopies.put("hidden", false);
			matCopies.put("required", true);
			matOwner.put("hidden", false);
			matOwner.put("required", true);
		} else {
			matDep.put("hidden", true);
			matDep.put("value", null);
			matDep.put("required", false);			
			matCopies.put("hidden", true);
			matCopies.put("value", null);
			matCopies.put("required", false);
			matOwner.put("hidden", true);
			matOwner.put("value", null);
			matOwner.put("required", false);
		}
		
		// Electronical materials
		JSONObject eMatResp = this.createProperty(CaseProperties.PROP_EMAT_RESP, false);
		if (materials != null && materials.contains("Electornical")) {
			eMatResp.put("hidden", false);
			eMatResp.put("required", true);			
		} else {
			eMatResp.put("hidden", true);
			eMatResp.put("required", false);	
			eMatResp.put("value", null);
		}
		
		result.add(matDep);
		result.add(matCopies);
		result.add(matOwner);
		result.add(eMatResp);
		
		return result;
	}
}
