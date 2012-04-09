package org.akk.akktuell.Activity;

import org.akk.akktuell.R;
import org.akk.akktuell.Model.AkkEvent;
import org.akk.akktuell.Model.InfoManager;

import android.app.Activity;
import android.content.Intent;
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
	private static int MAX_SIZE_OF_GESTURE=900;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        monthCounter = 0;
        gestureScanner = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) {
				if (velocityX < -MAX_SIZE_OF_GESTURE) {
					monthCounter++;
				} else if (velocityX > MAX_SIZE_OF_GESTURE){
					monthCounter--;
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
        			if (clickedEvent != null) {
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
    	if (!infoManager.readyToDisplayData()) {
    		//wait for data update
    	}
		View mainView = findViewById(R.id.main_activity_layout);
		TextView listHeaderMonthName = (TextView) mainView.findViewById(R.id.main_activity_list_header);
		listHeaderMonthName.setText("TestHeader" + monthCounter);
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
    
}