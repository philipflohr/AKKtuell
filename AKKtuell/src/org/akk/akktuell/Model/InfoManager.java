package org.akk.akktuell.Model;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


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


	public Cursor getData() {
		// TODO Auto-generated method stub
		return null;
	}


	public boolean readyToDisplayData() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
