package edu.illinois.financemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import edu.illinois.financemanager.R;
import edu.illinois.financemanager.object.Reminder;
import edu.illinois.financemanager.object.User;
import edu.illinois.financemanager.repo.ReminderRepo;
import edu.illinois.financemanager.repo.UserRepo;

/**
 * Activity for viewing reminders
 */
public class ReminderActivity extends MenuActivity {

    protected ListView mDrawerList;
    protected DrawerLayout mDrawerLayout;
    protected String mActivityTitle;

    protected ListView mListViewReminder;
    protected SimpleAdapter mAdapterReminder;
    protected ArrayList<Long> reminderIDList = new ArrayList<>();
    protected ArrayList<HashMap<String, String>> reminderListItem = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_reminder);
        mActivityTitle = getTitle().toString();
        mDrawerList = (ListView) findViewById(R.id.navList_reminder);
        createDrawer(ReminderActivity.this, mDrawerLayout, mActivityTitle, mDrawerList);

        mListViewReminder = (ListView) findViewById(R.id.reminder_list_view);

        mListViewReminder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ManageReminderActivity.class);
                intent.putExtra("reminder_ID", reminderIDList.get(position));
                startActivity(intent);
            }
        });

        ReminderRepo rRepo = new ReminderRepo(ReminderActivity.this);
        UserRepo uRepo = new UserRepo(ReminderActivity.this);
        User user = uRepo.getUser();

        if (user != null) {
            ArrayList<Reminder> reminderList = rRepo.getReminderList(user.id);

            if (reminderList.size() != 0) {
                for (int i = 0; i < reminderList.size(); i++) {
                    Reminder reminder = reminderList.get(i);
                    reminderIDList.add(reminder.id);
                    HashMap<String, String> item = new HashMap<>();
                    item.put("line1", reminder.message);
                    item.put("line2", String.format("%.02f", reminder.amount));
                    reminderListItem.add(item);
                }
                mAdapterReminder = new SimpleAdapter(ReminderActivity.this, reminderListItem,
                        android.R.layout.simple_list_item_2, new String[]{"line1", "line2"},
                        new int[]{android.R.id.text1, android.R.id.text2});
                mListViewReminder.setAdapter(mAdapterReminder);
            } else {
                Toast.makeText(ReminderActivity.this, "There are no reminders", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void handleAddReminderButton(View v) {
        Intent intent = new Intent(getApplicationContext(), AddReminderActivity.class);
        startActivity(intent);
    }
}
