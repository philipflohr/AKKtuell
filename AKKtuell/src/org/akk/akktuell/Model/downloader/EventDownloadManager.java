package org.akk.akktuell.Model.downloader;

import java.util.ArrayList;

import org.akk.akktuell.Model.AkkEvent;
import org.akk.akktuell.Model.InfoManager;

import android.content.Context;
import android.util.Log;

public class EventDownloadManager implements EventDownloader, Runnable {
	
	private ArrayList<EventDownloader> downloader = new ArrayList<EventDownloader>();
	private static EventDownloadManager instance = null;
	private int unsuccessfullDownloadAttempts = 0;
	private int downloadAttemptLimit = 0;
	private boolean untilSuccess = false;
	private EventDownloader currentDownloader = null;
	

	private EventDownloadManager(Context ctx) {
		//TODO settings file, initialization of downloaders...
		//TODO testing
		downloader.add(new AkkHomepageEventParser(ctx));
		
	}

	public static EventDownloadManager getInstance(Context ctx) {
		if (instance == null) {
			instance = new EventDownloadManager(ctx);
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

	@Override
	public AkkEvent[] updateEvents() {
		while (downloader.size() > 0 && (
				unsuccessfullDownloadAttempts <= downloadAttemptLimit
					|| untilSuccess)
				) {
			this.currentDownloader = downloader.get(unsuccessfullDownloadAttempts % this.downloader.size());

			if (this.currentDownloader.updateEvents() != null) {
				this.unsuccessfullDownloadAttempts = 0;
				return null;
			} else {
				unsuccessfullDownloadAttempts++;
			}			
		}
		return null;
	}

	@Override
	public boolean isUpdating() {
		try {
			return (currentDownloader != null) ? currentDownloader.isUpdating() : false;
		} catch (LinkNotSetException e) {
			Log.d("EventDownloadManager", "Link not set...");
			return false;
		}
	}

	private class Downloader {
		private final EventDownloader downloader;
		private int lastUsed = -1;
		private boolean wasOnline = false;

		public Downloader(EventDownloader downloader) {
			this.downloader = downloader;
		}

		
		
	}

	@Override
	public void setUrl(String url) {
		// nothing to do?
		
	}

	@Override
	public void addEventDownloadListener(InfoManager infoManager) {
		//testing
		for (EventDownloader ed : downloader) {
			ed.addEventDownloadListener(infoManager);
		}
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		downloader.get(0).updateEvents();
	}
}
