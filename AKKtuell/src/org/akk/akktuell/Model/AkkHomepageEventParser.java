package org.akk.akktuell.Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.regex.Pattern;

import org.akk.akktuell.R;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Paint.Join;
import android.util.Log;

public class AkkHomepageEventParser implements Runnable {
	
	private LinkedList<AkkEvent> eventsWaitingForDescription;
	private LinkedList<AkkEvent> eventsWaitingForDBPush;
	private ThreadGroup getDescThreads;
	private Context context;
	private InfoManager infoManager;
	
	public AkkHomepageEventParser(Context ctx, InfoManager infoManager) {
		this.context = ctx;
		this.infoManager = infoManager;
		this.eventsWaitingForDescription = new LinkedList<AkkEvent>();
		this.eventsWaitingForDBPush = new LinkedList<AkkEvent>();
		getDescThreads = new ThreadGroup("EventUpdateThreads");
		for (int i = 0; i < 3; i++) {
			new Thread(getDescThreads, this).start();
		}
	}
	
	private String getAkkHpSource() throws IOException {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet("http://www.akk.org/chronologie.php");
		HttpResponse response = client.execute(request);

		InputStream in = response.getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder str = new StringBuilder();
		String line = null;
		while((line = reader.readLine()) != null)
		{
		    str.append(line);
		}
		in.close();
		return str.toString();
	}
	
	public void updateEvents() {
		String htmlSource;
		try {
			htmlSource = getAkkHpSource();
		} catch (IOException e) {
			Log.d("AkkHomepageParser", "error while downloading akk source code");
			e.printStackTrace();
			AkkEvent[] result = new AkkEvent[1];
			result[0] = new AkkEvent("Error while downloading", null,"test");
			this.addElementToDBPushList(result[0]);
			return;
		}
		
		LinkedList<AkkEvent> events = new LinkedList<AkkEvent>();
		
		LinkedList<String> singleEventhtmlSource = getSingleEventSources(htmlSource);
		
		//remove first part
		singleEventhtmlSource.removeFirst();
		
		//save last part for last_modified string
		String htmlContainingLastModified = singleEventhtmlSource.getLast();
		singleEventhtmlSource.removeLast();
		
		
		//remove part after "</TD></TR>"
		for (int i = 0; i < singleEventhtmlSource.size(); i++) {
			String currentLine = singleEventhtmlSource.get(i).split("</TD></TR>")[0];
			singleEventhtmlSource.remove(i);
			singleEventhtmlSource.add(i, currentLine);
			//equivalent to string.matches but faster for multiple operations
			
			
		}
		
		/*
		 * example source String:
		 * 	<TR><TD>Do. 19. Apr.</TD><TD>20<SPAN class="min-alt">:</SPAN><SPAN class="min">00</SPAN> Uhr</TD><TD>
        	<A HREF="/schlonze/schlonz.php?Kochduell">Kochduell Schlonz</A></TD><TD><A HREF="/adresse.php">Altes Stadion</A></TD></TR>
		 
		 *
		 *ohne desc:
		 *
		 *<TR><TD>Di. 10. Jul.</TD><TD>20<SPAN class="min-alt">:</SPAN><SPAN class="min">00</SPAN> Uhr</TD><TD>
        	Reggae-Ska-Punk-Trash Schlonz</TD><TD><A HREF="/adresse.php">Altes Stadion</A></TD></TR>

		 */
		 
		
		//produce events from the lines
		AkkEvent newAkkEvent;
		String newAkkEventName;
		String newAkkEventPlace;
		Boolean hasDescription;
		GregorianCalendar newAkkEventDate;
		for (String currentEventString : singleEventhtmlSource) {
			newAkkEventDate = getEventDateFromString(currentEventString.substring(0,11));
			if (currentEventString.contains("HREF=\"/schlonze")) {
				hasDescription = true;
			} else {
				hasDescription = false;
			}
			
			
			
			try {
				if (hasDescription) {
					String source = currentEventString.split("/schlonze")[1];
					newAkkEventName = source.split("\">")[1];
					newAkkEventName = newAkkEventName.split("<")[0];
					
					newAkkEventPlace = source.split("adresse.php\">")[1];
					newAkkEventPlace = newAkkEventPlace.split("<")[0];
				
					newAkkEvent = new AkkEvent(newAkkEventName, newAkkEventDate, newAkkEventPlace);
					newAkkEvent.setDescription(currentEventString);
					this.addElementToWaitingList(newAkkEvent);
				} else {
					String source = currentEventString.split("</SPAN>")[2];
					newAkkEventName = source.split("</TD><TD>")[1];
				
					newAkkEventPlace = source.split("adresse.php\">")[1];
					newAkkEventPlace = newAkkEventPlace.split("<")[0];
				
					newAkkEvent = new AkkEvent(newAkkEventName, newAkkEventDate, newAkkEventPlace);
					newAkkEvent.setDescription(context.getResources().getString(R.string.no_description_available));
					this.addElementToDBPushList(newAkkEvent);
				}
				synchronized (this) {
					notify();
				}
				
			} catch (ArrayIndexOutOfBoundsException e) {
				Log.d("HPParser", "Seems this is not a normal String: " + currentEventString);
				e.printStackTrace();
			}
		}
		
	}

	private GregorianCalendar getEventDateFromString(String substring) {
		// TODO Auto-generated method stub
		return null;
	}

	private LinkedList<String> getSingleEventSources(String htmlSource) {
		LinkedList<String> returnStrings = new LinkedList<String>();
		String[] eventSourceSequence = htmlSource.split("<TR><TD>");
		for (String s : eventSourceSequence) {
			returnStrings.addLast(s);
		}
		return returnStrings;
	}
	
	
	public void run() {
		AkkEvent event;
		while (true) {
			if (this.elementsWaitingForDesc()) {
				Log.d("Thread:" + Thread.currentThread().toString(), "alive");
				event = this.popElementFromwaitingList();
				this.addElementToDBPushList(event);
			} else if (this.elementsWaitingForDBPush()){
				event = popElementFromDBPushList();
				infoManager.addEventToList(event);
			} else {
				try {
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException e) {
					Log.d("AkkHomepageParse", "Thread got InterrupterException.");
				}
			}
		}	
	}
	
	private boolean elementsWaitingForDBPush() {
		synchronized (this) {
			return !this.eventsWaitingForDBPush.isEmpty();
		}
	}

	private boolean elementsWaitingForDesc() {
		synchronized (this) {
			return !this.eventsWaitingForDescription.isEmpty();
		}
	}

	private void addElementToWaitingList(AkkEvent event) {
		synchronized (this) {
			this.eventsWaitingForDescription.addLast(event);
		}
	}
	
	private AkkEvent popElementFromwaitingList() {
		AkkEvent event;
		synchronized(this) {
			 event = this.eventsWaitingForDescription.getFirst();
			this.eventsWaitingForDescription.removeFirst();
		}
		return event;
	}
	
	private void addElementToDBPushList(AkkEvent event) {
		synchronized (this) {
			this.eventsWaitingForDBPush.addLast(event);
		}
	}
	
	private AkkEvent popElementFromDBPushList() {
		AkkEvent event;
		synchronized (this) {
			event = this.eventsWaitingForDBPush.getFirst();
			this.eventsWaitingForDBPush.removeFirst();
		}
		return event;
	}

}
