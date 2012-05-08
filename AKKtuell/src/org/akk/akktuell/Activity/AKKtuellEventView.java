package org.akk.akktuell.Activity;

import org.akk.akktuell.R;
import org.akk.akktuell.toolkit.Tools;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class AKKtuellEventView extends Activity {

	private Tools tools;
	
	private boolean isInCalendar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.tools = Tools.getInstance(this);
		setupView();
	}

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
		// Uri eventPicUri = (Uri)
		// intent.getSerializableExtra("EVENT_PICTURE_URI");
		// eventImage.setImageURI(eventPicUri);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		//To speed up the app, the activity is not restarted on screen rotation
		if (Tools.getInstance(this).isInLandscapeMode()) {
			setContentView(R.layout.simple_event_view_landscape);
		} else {
			setContentView(R.layout.simple_event_view);
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!this.isInCalendar) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.event_view_menu, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}
	
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
