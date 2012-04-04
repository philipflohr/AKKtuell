package org.akk.akktuell.Model;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public class InfoManager {
	
	private boolean isOnline;

	private CalendarBridge calendar;
	
	private ConnectivityManager conMgr;
	
	private Context applicationContext;
	
	public InfoManager(Context context) {
		applicationContext = context;
		calendar = new CalendarBridge();
		
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
		
		
	}
	
	private void updateEvents() {
		if (this.isOnline) {
			
		} else {
			Log.d("Updater", "Unable to update: no internet connection");
		}
	}


	public Cursor getData() {
		// TODO Auto-generated method stub
		return null;
	}


	public boolean readyToDisplayData() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isInCalendar(/*TODO: define arguments*/) {
		return true;
	}
	
	public void addToCalendar(/*TODO: define arguments*/) {
		calendar.addEvent(/*TODO: define arguments*/);
	}
}
