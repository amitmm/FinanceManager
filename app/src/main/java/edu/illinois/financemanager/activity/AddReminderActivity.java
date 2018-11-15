package edu.illinois.financemanager.activity;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

import edu.illinois.financemanager.R;
import edu.illinois.financemanager.fragment.DatePickerDialogFragment;
import edu.illinois.financemanager.fragment.TimePickerDialogFragment;
import edu.illinois.financemanager.object.NotificationReceiver;
import edu.illinois.financemanager.object.Reminder;
import edu.illinois.financemanager.object.User;
import edu.illinois.financemanager.repo.ReminderRepo;
import edu.illinois.financemanager.repo.UserRepo;

/**
 * Activity for adding reminders
 */
public class AddReminderActivity extends ActionBarActivity {

    private Button addButton;
    private EditText message, amount, date, time;
    private Spinner dropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        message = (EditText) findViewById(R.id.reminder_message);
        amount = (EditText) findViewById(R.id.reminder_amount);
        date = (EditText) findViewById(R.id.reminder_date);
        time = (EditText) findViewById(R.id.reminder_time);

        dropdown = (Spinner) findViewById(R.id.reminder_repeat);
        String[] items = new String[]{"Never", "Every Sunday", "Every Monday", "Every Tuesday", "Every Wednesday", "Every Thursday", "Every Friday", "Every Saturday"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        dropdown.setAdapter(adapter);

        addButton = (Button) findViewById(R.id.add_reminder_button);

        date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickerFragment(v);
                    addButton.requestFocus();
                }
            }
        });

        time.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showTimePickerFragment(v);
                    addButton.requestFocus();
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReminder();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancel_reminder_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReminderActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * helper function for adding reminders
     */
    private void addReminder() {
        ADD_REMINDER:
        {
            if (message.getText().toString().equals("") || amount.getText().toString().equals("") || date.getText().toString().equals("") || time.getText().toString().equals("")) {
                Toast.makeText(AddReminderActivity.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
            } else {
                UserRepo uRepo = new UserRepo(AddReminderActivity.this);
                User user = uRepo.getUser();

                ReminderRepo rRepo = new ReminderRepo(AddReminderActivity.this);
                Reminder reminder = new Reminder();

                reminder.message = message.getText().toString();

                reminder.amount = Double.parseDouble(amount.getText().toString());

                reminder.startDate = date.getText().toString();
                int pickerYear, pickerMonth, pickerDay, pickerHourOfDay, pickerMinute;
                pickerYear = Integer.parseInt(reminder.startDate.substring(0, 4));
                pickerMonth = Integer.parseInt(reminder.startDate.substring(5, 7)) - 1;
                pickerDay = Integer.parseInt(reminder.startDate.substring(8, 10));

                reminder.startTime = time.getText().toString();
                if (reminder.startTime.substring(6, 8).equals("PM")) {
                    pickerHourOfDay = Integer.parseInt(reminder.startTime.substring(0, 2)) + 12;
                } else {
                    pickerHourOfDay = Integer.parseInt(reminder.startTime.substring(0, 2));
                    if (pickerHourOfDay == 12)
                        pickerHourOfDay = 0;
                }
                pickerMinute = Integer.parseInt(reminder.startTime.substring(3, 5));

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, pickerYear);
                cal.set(Calendar.MONTH, pickerMonth);
                cal.set(Calendar.DATE, pickerDay);
                cal.set(Calendar.HOUR_OF_DAY, pickerHourOfDay);
                cal.set(Calendar.MINUTE, pickerMinute);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                Calendar today = Calendar.getInstance();
                if (today.compareTo(cal) > 0) {
                    Toast.makeText(AddReminderActivity.this, "Start Date Already Passed", Toast.LENGTH_SHORT).show();
                    break ADD_REMINDER;
                }

                reminder.repeatID = dropdown.getSelectedItemId();
                reminder.userID = user.id;

                long reminderID = rRepo.insert(reminder);
                Toast.makeText(AddReminderActivity.this, "New Reminder Added", Toast.LENGTH_SHORT).show();

                Intent alarmIntent = new Intent(getApplicationContext(), NotificationReceiver.class);
                alarmIntent.putExtra("reminder_ID", reminderID);
                Log.d("Notification", String.valueOf(reminderID));

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) reminderID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

                Intent intent = new Intent(getApplicationContext(), ReminderActivity.class);
                startActivity(intent);
            }
        }
    }

    // handle date picker fragment
    public void showDatePickerFragment(View v) {
        DialogFragment newFragment = new DatePickerDialogFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    // handle time picker fragment
    public void showTimePickerFragment(View v) {
        DialogFragment newFragment = new TimePickerDialogFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
