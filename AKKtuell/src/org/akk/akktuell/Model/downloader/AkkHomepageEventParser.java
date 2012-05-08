package org.akk.akktuell.Model.downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import org.akk.akktuell.R;
import org.akk.akktuell.Model.AkkEvent;
import org.akk.akktuell.Model.AkkEvent.AkkEventType;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.text.Html;
import android.util.Log;

public class AkkHomepageEventParser implements Runnable, EventDownloader {
	
	private LinkedList<AkkEvent> eventsWaitingForDescription;
	private LinkedList<AkkEvent> eventsWaitingForDBPush;
	private ThreadGroup getDescThreads;
	private Context context;
	private boolean updateRequested = false;
	private ArrayList<EventDownloadListener> listeners = new ArrayList<EventDownloadListener>();
	private Thread mainThread;
	private boolean allEventsParsed;
	private String AkkHpAddr = "http://www.akk.org/chronologie.php";
	private String AkkWsAddr= "http://www.akk.org/workshops/index.php";
	
	public AkkHomepageEventParser(Context ctx) {
		mainThread = Thread.currentThread();

		this.context = ctx;
		this.eventsWaitingForDescription = new LinkedList<AkkEvent>();
		this.eventsWaitingForDBPush = new LinkedList<AkkEvent>();
		getDescThreads = new ThreadGroup("EventUpdateThreads");
	}
	
	private String getAkkHpSource() throws IOException {
		return getSource(AkkHpAddr);
		
	}
	
	private String getWorkshopSource() throws IOException {
		return getSource(AkkWsAddr);
	} 
	
	private String getDescriptionSource(String link) throws IOException {
		return getSource(link);
	}
	
	private String getSource(String add) throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(add);
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
	
	
	@Override
	public AkkEvent[] updateEvents() {
		if (!this.updateRequested) {
			this.updateRequested = true;
			allEventsParsed = false;
			
			for (int i = 0; i < 3; i++) {
				new Thread(getDescThreads, this).start();
			}
			
			
			String htmlSource;
			String workshopSource;
			try {
				htmlSource = getAkkHpSource();
				workshopSource = getWorkshopSource();
			} catch (IOException e) {
				Log.d("AkkHomepageParser", "error while downloading akk source code");
				e.printStackTrace();
				return null;
			}
			
			LinkedList<String> singleEventhtmlSource = getSingleEventSources(htmlSource);
			LinkedList<String> singleWorkshopNamesAndLinks = getWorkshopNamesAndLinks(workshopSource);
			
			if (singleEventhtmlSource.size() < 3) {
				//this is not a correct version of the akk homepage
				return null;
			}
			
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
			 
			
			//produce events from the lines
			AkkEvent newAkkEvent;
			String newAkkEventName;
			String newAkkEventPlace;
			Boolean hasDescription;
			GregorianCalendar newAkkEventDate;
			AkkEvent.AkkEventType newAkkEventType;
			for (String currentEventString : singleEventhtmlSource) {
				
				//check event type
				if (currentEventString.contains("Veranstaltungshinweis")) {
					newAkkEventType = AkkEventType.Veranstaltungshinweis;
				} else if (currentEventString.contains("Sonderveranstaltung")) {
					newAkkEventType = AkkEventType.Sonderveranstaltung;
				} else if (currentEventString.contains("Workshop")) {
					newAkkEventType = AkkEventType.Workshop;
				} else if (currentEventString.contains("Schlonz") || currentEventString.contains("Liveschlonz")) {
					newAkkEventType = AkkEventType.Schlonz;
				} else {
					newAkkEventType = AkkEventType.Tanzen;
				}
				
				//get eventDate
				newAkkEventDate = getEventDateFromString(currentEventString.substring(0,11));
				
				//parse schlonze
				
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
				if (newAkkEventType == AkkEventType.Schlonz) {
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
							newAkkEventName = Html.fromHtml(newAkkEventName).toString();
							
							newAkkEventPlace = source.split("adresse.php\">")[1];
							newAkkEventPlace = newAkkEventPlace.split("<")[0];
						
							newAkkEvent = new AkkEvent(newAkkEventName, newAkkEventDate, newAkkEventPlace);
							newAkkEvent.setDescription(currentEventString);
							newAkkEvent.setType(AkkEventType.Schlonz);
							this.addElementToWaitingList(newAkkEvent);
						} else {
							String source = currentEventString.split("</SPAN>")[2];
							newAkkEventName = source.split("</TD><TD>")[1];
							newAkkEventName = Html.fromHtml(newAkkEventName).toString();
						
							newAkkEventPlace = source.split("adresse.php\">")[1];
							newAkkEventPlace = newAkkEventPlace.split("<")[0];
						
							newAkkEvent = new AkkEvent(newAkkEventName, newAkkEventDate, newAkkEventPlace);
							newAkkEvent.setDescription(context.getResources().getString(R.string.hello));
							newAkkEvent.setType(AkkEventType.Schlonz);
						}
						
						
					} catch (ArrayIndexOutOfBoundsException e) {
						Log.d("HPParser", "Seems this is not a normal String: " + currentEventString);
						e.printStackTrace();
					}
				} else if (newAkkEventType == AkkEventType.Veranstaltungshinweis) {
					/*example source
					 * Mo. 16. Apr.</TD><TD></TD><TD>	Veranstaltungshinweis: Rektor: Vorlesungsbeginn</TD><TD><A HREF="http://www.uni-karlsruhe.de/info/campusplan/">Campus</A>
					 * 
					 * 
					 * Fr. 6. Jul.</TD><TD>15<SPAN class="min-alt">:</SPAN><SPAN class="min">00</SPAN> Uhr</TD><TD>
	        			<A HREF="http://www.z10.info/">Veranstaltungshinweis: Z10: Sommerfest</A></TD><TD><A HREF="http://www.z10.info/?topic">Z10</A></TD></TR>
					 
					 *	Sa. 21. Apr.</TD><TD>20<SPAN class="min-alt">:</SPAN><SPAN class="min">00</SPAN> Uhr</TD><TD>	<A HREF="http://www.z10.info/">Veranstaltungshinweis: Z10: Konzert - Montreal, Liedfett, Ill</A></TD><TD><A HREF="http://www.z10.info/?topic">Z10</A>
					 */
					
					if (currentEventString.contains("Rektor")) {
						
					} else {
						if (currentEventString.endsWith("</A>")) {
							String source = currentEventString.split("\">")[3];
							newAkkEventName = source.split("</A>")[0];
							newAkkEventName = Html.fromHtml(newAkkEventName).toString();
							
							String [] substrings = currentEventString.split("\">");
							newAkkEventPlace = substrings[substrings.length -1];
							newAkkEventPlace = newAkkEventPlace.substring(0, newAkkEventPlace.length()-4);
						} else {
							newAkkEventName = currentEventString.split("</TD><TD>")[2];;
							newAkkEventName = Html.fromHtml(newAkkEventName).toString();
							
							newAkkEventPlace = currentEventString.split("</TD><TD>")[3];
						}
						if (newAkkEventName.startsWith("\t")) {
							newAkkEventName = newAkkEventName.substring(1, newAkkEventName.length() -1);
						}
						newAkkEvent = new AkkEvent(newAkkEventName, newAkkEventDate, newAkkEventPlace);
						newAkkEvent.setDescription(context.getResources().getString(R.string.hello));
						newAkkEvent.setType(AkkEventType.Veranstaltungshinweis);
						this.addElementToDBPushList(newAkkEvent);
					}
				} else if (newAkkEventType == AkkEventType.Workshop) {
					/*EXAMPLE STRINGS:
					  	D/Example Strings(  978): Di. 8. Mai.</TD><TD>15<SPAN class="min-alt">:</SPAN><SPAN class="min">00</SPAN> Uhr</TD><TD><A HREF="/workshops/">Workshop: Pr&uuml;fungsangst</A></TD><TD>
						D/Example Strings(  978): Sa. 12. Mai.</TD><TD>11<SPAN class="min-alt">:</SPAN><SPAN class="min">00</SPAN> Uhr</TD><TD><A HREF="/workshops/">Workshop: Photoworkshop f&uuml;r Anf&auml;nger</A></TD><TD>
						D/Example Strings(  978): Sa. 12. Mai.</TD><TD>13<SPAN class="min-alt">:</SPAN><SPAN class="min">00</SPAN> Uhr</TD><TD><A HREF="/workshops/">Workshop: Tango Argentino f&uuml;r Anf&auml;nger</A></TD><TD>
						D/Example Strings(  978): Sa. 19. Mai.</TD><TD>11<SPAN class="min-alt">:</SPAN><SPAN class="min">00</SPAN> Uhr</TD><TD><A HREF="/workshops/">Workshop: Massage - Ein sinnliches Erlebnis</A></TD><TD>
						D/Example Strings(  978): Sa. 2. Jun.</TD><TD>10<SPAN class="min-alt">:</SPAN><SPAN class="min">00</SPAN> Uhr</TD><TD><A HREF="/workshops/">Workshop: Salsa f&uuml;r Anf&auml;nger</A></TD><TD>
						D/Example Strings(  978): Sa. 2. Jun.</TD><TD>11<SPAN class="min-alt">:</SPAN><SPAN class="min">00</SPAN> Uhr</TD><TD><A HREF="/workshops/">Workshop: Massage - Ein sinnliches Erlebnis</A></TD><TD>
						D/Example Strings(  978): Sa. 2. Jun.</TD><TD>13<SPAN class="min-alt">:</SPAN><SPAN class="min">00</SPAN> Uhr</TD><TD><A HREF="/workshops/">Workshop: Tango Argentino f&uuml;r Fortgeschrittene</A></TD><TD>
						D/Example Strings(  978): So. 3. Jun.</TD><TD>13<SPAN class="min-alt">:</SPAN><SPAN class="min">00</SPAN> Uhr</TD><TD><A HREF="/workshops/">Workshop: F&uuml;hrungsakademie</A></TD><TD>
						D/Example Strings(  978): Mo. 4. Jun.</TD><TD>18<SPAN class="min-alt">:</SPAN><SPAN class="min">00</SPAN> Uhr</TD><TD><A HREF="/workshops/">Workshop: Linux kennenlernen</A></TD><TD>
					 */
					
					//cut the last 12 charaters
					currentEventString = currentEventString.substring(0, currentEventString.length() - 13);
					String[] splittStrings = currentEventString.split("/\">");
					newAkkEventName = splittStrings[splittStrings.length - 1];
					newAkkEventName = Html.fromHtml(newAkkEventName).toString();
					newAkkEventPlace = "This is s good question - but the website has no answer for this";
					newAkkEvent = new AkkEvent(newAkkEventName, newAkkEventDate, newAkkEventPlace);
					newAkkEvent.setType(newAkkEventType);
					
					//get link and set as description
					for (String s: singleWorkshopNamesAndLinks) {
						String s_ASCII = Html.fromHtml(s).toString();
						if (s_ASCII.contains(newAkkEventName.substring(10, newAkkEventName.length() - 1))) {
							newAkkEvent.setDescription("http://www.akk.org/workshops/" + s.split("\">")[0]);
							break;
						}
					}
					if (newAkkEvent.getEventDescription() == null || newAkkEvent.getEventDescription().length() == 0) {
						newAkkEvent.setDescription("Nothing about this...");
						this.addElementToDBPushList(newAkkEvent);
					} else {
						this.addElementToWaitingList(newAkkEvent);
					}
				}
			}
			
			synchronized (this) {
				notify();
			}
			allEventsParsed = true;
			while(elementsWaitingForDesc() || getDescThreads.activeCount() > 0) {
				synchronized (this) {
					try {
						wait(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			if (this.eventsWaitingForDBPush.size() == 0) {
				return null;
			}
			
			AkkEvent[] result = new AkkEvent[this.eventsWaitingForDBPush.size()];
			for (int i = 0; i < eventsWaitingForDBPush.size(); i++) {
				result[i] = eventsWaitingForDBPush.get(i);
			}
			notifyOnDownloadFinished(result);
			return result;
		}
		return null;
	}



	private LinkedList<String> getWorkshopNamesAndLinks(String workshopSource) {
		workshopSource = workshopSource.split("<TABLE class=\"workshops\">")[1];
		workshopSource = workshopSource.split("</TABLE>")[0];
		LinkedList<String> result = new LinkedList<String>();
		String[] splittedSource = workshopSource.split("HREF=\"");
		
		for (int i = 1; i < splittedSource.length; i++) {
			result.add(Html.fromHtml(splittedSource[i].split("</A></TD>")[0]).toString());
		}
		
		return result;
	}

	private GregorianCalendar getEventDateFromString(String substring) {
		GregorianCalendar calendar = new GregorianCalendar();
		if (substring.contains("Jan")) {
			calendar.set(GregorianCalendar.MONTH, 0);
		} else if (substring.contains("Feb")) {
			calendar.set(GregorianCalendar.MONTH, 1);
		} else if (substring.contains("Mar")) {
			calendar.set(GregorianCalendar.MONTH, 2);
		} else if (substring.contains("Apr")) {
			calendar.set(GregorianCalendar.MONTH, 3);
		} else if (substring.contains("Mai")) {
			calendar.set(GregorianCalendar.MONTH, 4);
		} else if (substring.contains("Jun")) {
			calendar.set(GregorianCalendar.MONTH, 5);
		} else if (substring.contains("Jul")) {
			calendar.set(GregorianCalendar.MONTH, 6);
		} else if (substring.contains("Aug")) {
			calendar.set(GregorianCalendar.MONTH, 7);
		} else if (substring.contains("Sep")) {
			calendar.set(GregorianCalendar.MONTH, 8);
		} else if (substring.contains("Okt")) {
			calendar.set(GregorianCalendar.MONTH, 9);
		} else if (substring.contains("Nov")) {
			calendar.set(GregorianCalendar.MONTH, 10);
		} else if (substring.contains("Dez")) {
			calendar.set(GregorianCalendar.MONTH, 11);
		} else {
			Log.d("Halt", "STOP!");
		}
		return calendar;
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
		AkkEvent event = null;
		while (true) {
			if (this.elementsWaitingForDesc()) {
				synchronized (this) {
					if (this.elementsWaitingForDesc()) {
						event = this.popElementFromwaitingList();
					}
				}
				if (event != null) {
					addDescriptionToEvent(event);
					this.addElementToDBPushList(event);
				}
			} else {
				synchronized (mainThread) {
					if (mainThread.getState() == Thread.State.WAITING) {
						mainThread.notify();
					}
				}
				if (!allEventsParsed) {
					synchronized (this) {
						try {
							wait(500);
						} catch (InterruptedException e) {
							Log.d("AkkHomepageParse", "Thread got InterrupterException.");
						} 
					}
				} else {
					break;
				}
			}
		}	
	}

	/**
	 * Notifies the attached listeners when a download has started.
	 */
	private void notifyOnDownloadStarted() {
		for (EventDownloadListener l : this.listeners) {
			l.downloadStarted();
		}
	}

	/**
	 * Notifies the attached listeners when the download has finished
	 * and returns the downloaded {@link AkkEvent AkkEvents}.
	 * @param events the downloaded events
	 */
	private void notifyOnDownloadFinished(AkkEvent[] events) {
		for (EventDownloadListener l : this.listeners) {
			l.downloadFinished(events);
		}
	}
	
	private void addDescriptionToEvent(AkkEvent event) {
		if (event.getEventType() == AkkEventType.Schlonz) {
			String eventSource = event.getEventDescription();
			eventSource = eventSource.split("<A HREF=\"")[1];
			String eventDescriptionSource = eventSource.split("\">")[0];
			eventDescriptionSource = "http://www.akk.org" + eventDescriptionSource;
			try {
				eventDescriptionSource = getDescriptionSource(eventDescriptionSource);
				String eventDescription = eventDescriptionSource.split("<P>")[1];
				eventDescription = Html.fromHtml(eventDescription.split("</P>")[0]).toString();
				event.setDescription(eventDescription);
			} catch (IOException e) {
				Log.d("HPParser", "Could not get event Description...");
				e.printStackTrace();
				event.setDescription("Error fetching Description");
				return;
			} 
		} else if (event.getEventType() == AkkEventType.Workshop) {
			String descSource = null;
			try {
				descSource = getSource(event.getEventDescription());
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			descSource = descSource.split("<div class=\"beschreibung\">")[1];
			descSource = descSource.split("</div>")[0];
			event.setDescription(Html.fromHtml(descSource).toString());
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
	
	@Override
	public boolean isUpdating() {
		return this.updateRequested;
	}

	@Override
	public void setUrl(String url) {
		this.AkkHpAddr = url;
	}
	

	@Override
	public void addEventDownloadListener(EventDownloadListener infoManager) {
		listeners.add(infoManager);		
	}

}
