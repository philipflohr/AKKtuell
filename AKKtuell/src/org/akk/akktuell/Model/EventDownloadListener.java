package org.akk.akktuell.Model;

/**
 * Listens to an {@link EventDownloader EventDownloader's} activities. 
 * @author Florian Muenchbach
 *
 */
public interface EventDownloadListener {
	/**
	 * Called when the download has been started.
	 */
	public void downloadStarted();
	/**
	 * Called when the download of all events has been finished.
	 * @param event the "fresh" event.
	 */
	public void downloadFinished(AkkEvent[] events);
}
