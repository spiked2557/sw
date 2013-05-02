package com.tugraz.parkreminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, MainActivity.class);

		PendingIntent pIntent = PendingIntent.getActivity(context, 0, i, 0);

		ParkingReminder app = (ParkingReminder) context.getApplicationContext();
		int seconds = (int) (app.getSecondsToParkingEnd() % 60);
		int minutes = (int) (app.getSecondsToParkingEnd() / 60);

		Notification noti = new Notification.Builder(context)
				.setContentTitle("Ihre Parkzeit läuft demnächst aus!")
				.setContentText(
						"Parkende in: " + minutes + "min " + seconds + "s")
				.setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent)
				.build();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		noti.flags |= Notification.FLAG_AUTO_CANCEL;
		noti.defaults = Notification.DEFAULT_ALL;
		notificationManager.notify(0, noti);
		
		app.setNotificationFired(true);

	}
}