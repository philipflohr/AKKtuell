package org.akk.akktuell.Model.downloader;

import java.util.ArrayList;

import android.content.Context;

public class EventDownloadManager implements EventDownloader {
	
	private ArrayList<EventDownloader> downloader = new ArrayList<EventDownloader>();
	private static EventDownloadManager instance = null;
	private int unsuccessfullDownloadAttempts = 0;
	private int downloadAttemptLimit = 0;
	private boolean untilSuccess = false;
	private EventDownloader currentDownloader = null;
	

	private EventDownloadManager(Context ctx) {
		//TODO settings file, initialization of downloaders...
		
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
	public boolean addEventDownloadListener(EventDownloadListener listener) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeEventDownloadListener(EventDownloadListener listener) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateEvents() {
		while (downloader.size() > 0 && (
				unsuccessfullDownloadAttempts <= downloadAttemptLimit
					|| untilSuccess)
				) {
			this.currentDownloader = downloader.get(unsuccessfullDownloadAttempts % this.downloader.size());

			if (this.currentDownloader.updateEvents()) {
				this.unsuccessfullDownloadAttempts = 0;
				return true;
			} else {
				unsuccessfullDownloadAttempts++;
			}			
		}
		return false;
	}

	@Override
	public boolean isUpdating() {
		return (currentDownloader != null) ? currentDownloader.isUpdating() : false;
	}

	private class Downloader {
		private final EventDownloader downloader;
		private int lastUsed = -1;
		private boolean wasOnline = false;

		public Downloader(EventDownloader downloader) {
			this.downloader = downloader;
		}

		
		
	}
}
