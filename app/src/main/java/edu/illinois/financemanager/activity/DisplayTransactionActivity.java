package edu.illinois.financemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import edu.illinois.financemanager.R;
import edu.illinois.financemanager.object.Transaction;

/**
 * Activity to display list of transaction results in a table format
 */
public class DisplayTransactionActivity extends ActionBarActivity {

    protected Button mCancelButton;
    protected ListView mListViewTransaction;
    protected SimpleAdapter mAdapterTransaction;
    protected ArrayList<HashMap<String, String>> transactionListItem = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_transaction);

        mCancelButton = (Button) findViewById(R.id.cancel_button);
        mListViewTransaction = (ListView) findViewById(R.id.transaction_list_view);

        Toast.makeText(DisplayTransactionActivity.this, SearchTransactionActivity.transactionList.get(0).message, Toast.LENGTH_SHORT).show();

        mListViewTransaction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Transaction transaction = SearchTransactionActivity.transactionList.get(position);
                Intent intent = new Intent(getApplicationContext(), DisplayPictureActivity.class);
                intent.putExtra("Type", transaction.type);
                intent.putExtra("Category", transaction.category);
                intent.putExtra("Amount", transaction.amount);
                intent.putExtra("Info", transaction.message);
                intent.putExtra("Date", transaction.date.toString());
                intent.putExtra("Picture", transaction.pic);
                startActivity(intent);
            }
        });

        for (int i = 0; i < SearchTransactionActivity.transactionList.size(); i++) {
            Transaction transaction = SearchTransactionActivity.transactionList.get(i);
            HashMap<String, String> item = new HashMap<>();
            item.put("line1", transaction.message);
            item.put("line2", String.format("%.02f", transaction.amount));
            transactionListItem.add(item);
        }
        mAdapterTransaction = new SimpleAdapter(DisplayTransactionActivity.this, transactionListItem,
                android.R.layout.simple_list_item_2, new String[]{"line1", "line2"},
                new int[]{android.R.id.text1, android.R.id.text2});
        mListViewTransaction.setAdapter(mAdapterTransaction);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_transaction, menu);
        return true;
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
