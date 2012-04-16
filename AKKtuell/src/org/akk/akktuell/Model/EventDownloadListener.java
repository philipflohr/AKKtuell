package org.akk.akktuell.Model;

public interface EventDownloadListener {
	public void appendEvent(AkkEvent event);
	public void downloadStarted();
	public void downloadFinished();
}
