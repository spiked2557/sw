package com.tugraz.parkreminder;

import java.util.Calendar;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.text.format.DateFormat;

public class TimePickerFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		if (!(getActivity() instanceof OnTimeSetListener))
			throw new IllegalStateException(
					"Activity should implement OnTimeSetListener!");
		OnTimeSetListener timeSetListener = (OnTimeSetListener) getActivity();

		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		return new TimePickerDialog(getActivity(), timeSetListener, hour,
				minute, DateFormat.is24HourFormat(getActivity()));
	}

}