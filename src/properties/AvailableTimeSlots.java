package properties;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

import configuration.CaseProperties;
import configuration.ConfigurationProperties;
import model.Reservations;
import servlets.EventReservationServlet;

public class AvailableTimeSlots extends Property {
	
	public AvailableTimeSlots(JSONArray inputProperties) {
		super(inputProperties);
	}
	
	/**
	 * Update property available time slots (add choices)
	 * @param inputProperties
	 * @return
	 */
	public JSONObject updateAvailableTimeSlots() {
		JSONObject requiredDate = this.getPropertyByName(inputProperties, CaseProperties.PROP_REQUIRED_DATE);
		JSONObject availableSlots = this.createProperty(CaseProperties.PROP_AVAILABLE_SLOTS, true);
		
		if (requiredDate.get("value") != null) {
			ArrayList<String> availableTimeSlots = this.getAvailableTimeSlots();
			
			availableSlots.put("hidden", false);
			availableSlots.put("choiceList", this.createTimeSlotsChoiceList(availableTimeSlots));
			availableSlots.put("value", this.removeNonAvailableValues(availableTimeSlots));
		} else {
			availableSlots.put("hidden", true);
			availableSlots.put("value", new JSONArray());
		}
		
		return availableSlots;
	}
	
	/**
	 * Get start date from JSON input string
	 * @param input
	 * @return
	 */
	private Calendar getStartDate() {
		String dateString = (String) this.getPropertyByName(inputProperties, CaseProperties.PROP_REQUIRED_DATE).get("value");
		EventReservationServlet.logger.info("Request date: " + dateString);
		
		if (dateString == null) {
			return null;
		}
		Date date;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(ConfigurationProperties.CASE_DATE_FORMAT);
			// Set time zone of input time
			formatter.setTimeZone(TimeZone.getTimeZone(ConfigurationProperties.REQUIRED_DATE_TIMEZONE));
			// Parse input date string
			date = formatter.parse(dateString);
			// Set working time zone and convert time
			formatter.setTimeZone(TimeZone.getTimeZone(ConfigurationProperties.PRAGUE_TIMEZONE));

			Calendar cal = Calendar.getInstance();
		    cal.setTime(date); 

		    return cal;
		} catch (ParseException e) {
			EventReservationServlet.logger.catching(e);
		}
		
		return null;
	}
	
	/**
	 * Get available slots in next 10 days
	 * @param inputProperties
	 * @return
	 */
	private ArrayList<String> getAvailableTimeSlots() {
		Calendar calendar = this.getStartDate();
		HashMap<String, HashMap<String, Integer>> reservations = null;
		try {
			reservations = (new Reservations()).getReservations(this.calendarToString(calendar));
		} catch (SQLException e) {
			EventReservationServlet.logger.catching(e);
		}
		HashMap<Integer, String> slots = new HashMap<Integer, String>();
		// All time slots is in order and if one is not available we add order and skip this slot
		int orderCounter = -1;
		
		for (int i=0; i < 10; i++) {
			String date = this.calendarToString(calendar);
			int oldCounter = orderCounter;
			// null as value means that both time slots are reserved for today
			if (reservations.containsKey(date) && reservations.get(date) != null) {
				HashMap<String, Integer> reservation = reservations.get(date);
				
				// Reservation starts afternoon, add first morning free slot
				if (reservation.get("START_TIME") >= 13) { 
					slots.put(orderCounter++, date + " 09:00 - 13:00");
				} else if (reservation.get("START_TIME") < 13 && reservation.get("END_TIME") <= 13){
					// Mode order counter because morning slot is full
					orderCounter++;
					slots.put(orderCounter++, date + " 13:00 - 17:00");
				}
			} else if (!reservations.containsKey(date)) {
				// No reservations today
				slots.put(orderCounter++, date + " 09:00 - 13:00");
				slots.put(orderCounter++, date + " 13:00 - 17:00");
			}
			int newChoicesCount = orderCounter - oldCounter;
			// Fill empty places in order counter
			if (newChoicesCount == 0) {
				orderCounter += 2;
			} else if (newChoicesCount == 1) {
				orderCounter += 1;
			}
			calendar.add(Calendar.DATE, 1);
		}
		
		return this.updateSlotsBySelected(slots, orderCounter);
	}
	
	/**
	 * Update slots depends on actual first selected time slot
	 * Show only slots they are before/after longest selected sequence of slots
	 * e.g. 08/10/2015 9:00-13:00 is followed by 08/10/2015 13:00-17:00 and
	 * prevent by 07/10/2015 13:00-17:00
	 * @param inputProperties
	 * @param availableTimeSlots
	 * @param orderCounter
	 * @return
	 */
	private ArrayList<String> updateSlotsBySelected(HashMap<Integer, String> availableTimeSlots, int orderCounter) {
		// Get selected time slots from inputProperties
		JSONArray selectedTimeSlots = (JSONArray) this.getPropertyByName(inputProperties, CaseProperties.PROP_AVAILABLE_SLOTS).get("value");
		// Remove time slots that are not in available range
		selectedTimeSlots = this.removeNonAvailableValues(new ArrayList<String>(availableTimeSlots.values()));
		
		ArrayList<String> result = new ArrayList<String>();
		
		if (!selectedTimeSlots.isEmpty()) {	
			ArrayList<Integer> sequence = this.getMaxSequence(selectedTimeSlots, availableTimeSlots);
			// Add one time slot before first selected slot
			if (availableTimeSlots.containsKey(sequence.get(0)-1)) { 
				String slot = availableTimeSlots.get(sequence.get(0)-1);
				result.add(slot);
			} 
			// Add all selected time slots
			for (Integer slotOrder: sequence) {
				result.add(availableTimeSlots.get(slotOrder));
			}
			// Add one time slot after last selected slot
			if (availableTimeSlots.containsKey(sequence.get(sequence.size()-1)+1)) {
				String slot = availableTimeSlots.get(sequence.get(sequence.size()-1)+1);
				result.add(slot);
			}
		} else {
			// No selected slots, show all available
			for (int i=0; i<orderCounter; i++) {
				if (availableTimeSlots.containsKey(i)) {
					result.add(availableTimeSlots.get(i));
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Get the longest sequence from selected time slots (by order)
	 * @param selectedTimeSlots
	 * @param availableTimeSlots
	 * @return
	 */
	private ArrayList<Integer> getMaxSequence(JSONArray selectedTimeSlots, HashMap<Integer, String> availableTimeSlots) {
		ArrayList<Integer> maxSequence = new ArrayList<Integer>();
		ArrayList<Integer> actualSequence = new ArrayList<Integer>();
		
		for (int i=0; i<selectedTimeSlots.size(); i++) {
			String selectedValue = (String) selectedTimeSlots.get(i);
			Integer valueOrder = (Integer) this.getKeyFromValue(availableTimeSlots, selectedValue);
			// If sequence is interrupted clear the collection
			if (!actualSequence.isEmpty() && actualSequence.get(actualSequence.size()-1) != valueOrder-1) {
				actualSequence.clear();
			}
			
			actualSequence.add(valueOrder);
			
			if (actualSequence.size() > maxSequence.size()) {
				maxSequence = (ArrayList<Integer>) actualSequence.clone();
			}
		}
		
		return maxSequence;
	}
	
	/**
	 * Convert calendar to string of specific format
	 * @param calendar
	 * @return
	 */
	private String calendarToString(Calendar calendar) {
		return new SimpleDateFormat(ConfigurationProperties.DATE_FORMAT).format(calendar.getTime());
	}
	
	/**
	 * Create JSON of choices for property
	 * @param inputProperties
	 * @return
	 */
	private JSONObject createTimeSlotsChoiceList(ArrayList<String> availableTimeSlots) {
		// Create choices with available time slots
		JSONArray choices = new JSONArray();
		for (String slot: availableTimeSlots) {
			JSONObject choice = new JSONObject();
			choice.put("displayName", slot);
			choice.put("value", slot);
			choices.add(choice);
		}
		
		// Create choice list object
		JSONObject choiceList = new JSONObject();
		choiceList.put("displayName", "AvailableTimeSlotsChoiceList");
		choiceList.put("choices", choices);

		return choiceList;
	}
	
	/**
	 * Remove from selected values attribute all values they are not in updated available time slots choice list
	 * @param availableTimeSlots
	 * @return
	 */
	private JSONArray removeNonAvailableValues(ArrayList<String> availableTimeSlots) {
		JSONArray selectedTimeSlots = (JSONArray) this.getPropertyByName(inputProperties, CaseProperties.PROP_AVAILABLE_SLOTS).get("value");
		JSONArray values = new JSONArray();
		
		for (int i=0; i<selectedTimeSlots.size(); i++) {
			String selectedSlot = (String) selectedTimeSlots.get(i);
			if (availableTimeSlots.contains(selectedSlot)) {
				values.add(selectedSlot);
			}
		}
		
		return values;
	}
}
