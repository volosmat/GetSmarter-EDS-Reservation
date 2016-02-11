package servlets;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.*;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

import configuration.*;
import properties.*;

/**
 * EDS servlet for event reservation case
 * @author Matus Volosin
 *
 */
public class EventReservationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final Logger logger = LogManager.getLogger(EventReservationServlet.class);
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EventReservationServlet() {
        super();
        logger.info("Event reservation EDS initialized.");
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {		
		logger.entry();
		
		JSONObject input = this.getInput(request.getReader());
		String requestMode = input.get("requestMode").toString();
		JSONArray inputProperties = (JSONArray)input.get("properties");
		
		logger.info("Post request mode: " + requestMode);
		
		Case eventReservationCase = new Case(inputProperties);
		if (requestMode.equals(ConfigurationProperties.MODE_IN_PROGRESS)) {
			// Update case properties when, property with dependent properties was changed
			JSONArray outputProperties = eventReservationCase.caseUpdate();
			response.getWriter().print(this.createOutput(outputProperties));
			logger.info("Dependants were updated (Event reservation case).");
		} else if (requestMode.equals(ConfigurationProperties.MODE_INITIAL_EXISTING) || 
			requestMode.equals(ConfigurationProperties.MODE_INITIAL_NEW)) {
			// Initialize case properties, when case start
			JSONArray outputProperties = eventReservationCase.caseInitialization();
			response.getWriter().print(this.createOutput(outputProperties));
			logger.info("Case initialization successfull (Event reservation case).");
		} else {
			// Do not change anything when case was completed
			response.getWriter().print(this.createOutput(new JSONArray()));
			
			logger.info("Case was completed (Event reservation case).");
		}
		logger.exit();
	} 
	
	/**
	 * Parse input JSON string to JSON object
	 * @param reader
	 * @return
	 */
	private JSONObject getInput(BufferedReader reader) {
		try {
			String inputString = "";
			String line = null;
			
			while ((line = reader.readLine()) != null) {
				inputString += line;
			}
		
			return JSONObject.parse(inputString);
		} catch (IOException e) {
			logger.catching(e);
		}
		
		return null;
	}
	
	/**
	 * Create output JSON object from output properties and required attributes
	 * @param properties
	 * @return
	 */
	private JSONObject createOutput(JSONArray properties) {
		JSONObject output = new JSONObject();
		output.put("externalDataIdentifier", properties.size());
		output.put("properties", properties);
		
		return output;
	}
}