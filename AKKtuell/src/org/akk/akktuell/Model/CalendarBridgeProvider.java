package org.akk.akktuell.Model;

public abstract class CalendarBridgeProvider {

	public abstract void addEvent(AkkEvent event);

	public abstract void removeEvent(AkkEvent event);

	public abstract boolean isInCalendar(AkkEvent event);

}
