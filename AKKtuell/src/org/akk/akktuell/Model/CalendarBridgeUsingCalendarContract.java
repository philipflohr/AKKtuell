package org.akk.akktuell.Model;

import android.content.Intent;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;

public class CalendarBridgeUsingCalendarContract extends CalendarBridgeProvider {

	@Override
	public void addEvent(AkkEvent event) {
		Intent intent = new Intent(Intent.ACTION_INSERT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra(Events.TITLE, event.getEventName());
		intent.putExtra(Events.EVENT_LOCATION, "AKK");
		intent.putExtra(Events.DESCRIPTION, event.getEventDescription());
		intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.getEventBeginTime().getTimeInMillis());
	}

	@Override
	public void removeEvent(AkkEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isInCalendar(AkkEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
}
