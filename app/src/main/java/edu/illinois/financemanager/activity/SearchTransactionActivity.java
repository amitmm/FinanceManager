package edu.illinois.financemanager.activity;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

import edu.illinois.financemanager.R;
import edu.illinois.financemanager.fragment.CategoryDialogFragment;
import edu.illinois.financemanager.fragment.DatePickerDialogFragment;
import edu.illinois.financemanager.object.Transaction;
import edu.illinois.financemanager.object.User;
import edu.illinois.financemanager.repo.TransactionRepo;
import edu.illinois.financemanager.repo.UserRepo;

/**
 * Activity to search for a particular set of transactions
 */
public class SearchTransactionActivity extends MenuActivity implements CategoryDialogFragment.OnFragmentInteractionListener {

    public static ArrayList<Transaction> transactionList = new ArrayList<>();
    public static int dateField = 1;
    protected ListView mDrawerList;
    protected DrawerLayout mDrawerLayout;
    protected String mActivityTitle;
    private Button searchButton;
    private EditText field1, field2;
    private RadioButton type;
    private RadioGroup type_group;

    /**
     * On create
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_transaction);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_search);
        mActivityTitle = getTitle().toString();
        mDrawerList = (ListView) findViewById(R.id.navList_search);
        createDrawer(SearchTransactionActivity.this, mDrawerLayout, mActivityTitle, mDrawerList);

        searchButton = (Button) findViewById(R.id.search_transaction_button);
        field1 = (EditText) findViewById(R.id.new_field1);
        field2 = (EditText) findViewById(R.id.new_field2);
        type_group = (RadioGroup) findViewById(R.id.radio_type);

        field2.setVisibility(View.GONE);

        field1.setHint("Keyword");

        type_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(field1.getWindowToken(), 0);
                field1.setInputType(InputType.TYPE_CLASS_TEXT);
                field2.setInputType(InputType.TYPE_CLASS_TEXT);
                switch (radioButton.getText().toString()) {
                    case "Keyword":
                        field1.setVisibility(View.VISIBLE);
                        field2.setVisibility(View.GONE);
                        field1.setText("");
                        field2.setText("");
                        field1.setHint("Keyword");
                        searchButton.requestFocus();

                        field1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                // do nothing
                            }
                        });

                        field2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                // do nothing
                            }
                        });
                        break;
                    case "Category":
                        field1.setVisibility(View.VISIBLE);
                        field2.setVisibility(View.GONE);
                        field1.setText("");
                        field2.setText("");
                        field1.setHint("Category");
                        searchButton.requestFocus();

                        field1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (hasFocus) {
                                    showCategoryDialog(v);
                                    searchButton.requestFocus();
                                }
                            }
                        });

                        field2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                // do nothing
                            }
                        });
                        break;
                    case "Amount":
                        field1.setVisibility(View.VISIBLE);
                        field2.setVisibility(View.VISIBLE);
                        field1.setText("");
                        field2.setText("");
                        field1.setHint("Min Amount");
                        field2.setHint("Max Amount");
                        field1.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        field2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        searchButton.requestFocus();

                        field1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                // do nothing
                            }
                        });

                        field2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                // do nothing
                            }
                        });
                        break;
                    case "Date":
                        field1.setVisibility(View.VISIBLE);
                        field2.setVisibility(View.VISIBLE);
                        field1.setText("");
                        field2.setText("");
                        field1.setHint("Start Date");
                        field2.setHint("End Date");
                        searchButton.requestFocus();

                        field1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (hasFocus) {
                                    dateField = 1;
                                    showDatePickerFragment(v);
                                    searchButton.requestFocus();
                                }
                            }
                        });

                        field2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (hasFocus) {
                                    dateField = 2;
                                    showDatePickerFragment(v);
                                    searchButton.requestFocus();
                                }
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {

            /**
             * On click, creates a new transaction and adds it to database
             */
            @Override
            public void onClick(View v) {
                TransactionRepo tRepo = new TransactionRepo(SearchTransactionActivity.this);
                UserRepo uRepo = new UserRepo(SearchTransactionActivity.this);
                User user = uRepo.getUser();

                String field1_text = field1.getText().toString();
                String field2_text = field2.getText().toString();

                int selectedId = type_group.getCheckedRadioButtonId();
                type = (RadioButton) findViewById(selectedId);

                if (type.getText().toString().equals("Keyword")) {
                    if (field1_text.equals("")) {
                        Toast.makeText(SearchTransactionActivity.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
                    } else {
                        transactionList = tRepo.getTransactionByMessage(field1_text.toLowerCase(), user.id);
                    }
                } else if (type.getText().toString().equals("Category")) {
                    if (field1_text.equals("")) {
                        Toast.makeText(SearchTransactionActivity.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
                    } else {
                        transactionList = tRepo.getTransactionByCategory(field1_text.toLowerCase(), user.id);
                    }
                } else if (type.getText().toString().equals("Amount")) {
                    if (field1_text.equals("") || field2_text.equals("")) {
                        Toast.makeText(SearchTransactionActivity.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
                    } else {
                        transactionList = tRepo.getTransactionByAmount(field1_text, field2_text, user.id);
                    }
                } else if (type.getText().toString().equals("Date")) {
                    if (field1_text.equals("") || field2_text.equals("")) {
                        Toast.makeText(SearchTransactionActivity.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
                    } else {
                        transactionList = tRepo.getTransactionByDate(field1_text, field2_text, user.id);
                    }
                }

                if (transactionList.size() == 0) {
                    Toast.makeText(SearchTransactionActivity.this, "No Results", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DisplayTransactionActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void showCategoryDialog(View v) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new CategoryDialogFragment();
        dialog.show(getFragmentManager(), "CategoryDialogFragment");
    }

    public void showDatePickerFragment(View v) {
        DialogFragment newFragment = new DatePickerDialogFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onCategoryClick(DialogFragment dialog, String category) {
        field1.setText(category);
    }
}
