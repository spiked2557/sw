package com.tugraz.parkreminder;

import java.util.Calendar;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CountdownActivity extends Activity {

	private GoogleMap map;
	private Location location;
	private Calendar parkingEnd;
	private Integer hour;
	private Integer minute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_countdown);
		setupActionBar();

		Bundle bundle = getIntent().getExtras();
		location = bundle.getParcelable("location");
		hour = bundle.getInt("hour");
		minute = bundle.getInt("minute");
		parkingEnd = Calendar.getInstance();
		parkingEnd.setTimeInMillis(System.currentTimeMillis());

		parkingEnd.set(Calendar.HOUR_OF_DAY, hour);
		parkingEnd.set(Calendar.MINUTE, minute);

		LatLng car = new LatLng(location.getLatitude(), location.getLongitude());

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		map.addMarker(new MarkerOptions().position(car).title("Auto"));

		map.moveCamera(CameraUpdateFactory.newLatLngZoom(car, 10));
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.countdown, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
