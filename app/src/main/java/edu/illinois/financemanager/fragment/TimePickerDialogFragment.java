package edu.illinois.financemanager.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

import edu.illinois.financemanager.R;


public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        if (hourOfDay > 12)
            ((EditText) getActivity().findViewById(R.id.reminder_time)).setText(String.format("%02d:%02d PM", hourOfDay - 12, minute));
        else if (hourOfDay == 0)
            ((EditText) getActivity().findViewById(R.id.reminder_time)).setText(String.format("12:%02d AM", minute));
        else
            ((EditText) getActivity().findViewById(R.id.reminder_time)).setText(String.format("%02d:%02d AM", hourOfDay, minute));
    }

}
