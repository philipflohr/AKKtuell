package org.akk.akktuell.Model;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import org.akk.akktuell.Activity.AKKtuellEventView;
import org.akk.akktuell.Activity.AKKtuellMainActivity;
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
	
	private AkkEvent[][] eventsPerMonthSortedByDate = new AkkEvent[12][];
	
	private int currentMonth = new GregorianCalendar().get(GregorianCalendar.MONTH);
	
	private int currentYear = new GregorianCalendar().get(GregorianCalendar.YEAR);
	
	private Thread updateManagerThread;
	
	private Handler viewHandler;
	
	private EventDownloadManager updateManager;
	
	public InfoManager(Context context, Handler viewUpdateHandler) throws DBException {
		this.viewHandler = viewUpdateHandler;
		applicationContext = context;
		database = Database.getInstance(context);

		database.open();
		updateEventLists();
		calendar = new CalendarBridge();

		//load initial list from db
		
		if (readyToDisplayData()) {
			this.viewHandler.sendEmptyMessage(0);
		}

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
		
		updateManager = EventDownloadManager.getInstance(context, this);
		updateManagerThread = new Thread(updateManager);
		if (this.isOnline) {
			updateManagerThread.start();
		} else {
			if (this.readyToDisplayData()) {
				//There is something to display... 
				viewHandler.sendEmptyMessage(1);
			} else {
				viewHandler.sendEmptyMessage(2);
			}
		}
	}
	
	private void updateEventLists() {
		for (int i = 0; i < 12; i++) {
			eventsPerMonthSortedByDate[i] = database.getAllEventsInMonth(i, currentYear, DBFields.EVENT_DATE, DBInterface.ASCENDING); 
		}
		
	}

	public boolean readyToDisplayData() {
		return (!(this.eventsPerMonthSortedByDate[currentMonth] == null) && !(this.eventsPerMonthSortedByDate[currentMonth].length == 0));
	}
	
	public boolean isInCalendar(AkkEvent event) {
		return true;//NICHT;)
	}
	
	public void addToCalendar(AkkEvent event) {
		calendar.addEvent(event);
	}

	public AkkEvent[] getEvents() {
		if (eventsPerMonthSortedByDate[currentMonth] == null || eventsPerMonthSortedByDate[currentMonth].length == 0) {
			AkkEvent[] result = new AkkEvent[1];
			result[0] = new AkkEvent("Sorry - No events in this month", new GregorianCalendar(), "Nowhere");
			return result;
		} else {
			return eventsPerMonthSortedByDate[currentMonth];
		}
	}
	
	
	
	public boolean setCurrentMonth(int month) {
		if (month >= 0 && month < 12) {
			currentMonth = month;
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
		synchronized (this) {		
			try {
				database.insertAkkEvents(events);
			} catch (DBException e) {
				//TODO Error message in gui
				e.printStackTrace();
			}
			updateEventLists();
			viewHandler.sendEmptyMessage(0);
		}
	}
	
	public void finish() {
		database.close();
	}
	
	public void setViewUpdateHandler(Handler updateHandler) {
		this.viewHandler = updateHandler;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void updateEvents() {
		if (!updateManagerThread.isAlive()) {
			updateManagerThread = new Thread(updateManager);
			updateManagerThread.start();
		}
	}
}
