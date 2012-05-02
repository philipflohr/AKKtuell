package org.akk.akktuell.Model;

import java.util.GregorianCalendar;

import android.net.Uri;

public class AkkEvent {
	
	public enum AkkEventType {
		Schlonz("Schlonz"),
		Workshop("Workshop"),
		Tanzen("Tanzen"),
		Sonderveranstaltung("Sonderveranstaltung"),
		Veranstaltungshinweis("Veranstaltungshinweis"),
		Default("Schlonz");
		
		private final String desc;
		
		AkkEventType(String desc) {
			this.desc = desc;
		}

		public boolean isEqual(Object o) {
			if (o instanceof AkkEventType) {
				return (this.equals(o));
			} else if (o instanceof String) {
				return (this.toString().equals((String) o));
			}
			return false;
		}

		public static AkkEventType getAkkEventType(String desc) {
			for (AkkEventType t : AkkEventType.values())
				if (t.isEqual(desc))
					return t;
			return Default;
		}

		public String toString() {
			return this.desc;
		}
		
	}

	private final String eventName;
	
	private String eventDescription;
	
	private final GregorianCalendar eventBeginTime;
	
	private Uri eventPictureUri;
	
	private final String eventPlace;
	
	private AkkEventType type = null;

	public AkkEvent(String eventName, GregorianCalendar eventBeginTime, String eventPlace) {
		this.eventName = eventName;
		this.eventBeginTime = eventBeginTime;
		this.eventPlace = eventPlace;
	}
	
	public AkkEvent(String string, String string2,
			GregorianCalendar iso8601ToGregorianCalendar, Uri parse) {
		this.eventName = string;
		this.eventBeginTime = iso8601ToGregorianCalendar;
		this.eventPlace = "test";
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean equals(Object otherEvent) {
		if (!(otherEvent instanceof AkkEvent)) {
			return false;
		}
		if (this.eventName.equals(((AkkEvent) otherEvent).getEventName())) {
			return true;
		}
		return false;
	}
	
	public String getEventName() {
		return this.eventName;
	}

	public boolean wasUpdated(AkkEvent otherEvent) {
		if (!this.eventName.equals(otherEvent.getEventName())) {
			throw new IllegalArgumentException("Try to check if Event was updated. Failure: " + this.eventName + " was compared with " + otherEvent.getEventName());
		}
		if ((!this.eventDescription.equals(otherEvent.getEventDescription())) || (!this.eventBeginTime.equals(otherEvent.getEventBeginTime()))
				||(this.eventPictureUri.equals(otherEvent.getEventPicUri()))) {
			return true;
		}
		return false;
	}

	public Uri getEventPicUri() {
		return this.eventPictureUri;
	}

	public String getEventDescription() {
		return this.eventDescription;
	}
	
	public GregorianCalendar getEventBeginTime() {
		return this.eventBeginTime;
	}
	
	public String getPlace() {
		return this.eventPlace;
	}
	
	public void setDescription(String newDesc) {
		this.eventDescription = newDesc;
	}

	public AkkEventType getEventType() {
		return this.type;
	}
	
	public void setType(AkkEventType type) {
		this.type = type;
	}
	
}
