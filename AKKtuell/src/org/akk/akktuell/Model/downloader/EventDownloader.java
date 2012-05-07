package org.akk.akktuell.Model.downloader;

import org.akk.akktuell.Model.AkkEvent;
import org.akk.akktuell.Model.InfoManager;

import android.provider.ContactsContract.CommonDataKinds.Event;

/**
 * Event downloader interface.
 * @author Florian Muenchbach
 *
 */
public interface EventDownloader {
	/**
	 * Starts the update process and returns the events found.
	 * If null is returned, something went wrong (not reachable, etc.)
	 * @return the events found.
	 */
	//TODO void
	public AkkEvent[] updateEvents();

	/**
	 * Returns true, if an update has been requested.
	 * This can be used to avoid interrupting an update process (e.g. on App shutdown).
	 * @return true, if an update has been requested.
	 */
	public boolean isUpdating() throws LinkNotSetException;

	/**
	 * Sets the URL pointing to the address to use.
	 * This has to be called at least once before using the {@link #updateEvents()} method.
	 * @param url the url to use.
	 */
	public void setUrl(String url);

	public void addEventDownloadListener(EventDownloadListener listener);
}
