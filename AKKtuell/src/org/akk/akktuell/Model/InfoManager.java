package org.akk.akktuell.Model;

import java.util.LinkedList;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.akk.akktuell.database.*;


public class InfoManager {
	
	private boolean isOnline;

	private CalendarBridge calendar;
	
	private ConnectivityManager conMgr;
	
	private Context applicationContext;
	
	private Updater updater;
	
	private Thread t;
	
	private AkkHomepageEventParser parser;
	
	private Database database;
	
	private LinkedList<AkkEvent> eventsSortedByDate;
	
	public InfoManager(Context context) {
		applicationContext = context;
		database = Database.getInstance(context);
		eventsSortedByDate = new LinkedList<AkkEvent>();
		//eventsSortedByDate = database.getAllEvents(orderBy, direction);
		calendar = new CalendarBridge();
		updater = new Updater(null, null);
		t = null;
		
		//check online state
		this.isOnline = false;
		conMgr =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = conMgr.getAllNetworkInfo();
		for (NetworkInfo netInf: netInfo) {
			if (netInf.isConnected()) {
				this.isOnline = true;
				break;
			}
		}
		//finished checking
		
		parser = new AkkHomepageEventParser(context, this);
		parser.updateEvents();
	}
	
	private void updateEvents() {
		if (this.isOnline) {
			if (updater.updateNeeded()) {
				t = new Thread(updater);
				t.run();
			} else {
				Log.d("InfoManager", "No updated required");
			}
		} else {
			Log.d("Updater", "Unable to update: no internet connection");
		}
	}


	public boolean readyToDisplayData() {
		if (this.eventsSortedByDate.size() < 10) {
			return false;
		}
		return true;
	}
	
	public boolean isInCalendar(AkkEvent event) {
		return true;//NICHT;)
	}
	
	public void addToCalendar(AkkEvent event) {
		calendar.addEvent(event);
	}

	public AkkEvent[] getEvents() {
		AkkEvent result[] = new AkkEvent[eventsSortedByDate.size()];
		for (int i = 0; i < eventsSortedByDate.size(); i++) {
			result[i] = eventsSortedByDate.get(i);
		}
		return result;
	}
	
	public void addEventToList(AkkEvent event){
		eventsSortedByDate.addLast(event);
	}
}
