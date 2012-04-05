package org.akk.akktuell.Activity;

import org.akk.akktuell.R;
import org.akk.akktuell.Model.InfoManager;

import android.app.Activity;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class AKKtuellMainActivity extends Activity {
	
	private InfoManager infoManager;
	private ListView elementListView;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        infoManager = new InfoManager(getApplicationContext());

        elementListView = (ListView) findViewById(R.id.main_element_listview);        
        
        displayData();
        
        setContentView(R.layout.main);
        
        //set up eventListener
    }
    
    private void displayData() {
    	if (infoManager.readyToDisplayData()) {
    		Cursor cursor = infoManager.getData();
    		//TODO define fields from db to display
    		String[] elementIDs = new String[] {"New", "Date", "Title", "inCalendar"};
    	
    		//set View to use
    		int[] itemView = new int[] { R.id.list_image_new, R.id.listitem_eventdate, R.id.listitem_eventname, R.id.listitem_incalendar};
    	
    		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.main_activity_list_item, cursor, elementIDs, itemView);
    	
    		elementListView.setAdapter(adapter);
    	} else {
    		//wait for data update
    	}
    	
    }
    
    
}