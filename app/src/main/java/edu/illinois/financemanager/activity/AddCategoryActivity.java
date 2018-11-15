package edu.illinois.financemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.illinois.financemanager.R;
import edu.illinois.financemanager.object.Category;
import edu.illinois.financemanager.object.User;
import edu.illinois.financemanager.repo.CategoryRepo;
import edu.illinois.financemanager.repo.UserRepo;

/**
 * Activity to add new categories
 */
public class AddCategoryActivity extends ActionBarActivity {

    private EditText newName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        Button saveButton, cancelButton;
        saveButton = (Button) findViewById(R.id.save_category_button);
        cancelButton = (Button) findViewById(R.id.cancel_category_button);
        newName = (EditText) findViewById(R.id.new_category_name);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * helper function for adding new categories
     */
    private void addCategory() {
        if (newName.getText().toString().equals("")) {
            Toast.makeText(AddCategoryActivity.this, "Please Input Name", Toast.LENGTH_SHORT).show();
        } else {
            UserRepo uRepo = new UserRepo(AddCategoryActivity.this);
            User user = uRepo.getUser();

            CategoryRepo cRepo = new CategoryRepo(AddCategoryActivity.this);
            Category category = new Category();
            category.name = newName.getText().toString();
            category.userID = user.id;

            cRepo.insert(category);
            Toast.makeText(AddCategoryActivity.this, "New Category Added", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_category, menu);
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
