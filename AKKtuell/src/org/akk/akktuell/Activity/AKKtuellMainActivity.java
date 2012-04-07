package org.akk.akktuell.Activity;

import java.util.GregorianCalendar;

import org.akk.akktuell.R;
import org.akk.akktuell.Model.AkkEvent;
import org.akk.akktuell.Model.InfoManager;

import android.app.Activity;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class AKKtuellMainActivity extends Activity {
	
	private InfoManager infoManager;
	private ListView elementListView;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        infoManager = new InfoManager(getApplicationContext());
        
        setContentView(R.layout.main);
        elementListView = (ListView) findViewById(R.id.main_element_listview);
        //elementListView.addHeaderView(view, null, false);
        elementListView.setOnItemClickListener(new OnItemClickListener() {  
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view,
        			int position, long id) {
        			AkkEvent clickedEvent = (AkkEvent) AKKtuellMainActivity.this.elementListView.getAdapter().getItem(position);
        			Toast.makeText(getApplicationContext(),
        				clickedEvent.getEventDescription(), Toast.LENGTH_LONG).show();
        		}
        });
        displayData();
        
    }
    
    private void displayData() {
    	if (!infoManager.readyToDisplayData()) {
    		//wait for data update
    	} 
    	AkkEventAdapter adapter = new AkkEventAdapter(getApplicationContext(), infoManager.getEvents());
    	elementListView.setAdapter(adapter);
    	
    }
    
    
}