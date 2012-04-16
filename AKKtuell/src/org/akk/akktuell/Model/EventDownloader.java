package org.akk.akktuell.Model;

import android.provider.ContactsContract.CommonDataKinds.Event;

/**
 * Event downloader interface.
 * @author Florian Muenchbach
 *
 */
public interface EventDownloader {
	/**
	 * Adds an {@link EventDownloadListener} to the list of listeners.
	 * @param listener the listener to add.
	 * @return true, if the listener has been added successfully.
	 */
	public boolean addEventDownloadListener(EventDownloadListener listener);

	/**
	 * Removes the given {@link EventDownloadListener} from the list of listeners.
	 * @param listener the listener to remove.
	 * @return true, if the listener has been removed successfully.
	 */
	public boolean removeEventDownloadListener(EventDownloadListener listener);

	/**
	 * Requests the start of the update of the events.
	 * If an update process is currently working, false will be returned, true otherwise.
	 * The attached listeners will be informed using the {@link EventDownloadListener#downloadStarted()}
	 * method.
	 * @return true, if the process can be started. This does not mean, it will be started immediately.
	 */
	public boolean updateEvents();

	/**
	 * Returns true, if an update has been requested.
	 * This can be used to avoid interrupting an update process (e.g. on App shutdown).
	 * @return true, if an update has been requested.
	 */
	public boolean isUpdating();
}
