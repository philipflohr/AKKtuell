package org.akk.akktuell.Model;

import android.os.Build;
import android.util.Log;

public class CalendarBridge {

	private CalendarBridgeProvider calendar;
	
	public CalendarBridge () {
		if (calendar == null) {
			if (Build.VERSION.SDK_INT > 14) {
				Log.d("Akktuell", "Benutze Android4: CalendarCotract");
				this.calendar =  new CalendarBridgeUsingCalendarContract();
			} else {
				Log.d("Akktuell", "Benutze seltsamen Kalender workaround");
				this.calendar =  new CalendarBridgeNotUsingCalendarContract();
			}
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
