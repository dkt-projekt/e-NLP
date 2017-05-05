package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.Date;

public class MovementActionEvent {

	private int startIndex;
	private int endIndex;
	private String person;
	private String origin;
	private String destination;
	private Date departureTime;
	private Date arrivalTime;
	/**
	 * Travel mode refers to the transportation mode.
	 */
	private String travelMode;
	/**
	 * Text refers to the text where the MAE appears.
	 */
	private String text;
	/**
	 * Confidence of the MAE.
	 */
	private float score;
	
	public MovementActionEvent(int startIndex, int endIndex, String person, String origin, String destination,
			Date departureTime, Date arrivalTime, String travelMode, String text, float score) {
		super();
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.person = person;
		this.origin = origin;
		this.destination = destination;
		this.departureTime = departureTime;
		this.arrivalTime = arrivalTime;
		this.travelMode = travelMode;
		this.text = text;
		this.score = score;
	}

	public MovementActionEvent() {
		super();
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Date getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(Date departureTime) {
		this.departureTime = departureTime;
	}

	public Date getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public String getTravelMode() {
		return travelMode;
	}

	public void setTravelMode(String travelMode) {
		this.travelMode = travelMode;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}
	
	
}
