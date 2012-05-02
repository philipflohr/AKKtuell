package org.akk.akktuell.Model;

import java.util.GregorianCalendar;
import java.util.LinkedList;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.akk.akktuell.Activity.AKKtuellEventView;
import org.akk.akktuell.Model.downloader.AkkHomepageEventParser;
import org.akk.akktuell.Model.downloader.EventDownloadListener;
import org.akk.akktuell.Model.downloader.EventDownloadManager;
import org.akk.akktuell.Model.downloader.EventDownloader;
import org.akk.akktuell.database.*;


public class InfoManager implements EventDownloadListener {
	
	private boolean isOnline;

	private CalendarBridge calendar;
	
	private ConnectivityManager conMgr;
	
	private Context applicationContext;
	
	private EventDownloader parser;
	
	private Database database;
	
	private AkkEvent[] eventsSortedByDate = null;
	
	private int currentMonth = new GregorianCalendar().get(GregorianCalendar.MONTH);
	
	private int currentYear = new GregorianCalendar().get(GregorianCalendar.YEAR);
	
	public InfoManager(Context context) {
		applicationContext = context;
		database = Database.getInstance(context);

		try {
			database.open();
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		calendar = new CalendarBridge();

		//load initial list from db
		eventsSortedByDate = database.getAllEvents(DBFields.EVENT_DATE, DBInterface.ASCENDING);

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
		
		if (this.isOnline) {
			EventDownloadManager updateManager = EventDownloadManager.getInstance(context);
			Thread updateManagerThread = new Thread(updateManager);
			updateManagerThread.start();
		}
	}
	
//	private void updateEvents() {
//		if (this.isOnline) {
//			if (updater.updateNeeded()) {
//				t = new Thread(updater);
//				t.run();
//			} else {
//				Log.d("InfoManager", "No updated required");
//			}
//		} else {
//			Log.d("Updater", "Unable to update: no internet connection");
//		}
//	}


	public boolean readyToDisplayData() {
		return (!(this.eventsSortedByDate == null) && !(this.eventsSortedByDate.length == 0));
	}
	
	public boolean isInCalendar(AkkEvent event) {
		return true;//NICHT;)
	}
	
	public void addToCalendar(AkkEvent event) {
		calendar.addEvent(event);
	}

	public AkkEvent[] getEvents() {
		LinkedList<AkkEvent> resultList = new LinkedList<AkkEvent>();
		for (AkkEvent event : eventsSortedByDate) {
			if (event.getEventBeginTime().get(GregorianCalendar.MONTH) == currentMonth) {
				resultList.add(event);
			}
		}
		AkkEvent result[] = new AkkEvent[resultList.size()];
		for (int i = 0; i < resultList.size(); i++) {
			result[i] = resultList.get(i);
		}
		this.eventsSortedByDate = database.getAllEventsInMonth(currentMonth, 
				currentYear, DBFields.EVENT_DATE, DBInterface.DESCENDING);
		return result;
	}
	
	public boolean setCurrentMonth(int month) {
		if (month >= 0 && month < 12) {
			currentMonth = month;
			this.eventsSortedByDate = null;
			return true;
		}
		return false;
	}

	@Override
	public void downloadStarted() {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void downloadFinished(AkkEvent[] events) {
		AkkEvent currentEvent;
		boolean wasInList;
		synchronized (this) {
//			for (int i = 0; i < events.length; i++) {
//				currentEvent = events[i];
//				wasInList = false;
//				for (int j = 0; j < eventsSortedByDate.length; j++) {
//					if (currentEvent.equals(eventsSortedByDate[j])) {
//						wasInList = true;
//						break;
//					}
//				}
//				if (!wasInList) {
//					try {
//						database.insertAkkEvent(currentEvent);
//						eventsSortedByDate = database.getAllEvents(DBFields.EVENT_DATE, DBInterface.ASCENDING);
//					} catch (DBException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//			
			try {
				database.insertAkkEvents(events);
			} catch (DBException e) {
				//TODO Error message in gui
				e.printStackTrace();
			}
		}
	}
	
	public void finish() {
		database.close();
	}
}
