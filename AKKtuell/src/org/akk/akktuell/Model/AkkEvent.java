package org.akk.akktuell.Model;

import java.util.GregorianCalendar;

import android.net.Uri;

public class AkkEvent {

	private final String eventName;
	
	private final String eventDescription;
	
	private final GregorianCalendar eventBeginTime;
	
	private final Uri eventPictureUri;

	public AkkEvent(String eventName, String eventdescription, GregorianCalendar eventBeginTime, Uri eventPictureUri) {
		this.eventName = eventName;
		this.eventDescription = eventdescription;
		this.eventBeginTime = eventBeginTime;
		this.eventPictureUri = eventPictureUri;
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
	
}
