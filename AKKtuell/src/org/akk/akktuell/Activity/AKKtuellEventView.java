package org.akk.akktuell.Activity;

import java.io.File;

import org.akk.akktuell.R;
import org.akk.akktuell.toolkit.Tools;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * The Class AKKtuellEventView. It displays the information given to it via an intent
 * 
 * @author Philip Flohr
 */
public class AKKtuellEventView extends Activity {

	/** The tools. */
	private Tools tools;
	
	/** The is in calendar. */
	private boolean isInCalendar;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.tools = Tools.getInstance(this);
		setupView();
	}

	/**
	 * Setup view.
	 */
	private void setupView() {
		if (tools.isInLandscapeMode()) {
			setContentView(R.layout.simple_event_view_landscape);
		} else {
			setContentView(R.layout.simple_event_view);
		}
		Intent intent = getIntent();
		this.isInCalendar = intent.getBooleanExtra("IS_IN_CALENDAR", false);
		TextView eventName = (TextView) findViewById(R.id.simple_event_view_eventname);
		TextView eventDate = (TextView) findViewById(R.id.simple_event_view_eventdate);
		TextView eventDescription = (TextView) findViewById(R.id.simple_event_view_eventdescription);
		ImageView eventImage = (ImageView) findViewById(R.id.simple_event_view_eventimage);
		eventName.setText(intent.getStringExtra("EVENT_NAME"));
		eventDate.setText(intent.getStringExtra("EVENT_DATE"));
		eventDescription.setText(intent.getStringExtra("EVENT_DESCRIPTION"));
		String eventPicPath = intent.getStringExtra("PIC_RELATIVE_PATH");
		if (eventPicPath != null) {
			if (Tools.getInstance(this).getAndStoreEventPicture("http://www.akk.org/schlonze/bilder/", eventPicPath.substring(7), getApplicationContext())) {
				eventImage.setClickable(false);
				File imgFile = new  File(eventPicPath);
			    if(imgFile.exists())
			    {
			        eventImage.setImageURI(Uri.fromFile(imgFile));

			    }
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		//To speed up the app, the activity is not restarted on screen rotation
		if (Tools.getInstance(this).isInLandscapeMode()) {
			setContentView(R.layout.simple_event_view_landscape);
		} else {
			setContentView(R.layout.simple_event_view);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!this.isInCalendar) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.event_view_menu, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_item_addtocalendar:
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
