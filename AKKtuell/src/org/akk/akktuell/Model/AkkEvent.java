package org.akk.akktuell.Model;

import android.net.Uri;

public class AkkEvent {

	private final String eventName;
	
	private final String eventDate;
	
	private final String eventdescription;
	
	private final String eventBeginTime;
	
	private final Uri eventPictureUri;

	public AkkEvent(String eventName, String eventDate,
			String eventdescription, String eventBeginTime, Uri eventPictureUri) {
		this.eventName = eventName;
		this.eventDate = eventDate;
		this.eventdescription = eventdescription;
		this.eventBeginTime = eventBeginTime;
		this.eventPictureUri = eventPictureUri;
	}
	
	
	
}
