package org.akk.akktuell.Model;

import android.os.Build;

public class CalendarBridge {

	private boolean usesCalendarContract;
	
	private CalendarBridge calendar;
	
	public CalendarBridge () {
		if (Build.VERSION.SDK_INT > 4) {
			this.usesCalendarContract = true;
			this.calendar =  new CalendarBridgeUsingCalendarContract();
		} else {
			this.usesCalendarContract = false;
			this.calendar =  new CalendarBridgeNotUsingCalendarContract();
		}
	}
	
	
	public void addEvent(AkkEvent event) {
		calendar.addEvent(event);
	}

	public void removeEvent(AkkEvent event) {
		calendar.removeEvent(event);
	}
	
	public boolean isInCalendar(AkkEvent event) {
		return this.calendar.isInCalendar(event);
	}
}
