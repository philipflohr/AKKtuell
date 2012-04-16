package org.akk.akktuell.Activity;

import java.text.DateFormatSymbols;
import java.util.GregorianCalendar;

import org.akk.akktuell.R;
import org.akk.akktuell.Model.AkkEvent;
import org.akk.akktuell.Model.AkkEvent.AkkEventType;
import org.akk.akktuell.Model.InfoManager;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class AKKtuellMainActivity extends Activity  {
	
	private InfoManager infoManager;
	private ListView elementListView;
	private GestureDetector gestureScanner;
	private int monthCounter;
	private static int MIN_SIZE_OF_GESTURE=500;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        monthCounter = 0;
        gestureScanner = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) {
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
				}
				
				AKKtuellMainActivity.this.displayData();
				return false;
			}
		});
        infoManager = new InfoManager(getApplicationContext());
        
        setContentView(R.layout.main);
        elementListView = (ListView) findViewById(R.id.main_element_listview);
        //elementListView.addHeaderView(view, null, false);
        elementListView.setOnItemClickListener(new OnItemClickListener() {  
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view,
        			int position, long id) {
        			AkkEvent clickedEvent = (AkkEvent) AKKtuellMainActivity.this.elementListView.getAdapter().getItem(position);
        			if (clickedEvent != null && !(clickedEvent.getEventType() == AkkEventType.Veranstaltungshinweis)) {
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
    
    private void displayData() {
    	while (!infoManager.readyToDisplayData()) {
    		//wait for data update
    	}
		View mainView = findViewById(R.id.main_activity_layout);
		TextView listHeaderMonthName = (TextView) mainView.findViewById(R.id.main_activity_list_header);
		listHeaderMonthName.setText(new DateFormatSymbols().getMonths()[new GregorianCalendar().get(GregorianCalendar.MONTH) + monthCounter - 1]);
		AkkEventAdapter adapter = new AkkEventAdapter(getApplicationContext(), infoManager.getEvents(), infoManager);
    	elementListView.setAdapter(adapter);    	
    } 
    
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
	    if (gestureScanner != null) {
	        if (gestureScanner.onTouchEvent(ev))
	            return true;
	    }
	    return super.dispatchTouchEvent(ev);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//To speed up the app, the activity is not restarted on screen rotation
		setContentView(R.layout.main);
		displayData();
	}
    
}