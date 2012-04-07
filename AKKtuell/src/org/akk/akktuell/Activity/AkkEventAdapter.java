package org.akk.akktuell.Activity;

import org.akk.akktuell.R;
import org.akk.akktuell.Model.AkkEvent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AkkEventAdapter extends ArrayAdapter<AkkEvent> {
	private final Context context;
	private final AkkEvent[] events;

	public AkkEventAdapter(Context context, AkkEvent[] events) {
		super(context, R.layout.main_activity_list_item, events);
		this.context = context;
		this.events = events;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View listItemView = inflater.inflate(R.layout.main_activity_list_item, parent, false);
		TextView eventNameView = (TextView) listItemView.findViewById(R.id.listitem_eventname);
		TextView eventDateView = (TextView) listItemView.findViewById(R.id.listitem_eventdate);
		
		eventNameView.setText(events[position].getEventName());
		eventDateView.setText("testDate");
		return listItemView;
	}
}
