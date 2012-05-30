package org.akk.akktuell.Activity;

import java.text.DateFormatSymbols;
import java.util.GregorianCalendar;

import org.akk.akktuell.R;
import org.akk.akktuell.Model.AkkEvent;
import org.akk.akktuell.Model.InfoManager;
import org.akk.akktuell.database.DBException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The Class AKKtuellMainActivity. t creates the model and displays all available events.
 * 
 * @author Philip Flohr
 */
public class AKKtuellMainActivity extends Activity  {
	
	/** The info manager. */
	private InfoManager infoManager;
	
	/** The element list view. */
	private ListView elementListView;
	
	/** The gesture scanner. */
	private GestureDetector gestureScanner;
	
	/** The month counter. */
	private int monthCounter;
	
	/** The MI n_ siz e_ o f_ gesture. */
	private static int MIN_SIZE_OF_GESTURE=800;
	
	/** The view handler. */
	private Handler viewHandler;
	
	
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_for_data);
        monthCounter = 0;
        gestureScanner = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) {
				if (infoManager.readyToDisplayData()) {
					if (velocityX > MIN_SIZE_OF_GESTURE) {
						if (infoManager.setCurrentMonth(new GregorianCalendar().get(GregorianCalendar.MONTH) + monthCounter - 1)) {
							monthCounter--;
						} else {
							monthCounter = 11 - (new GregorianCalendar().get(GregorianCalendar.MONTH));
						}
					} else if (velocityX < -1*MIN_SIZE_OF_GESTURE){
						if (infoManager.setCurrentMonth(new GregorianCalendar().get(GregorianCalendar.MONTH) + monthCounter + 1)) {
							monthCounter++;
						} else {
							monthCounter = -(new GregorianCalendar().get(GregorianCalendar.MONTH));
						}
					} else {
						//this is not a guesture we want to interpret
						return false;
					}
					
					AKKtuellMainActivity.this.displayData();
					return true;
				} else {
					//there is no data...
					return false;
				}
			}
		});
        
        //Lets create a Handler for GUI events
        viewHandler = new Handler() {
    		@Override
    		public void handleMessage(Message msg) {
    			if (msg.what == 0) {
    				//There is some data to display
    				AKKtuellMainActivity.this.onDataAvailable();
    			} else if (msg.what == 1) {
    				//There is no data to display, but the download started
    				Toast toast = Toast.makeText(getApplicationContext(), "There is no local data but the download already started.\nPlease be patient", 1500);
	        		toast.show();
    			} else if (msg.what == 2) {
    				//There is no data to display and it's not possible to download something.
    				Toast toast = Toast.makeText(getApplicationContext(), "There is no local data and you are not connected to the internet.\nSo this app is currently useless", 1500);
	        		toast.show();
    			} else if (msg.what == 3) {
    				//There is no data to display and it's not possible to download something.
    				Toast toast = Toast.makeText(getApplicationContext(), "There is already an update in process", 1500);
	        		toast.show();
    			}
    		}
    	};
        try {
			infoManager = new InfoManager(getApplicationContext(), viewHandler);
		} catch (DBException e) {
			// No InfoManager - No Application. Thats it.
			e.printStackTrace();
			this.finish();
		}
    }
    
    /**
     * On data available.
     */
    public void onDataAvailable() {
    	setContentView(R.layout.main);
        elementListView = (ListView) findViewById(R.id.main_element_listview);
        elementListView.setOnItemClickListener(new OnItemClickListener() {  
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view,
        			int position, long id) {
        			AkkEvent clickedEvent = (AkkEvent) AKKtuellMainActivity.this.elementListView.getAdapter().getItem(position);
        			if (clickedEvent != null && clickedEvent.getEventDescription() != null) {
        				Intent intent = new Intent(AKKtuellMainActivity.this,AKKtuellEventView.class);
        				intent.putExtra("EVENT_NAME", clickedEvent.getEventName());
        				intent.putExtra("EVENT_DATE", "test");
        				intent.putExtra("EVENT_DESCRIPTION", clickedEvent.getEventDescription());
        				startActivity(intent);
        			}
        		}
        });
        displayData();
    }
    
    
    /**
     * Display data.
     * 
     * There is something(maybe new) to display! Do it!
     */
    private void displayData() {
		View mainView = findViewById(R.id.main_activity_layout);
		TextView listHeaderMonthName = (TextView) mainView.findViewById(R.id.main_activity_list_header);
		listHeaderMonthName.setText(new DateFormatSymbols().getMonths()[new GregorianCalendar().get(GregorianCalendar.MONTH) + monthCounter]);
		AkkEventAdapter adapter = new AkkEventAdapter(getApplicationContext(), infoManager.getEvents(), infoManager);
    	elementListView.setAdapter(adapter);    	
    } 
    
    
    //This is important for OnFiling function
	/* (non-Javadoc)
     * @see android.app.Activity#dispatchTouchEvent(android.view.MotionEvent)
     */
    @Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
	    if (gestureScanner != null) {
	        if (gestureScanner.onTouchEvent(ev))
	            return true;
	    }
	    return super.dispatchTouchEvent(ev);
	}
	
	//To speed up the app, the activity is not restarted on screen rotation
	/* (non-Javadoc)
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (infoManager.readyToDisplayData()) {
			setContentView(R.layout.main);
			displayData();
		}
	}
    
	//Close Database on App-close
	/* (non-Javadoc)
	 * @see android.app.Activity#finish()
	 */
	@Override
	public void finish() {
		infoManager.finish();
		super.finish();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_item_update:
	        	if (infoManager.isOnline()) {
	        		infoManager.updateEvents();
	        	} else {
	        		Toast toast = Toast.makeText(getApplicationContext(), "Update not possible -- You're not online", 500);
	        		toast.show();
	        	}
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	//end the application if back is pressed and no data is available
	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
	    if ((keyCode == KeyEvent.KEYCODE_BACK) && !infoManager.readyToDisplayData() && !infoManager.isOnline())
	    {
	        finish();
	    }
	    return super.onKeyDown(keyCode, event);
	}

}