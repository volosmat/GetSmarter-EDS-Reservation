package properties;

import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.login.CredentialExpiredException;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.ibm.websphere.security.WSSecurityException;
import com.ibm.websphere.security.auth.CredentialDestroyedException;
import com.ibm.websphere.security.auth.WSSubject;
import com.ibm.websphere.security.cred.WSCredential;

import configuration.CaseProperties;
import model.User;
import servlets.EventReservationServlet;

public class Applicant extends Property {

	public Applicant(JSONArray inputProperties) {
		super(inputProperties);
	}
	
	/**
	 * Update applicant properties
	 * @return
	 */
	public JSONArray updateApplicant() {
		JSONObject usernameProperty = this.getPropertyByName(inputProperties, CaseProperties.PROP_USERNAME);
		JSONObject phoneProperty = this.getPropertyByName(inputProperties, CaseProperties.PROP_PHONE);
		JSONObject emailProperty = this.getPropertyByName(inputProperties, CaseProperties.PROP_EMAIL);
		
		JSONArray result = new JSONArray();
		
		String emailPropertyValue = (String)emailProperty.get("value");
		// Update applicant info only when applicant fields are not set
		if (emailPropertyValue != null && !emailPropertyValue.isEmpty()) {
			return result;
		}
		
		String loginEmail = this.getUserLoginEmail();
		User userDetail = new User(loginEmail);
		
		usernameProperty.put("value", userDetail.getName());		
		phoneProperty.put("value", userDetail.getPhone());		
		emailProperty.put("value", loginEmail);
		
		result.add(usernameProperty);
		result.add(phoneProperty);
		result.add(emailProperty);
		
		return result;
	}
	
	/**
	 * Get user login (always user login is IBM email)
	 * @return
	 */
	public String getUserLoginEmail() {
		try {
			// Get current security subject
			Subject security_subject;
			security_subject = WSSubject.getRunAsSubject();
			
			if  (security_subject != null) {
			    // Get all security credentials from the security subject
			    Set<WSCredential> security_credentials = security_subject.getPublicCredentials(WSCredential.class);
			    // Get the first credential
			    WSCredential security_credential = (WSCredential)security_credentials.iterator().next();
			    return security_credential.getSecurityName();
			}
		} catch (CredentialExpiredException e) {
			EventReservationServlet.logger.catching(e);
		} catch (CredentialDestroyedException e) {
			EventReservationServlet.logger.catching(e);
		} catch (WSSecurityException e) {
			EventReservationServlet.logger.catching(e);
		}
		
		return "";
	}
}
