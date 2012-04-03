package org.akk.akktuell.Activity;

import org.akk.akktuell.R;
import org.akk.akktuell.Model.InfoManager;

import android.app.Activity;
import android.os.Bundle;

public class AKKtuellMainActivity extends Activity {
	
	private InfoManager infoManager;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        infoManager = new InfoManager();
        setContentView(R.layout.main);
    }
}