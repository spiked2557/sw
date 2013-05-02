package com.tugraz.parkreminder;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

public class LocationReceiver extends BroadcastReceiver {

	private static final String TAG = "ParkingReminder";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "location proximity change");

		Intent i = new Intent(context, MainActivity.class);

		PendingIntent pIntent = PendingIntent.getActivity(context, 0, i, 0);

		Boolean entering = intent.getBooleanExtra(
				LocationManager.KEY_PROXIMITY_ENTERING, false);

		ParkingReminder app = (ParkingReminder) context.getApplicationContext();

		app.updateDistance();

	}
}