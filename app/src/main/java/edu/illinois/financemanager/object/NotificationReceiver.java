package edu.illinois.financemanager.object;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.Calendar;

import edu.illinois.financemanager.R;
import edu.illinois.financemanager.activity.MainActivity;
import edu.illinois.financemanager.repo.ReminderRepo;

public class NotificationReceiver extends BroadcastReceiver {

    final int MILLIS_A_DAY = 86400000;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        ReminderRepo rRepo = new ReminderRepo(context);
        long reminderID = intent.getLongExtra("reminder_ID", 0);
        Log.d("Notification", String.valueOf(reminderID));
        Reminder reminder = rRepo.getReminderByID(reminderID);
        if (reminder != null) {
            if (reminder.repeatID != 0) {
                Intent alarmIntent = new Intent(context, NotificationReceiver.class);
                alarmIntent.putExtra("reminder_ID", reminderID);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) reminderID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                Calendar cal = Calendar.getInstance();
                int days;
                if ((int) reminder.repeatID == cal.get(Calendar.DAY_OF_WEEK)) {
                    days = 7;
                } else {
                    days = (int) reminder.repeatID - cal.get(Calendar.DAY_OF_WEEK);
                    if (days < 1)
                        days += 7;
                }

                int hourOfDay, minute;
                if (reminder.startTime.substring(6, 8).equals("PM")) {
                    hourOfDay = Integer.parseInt(reminder.startTime.substring(0, 2)) + 12;
                } else {
                    hourOfDay = Integer.parseInt(reminder.startTime.substring(0, 2));
                    if (hourOfDay == 12)
                        hourOfDay = 0;
                }
                minute = Integer.parseInt(reminder.startTime.substring(3, 5));

                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Log.d("DAYS", String.valueOf(days));
                alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + (days * MILLIS_A_DAY), pendingIntent);
            }

            mBuilder.setSmallIcon(R.drawable.financemanager_logo);
            mBuilder.setContentTitle(reminder.message);
            mBuilder.setContentText(String.format("%.2f", reminder.amount));
            mBuilder.setAutoCancel(true);

            Intent resultIntent = new Intent(context, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify((int) reminderID, mBuilder.build());
        }
    }
}
