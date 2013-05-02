package com.tugraz.parkreminder;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

public class ParkingReminder extends Application {

	private static final String TAG = "ParkingReminder";

	private static final String PREFS_NAME = "AlarmFile";
	private static final long DISTANCE_TIME = 500;
	private static final float PROXIMITY_DISTANCE = 2;

	private AlarmManager alarmManager;
	private LocationManager locationManager;

	private Criteria criteria;

	private Calendar parkingEnd;
	private Calendar alarmTime;
	private Boolean alarmActive;
	private Boolean notificationFired;
	private Location carLocation;
	private Integer distance;

	public Criteria getCriteria() {

		return criteria;
	}

	public Calendar getParkingEnd() {
		return parkingEnd;
	}

	public void setParkingEnd(Calendar parkingEnd) {
		this.parkingEnd = parkingEnd;
		writeSharedPrefs();
	}

	public Calendar getAlarmTime() {
		return alarmTime;
	}

	public void setAlarmTime(Calendar alarmTime) {
		this.alarmTime = alarmTime;
		writeSharedPrefs();
	}

	public Boolean isAlarmActive() {
		return alarmActive;
	}

	public void setNotificationFired(Boolean fired){
		notificationFired = fired;
		writeSharedPrefs();
	}
	
	public void activateAlarm() {
		this.alarmActive = true;
		distance = 0;
		setNotificationFired(false);
		writeSharedPrefs();
		updateDistance();
		updateAlarms();
	}

	public void deactivateAlarm() {
		this.alarmActive = false;
		writeSharedPrefs();
		updateAlarms();
	}
	
	public Boolean isTimeToGo(){
		if(alarmTime.getTimeInMillis() - System.currentTimeMillis() <= 0){
			return true;
		}
		return false;
	}

	public Location getCarLocation() {
		return carLocation;
	}

	public void setCarLocation(Location carLocation) {
		this.carLocation = carLocation;
		writeSharedPrefs();
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
		writeSharedPrefs();
		updateDistance();
	}
	
	public long getSecondsToParkingEnd() {
		long timeToAlarm = (parkingEnd.getTimeInMillis() - System.currentTimeMillis()) / 1000;
		
		if (timeToAlarm > 0){
			return timeToAlarm;
		}
		else{
			return 0;
		}
	}

	public long getSecondsToAlarm() {
		long timeToAlarm = (alarmTime.getTimeInMillis() - System.currentTimeMillis()) / 1000;
		
		if (timeToAlarm > 0){
			return timeToAlarm;
		}
		else{
			return 0;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();

		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		parkingEnd = Calendar.getInstance();
		alarmTime = Calendar.getInstance();
		distance = 0;
		carLocation = new Location("car");
		carLocation.setAltitude(0);
		carLocation.setLongitude(0);
		
		notificationFired = false;

		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
		criteria.setAltitudeRequired(true);
		criteria.setBearingRequired(true);
		criteria.setSpeedRequired(true);
		criteria.setCostAllowed(true);

		readSharedPrefs();

	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	public void writeSharedPrefs() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		editor.putBoolean("active", alarmActive);

		if (alarmActive) {
			editor.putBoolean("notificationFired", notificationFired);
			editor.putInt("distance", distance);
			editor.putLong("parkingEnd", parkingEnd.getTimeInMillis());
			editor.putLong("alarmTime", alarmTime.getTimeInMillis());
			editor.putFloat("latitude", (float) carLocation.getLatitude());
			editor.putFloat("longitude", (float) carLocation.getLongitude());
		}

		editor.commit();
	}

	public void readSharedPrefs() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		alarmActive = settings.getBoolean("active", false);

		if (alarmActive) {
			notificationFired = settings.getBoolean("notificationFired", notificationFired);
			distance = settings.getInt("distance", 0);
			parkingEnd.setTimeInMillis(settings.getLong("parkingEnd", 0));
			alarmTime.setTimeInMillis(settings.getLong("alarmTime", 0));
			carLocation.setLatitude(settings.getFloat("latitude", 0));
			carLocation.setLongitude(settings.getFloat("longitude", 0));
		}
	}

	public void updateDistance() {

		Location newLocation = locationManager
				.getLastKnownLocation(locationManager.getBestProvider(criteria,
						true));

		Integer newDistance = (int) carLocation.distanceTo(newLocation);
		
		Integer oldZone = (int) (distance / PROXIMITY_DISTANCE);
		Integer newZone = (int) (newDistance / PROXIMITY_DISTANCE);
		Log.d(TAG, "new distance: " + newDistance + " (" + newZone + ") " + distance + " (" + oldZone + ")");
		distance = newDistance;
		if (!oldZone.equals(newZone)) {
			Log.d(TAG, "new zone");
			updateAlarms();
		}
	}

	public void updateAlarms() {

		long expiration = parkingEnd.getTimeInMillis()
				- System.currentTimeMillis();
		
		
		Integer zone = (int) (distance / PROXIMITY_DISTANCE);

		float proximityIn = zone * PROXIMITY_DISTANCE;
		float proximityOut = (zone + 1) * PROXIMITY_DISTANCE;

		Context context = getApplicationContext();
		Intent alarm_intent = new Intent(context, AlarmReceiver.class);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				alarm_intent, PendingIntent.FLAG_UPDATE_CURRENT);

		alarmManager.cancel(pendingIntent);

		if (alarmActive && !notificationFired) {
			
			long alarm = parkingEnd.getTimeInMillis() - (distance * DISTANCE_TIME);

			long timeToAlarm = alarm - System.currentTimeMillis();

			if (timeToAlarm < 0) {
				alarm = System.currentTimeMillis() + 1000;
			}

			Calendar newAlarmTime = Calendar.getInstance();
			newAlarmTime.setTimeInMillis(alarm);
			setAlarmTime(newAlarmTime);

			alarmManager.set(AlarmManager.RTC_WAKEUP,
					alarmTime.getTimeInMillis(), pendingIntent);
			
			Log.d(TAG, "notification alarm set");

		}

		Intent location_intent = new Intent(context, LocationReceiver.class);

		pendingIntent = PendingIntent.getBroadcast(context, 0, location_intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		locationManager.removeProximityAlert(pendingIntent);

		if (alarmActive) {
			if (!distance.equals(0)) {
				locationManager.addProximityAlert(carLocation.getLatitude(),
						carLocation.getLongitude(), proximityIn, expiration,
						pendingIntent);
				Log.d(TAG, "proximity alarm set! exp:" + expiration + " ,prox: " + proximityIn);

			}
			locationManager.addProximityAlert(carLocation.getLatitude(),
					carLocation.getLongitude(), proximityOut, expiration,
					pendingIntent);
			Log.d(TAG, "proximity alarm set! exp:" + expiration + " ,prox: " + proximityOut);

		}
	}
	
	public Location getLastLocation(){
		return locationManager
				.getLastKnownLocation(locationManager.getBestProvider(criteria,
						true));
	}

	public void setAlarm(int hour, int minute) {

		setCarLocation(getLastLocation());

		Log.d(TAG, "carLocation: " + getCarLocation().toString());
		
		parkingEnd.setTimeInMillis(System.currentTimeMillis());
		
		if (parkingEnd.get(Calendar.HOUR_OF_DAY) > hour){
			Toast.makeText(this, "Alarm muss in der Zukunft liegen!", Toast.LENGTH_SHORT).show();
			return;
		}else if (parkingEnd.get(Calendar.HOUR_OF_DAY) == hour){
			if (parkingEnd.get(Calendar.MINUTE) >= minute){
				Toast.makeText(this, "Alarm muss in der Zukunft liegen!", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		
		parkingEnd.set(Calendar.HOUR_OF_DAY, hour);
		parkingEnd.set(Calendar.MINUTE, minute);
		parkingEnd.set(Calendar.SECOND, 0);	
		
		activateAlarm();
			
	}
}