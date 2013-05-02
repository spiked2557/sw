package com.tugraz.parkreminder;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity implements LocationListener, OnTimeSetListener {

	private static final long LOCATION_UPDATE_INTERVAL = 5;
	private static final float LOCATION_UPDATE_DISTANCE = 5;

	private static final String TAG = "ParkingReminder";

	private ParkingReminder app;

	private LocationManager locationManager;

	private GoogleMap map;

	private MapFragment mapFragment;
	private UiSettings mapSettings;
	private Marker carMarker;

	private LinearLayout countdownLayout;

	private Button addAlarmButton;
	private TextView parkingEndTextView;
	private TextView distanceTextView;
	private TextView countdownTextView;
	private ImageView timeToGoImage;

	private CountDownTimer countdownTimer;

	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

	private DecimalFormat twoNumbersFormat = new DecimalFormat("00");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		countdownLayout = (LinearLayout) this.findViewById(R.id.countdownLayout);
		addAlarmButton = (Button) this.findViewById(R.id.addAlarmButton);
		parkingEndTextView = (TextView) this.findViewById(R.id.parkingEndTextView);
		distanceTextView = (TextView) this.findViewById(R.id.distanceTextView);
		countdownTextView = (TextView) this.findViewById(R.id.countdownTextView);

		timeToGoImage = (ImageView) this.findViewById(R.id.imageViewToCar);

		app = (ParkingReminder) getApplication();

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		setUpMap();
		setUpLocationUpdates();
		activateCountdownTimer();
		update();
		zoomToLocation();

	}

	@Override
	protected void onResume() {
		super.onResume();
		activateCountdownTimer();
		setUpLocationUpdates();
		update();
		zoomToLocation();
	}

	@Override
	protected void onStart() {
		super.onStart();
		activateCountdownTimer();
		setUpLocationUpdates();
		update();
		zoomToLocation();
	}

	@Override
	protected void onPause() {
		super.onPause();
		deactivateCountdownTimer();
		cancelLocationUpdates();
	}

	@Override
	protected void onStop() {
		super.onStop();
		deactivateCountdownTimer();
		cancelLocationUpdates();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_cancel:
			cancelAlarm();
			break;
		case R.id.action_settings:
			Toast.makeText(this, "Menu item 2 selected", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}

		return true;
	}

	@Override
	public void onLocationChanged(Location location) {

		Log.d(TAG, "location update in main activity");
		if (app.isAlarmActive()) {
			app.updateDistance();
		}

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public void setUpLocationUpdates() {
		Log.d(TAG, "set location updates");
		cancelLocationUpdates();
		locationManager.requestLocationUpdates(locationManager.getBestProvider(app.getCriteria(), true), LOCATION_UPDATE_INTERVAL, LOCATION_UPDATE_DISTANCE, this);

	}

	public void cancelLocationUpdates() {
		Log.d(TAG, "cancel location updates");
		locationManager.removeUpdates(this);
	}

	private void update() {
		if (app.isAlarmActive()) {

			if (carMarker == null) {
				Log.d(TAG, "carMarker == null");
				carMarker = map.addMarker(new MarkerOptions().position(new LatLng(app.getCarLocation().getLatitude(), app.getCarLocation().getLongitude())).title("Dein Auto"));
			}

			countdownLayout.setVisibility(View.VISIBLE);
			addAlarmButton.setVisibility(View.GONE);
			parkingEndTextView.setText(timeFormat.format(app.getParkingEnd().getTime()));
			distanceTextView.setText(app.getDistance() + "m");

			if (app.isTimeToGo()) {
				countdownTextView.setText(twoNumbersFormat.format(app.getSecondsToParkingEnd() / 60) + ":" + twoNumbersFormat.format(app.getSecondsToParkingEnd() % 60));

				timeToGoImage.setVisibility(View.VISIBLE);
			} else {
				countdownTextView.setText(twoNumbersFormat.format(app.getSecondsToAlarm() / 60) + ":" + twoNumbersFormat.format(app.getSecondsToAlarm() % 60));

				timeToGoImage.setVisibility(View.GONE);
			}
		} else {
			if (carMarker != null) {
				carMarker.remove();
				carMarker = null;
			}
			countdownLayout.setVisibility(View.GONE);
			addAlarmButton.setVisibility(View.VISIBLE);
		}
		this.invalidateOptionsMenu();
	}

	private void zoomToLocation() {
		if (app.getLastLocation() != null) {
			LatLng zoomTo = new LatLng(app.getLastLocation().getLatitude(), app.getLastLocation().getLongitude());
			map.moveCamera(CameraUpdateFactory.newLatLng(zoomTo));
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomTo, 15));
		}

	}

	private void setUpMap() {
		mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

		map = mapFragment.getMap();
		mapSettings = map.getUiSettings();
		mapSettings.setMyLocationButtonEnabled(true);
		mapSettings.setZoomControlsEnabled(false);
		map.setMyLocationEnabled(true);
		map.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng arg0) {

				/*
				 * Context context = getApplicationContext(); Intent intent = new Intent(context, CountdownActivity.class); intent.putExtra("location", carLocation); intent.putExtra("hour",
				 * alarmTimePicker.getCurrentHour()); intent.putExtra("minute", alarmTimePicker.getCurrentMinute());
				 * 
				 * startActivity(intent);
				 */

			}
		});

	}

	public void showTimePickerDialog(View v) {
		DialogFragment timePickerFragment = new TimePickerFragment();
		timePickerFragment.show(getFragmentManager(), "timePicker");

	}

	@Override
	public void onTimeSet(TimePicker view, int hour, int minute) {
		Log.d(TAG, "time set");
		setAlarm(hour, minute);

	}

	public void setAlarm(int hour, int minute) {
		Log.d(TAG, "time set");
		if (!app.isAlarmActive()) {

			app.setAlarm(hour, minute);
			Log.d(TAG, "alarm set");
			activateCountdownTimer();
			update();
		}

	}

	public void cancelAlarm() {

		app.deactivateAlarm();
		Log.d(TAG, "alarm unset");
		deactivateCountdownTimer();
		update();

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.action_cancel);
		if (app.isAlarmActive()) {
			item.setEnabled(true);
			item.setVisible(true);
		} else {
			item.setEnabled(false);
			item.setVisible(false);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	public void activateCountdownTimer() {
		if (app.isAlarmActive() && countdownTimer == null) {
			countdownTimer = new CountDownTimer(app.getParkingEnd().getTimeInMillis() - System.currentTimeMillis(), 1000) {

				@Override
				public void onTick(long millisUntilFinished) {
					update();
				}

				@Override
				public void onFinish() {
					update();
				}
			}.start();
		}
	}

	public void deactivateCountdownTimer() {
		if (countdownTimer != null) {
			countdownTimer.cancel();
			countdownTimer = null;
		}
	}
}
