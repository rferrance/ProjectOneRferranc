package edu.vt.ece4564.AssignmentOne.rferranc;

import org.json.JSONArray;

public class WootEvent {
	private String endDate;
	private String ID;
	private JSONArray offers;
	private String startDate;
	private String site;
	private String title;
	private String type;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public JSONArray getOffers() {
		return offers;
	}
	public void setOffers(JSONArray offers) {
		this.offers = offers;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
}
