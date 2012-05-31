package org.akk.akktuell.Model.downloader;

import java.util.ArrayList;

import org.akk.akktuell.Model.AkkEvent;
import org.akk.akktuell.Model.InfoManager;

import android.content.Context;
import android.util.Log;

public class EventDownloadManager implements Runnable {
	
	private static EventDownloadManager instance = null;
	private int unsuccessfullDownloadAttempts = 0;
	private int downloadAttemptLimit = 0;
	private boolean untilSuccess = false;
	private int currentDownloader = 0;
	private ArrayList<EventDownloadListener> listener =
			new ArrayList<EventDownloadListener>();
	private ArrayList<Download> downloads = new ArrayList<Download>();
	
	private EventDownloader[] downloader = new EventDownloader[2];
	
	/** JSON Constant. This is used to identify JSON Download links as well as
	 * the array position of the JSON Downloader in the downloaders array.*/
	//TODO: Zahlen wieder drehen, wenn JSON parser fertig
	private static final int JSON = 1;
	private static final int HTML = 0;

	

	private EventDownloadManager(Context ctx, InfoManager infoManager) {
		//TODO settings file, initialization of downloaders...
		//TODO testing
		//AkkHomepageEventParser parser = new AkkHomepageEventParser(ctx);
		//parser.addEventDownloadListener(infoManager);
		//downloader.add(parser);
//		for (String json : getJsonLinks())
//			this.downloads.add(new Download(json, JSON));
		for (String html : getHtmlLinks())
			this.downloads.add(new Download(html, HTML));

		//TODO this.downloader[JSON] = new ;
		this.downloader[HTML] = new AkkHomepageEventParser(ctx);
		
	}

	public static EventDownloadManager getInstance(Context ctx, InfoManager infomanager) {
		if (instance == null) {
			instance = new EventDownloadManager(ctx, infomanager);
		}
		return instance;
	}

	private String[] getJsonLinks() {
		return new String[]{
				"https://studwww.ira.uni-karlsruhe.de/~s_vielsa/scripts/schlonze.php"
			};
	}

	private String[] getHtmlLinks() {
		return new String[] {
				"http://www.akk.org/chronologie.php"
			};
	}

	public AkkEvent[] updateEvents() {
		if (this.listener.size() <= 0) {
			Log.w("AKKtuell", "EventDownloadManager: You should better register some listeners first," +
					"no one cares about the stuff I am downloading...");
			return null;
		}
		
		while (downloads.size() > 0 && (
				unsuccessfullDownloadAttempts <= downloadAttemptLimit
					|| untilSuccess)
				) {
			Download download = downloads.get(0);
			//		unsuccessfullDownloadAttempts % this.downloads.size());

			this.currentDownloader = download.getDownloadType();

			this.downloader[this.currentDownloader].setUrl(download.getLink());

			//TODO exception
			AkkEvent[] events = this.downloader[this.currentDownloader].updateEvents();
			download.setLastUsedTimestamp(System.currentTimeMillis());
			
			if (events != null) {
				this.unsuccessfullDownloadAttempts = 0;
				download.setOnlineStatus(true);
				return events;
			} else {
				download.setOnlineStatus(false);
				unsuccessfullDownloadAttempts++;
			}			
		}
		return null;
	}

	public boolean isUpdating() {
		try {
			return (this.downloader[this.currentDownloader] != null) ? 
					this.downloader[this.currentDownloader].isUpdating() : false;
		} catch (LinkNotSetException e) {
			Log.d("AKKtuell", "EventDownloadManager: Link not set...");
			return false;
		}
	}

	private class Download {
		
		private final String link;
		private long lastUsed = -1;
		private final int type;
		private boolean wasOnline = false;

		public Download(String link, int type) {
			this.link = link;
			this.type = type;
		}


		public void setLastUsedTimestamp(long currentTimeMillis) {
			this.lastUsed = currentTimeMillis;
		}


		public void setOnlineStatus(boolean b) {
			this.wasOnline = b;
		}


		public String getLink() {
			return this.link;
		}


		public int getDownloadType() {
			return this.type;
		}

		public long getLastUsedTimestamp() {
			return this.lastUsed;
		}

		public boolean wasOnlineOnLastTry() {
			return this.wasOnline;
		}

		
	}

	public void addEventDownloadListener(EventDownloadListener downloadListener) {
		this.listener.add(downloadListener);
	}

	/**
	 * Notifies the attached listeners when a download has started.
	 */
	private void notifyOnDownloadStarted() {
		for (EventDownloadListener l : this.listener) {
			l.downloadStarted();
		}
	}

	/**
	 * Notifies the attached listeners when the download has finished
	 * and returns the downloaded {@link AkkEvent AkkEvents}.
	 * @param events the downloaded events
	 */
	private void notifyOnDownloadFinished(AkkEvent[] events) {
		for (EventDownloadListener l : this.listener) {
			l.downloadFinished(events);
		}
	}

	
	public void setDownloadAttemptLimit(int limit) {
		this.downloadAttemptLimit = (limit > 1) ? limit : 1;
	}

	public void tryToDownloadUntilSuccess(boolean untilSuccess) {
		this.untilSuccess = untilSuccess;
	}

	@Override
	public void run() {
		notifyOnDownloadStarted();
		notifyOnDownloadFinished(updateEvents());
	}
}
