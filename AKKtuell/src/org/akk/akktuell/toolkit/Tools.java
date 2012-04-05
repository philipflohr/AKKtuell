package org.akk.akktuell.toolkit;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.res.Configuration;
import android.view.Display;

public class Tools {

	private Activity activity = null;
	private static Tools singleton = null;

	public static Tools getInstance(Activity activity) {
		if (singleton == null) {
			singleton = new Tools(activity);
		}

		return singleton;
	}

	private Tools(Activity activity) {
		this.activity = activity;
	}

	public boolean isTablet() {
		if (android.os.Build.VERSION.SDK_INT >= 11) { // honeycomb
			// test screen size, use reflection because isLayoutSizeAtLeast is
			// only available since 11
			Configuration con = activity.getResources().getConfiguration();
			try {
				Method mIsLayoutSizeAtLeast = con.getClass().getMethod(
						"isLayoutSizeAtLeast", int.class);
				boolean r = (Boolean) mIsLayoutSizeAtLeast.invoke(con,
						0x00000004); // Configuration.SCREENLAYOUT_SIZE_XLARGE
				return r;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	public boolean isInLandscapeMode() {
		Display display = activity.getWindowManager().getDefaultDisplay();
    	return display.getWidth() > display.getHeight() ? true : false;
	}
}
