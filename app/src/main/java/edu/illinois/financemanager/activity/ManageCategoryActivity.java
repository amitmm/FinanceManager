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
import edu.illinois.financemanager.repo.CategoryRepo;

/**
 * Activity for managing category
 */
public class ManageCategoryActivity extends ActionBarActivity {

    private long categoryID;
    private EditText newName;
    private Category category;
    private CategoryRepo repo = new CategoryRepo(ManageCategoryActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);

        Button updateButton, deleteButton, cancelButton;
        newName = (EditText) findViewById(R.id.new_category_name);
        updateButton = (Button) findViewById(R.id.update_category_button);
        deleteButton = (Button) findViewById(R.id.delete_category_button);
        cancelButton = (Button) findViewById(R.id.cancel_category_button);

        categoryID = 0;
        Intent intent = getIntent();
        categoryID = intent.getLongExtra("category_ID", 0);
        category = repo.getCategoryByID(categoryID);

        newName.setText(category.name);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (category.name.equals("No Category")) {
                    Toast.makeText(ManageCategoryActivity.this, "Cannot Modify the Default Category", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
                    startActivity(intent);
                } else {
                    if (newName.getText().toString().equals("")) {
                        Toast.makeText(ManageCategoryActivity.this, "Please Input Name", Toast.LENGTH_SHORT).show();
                    } else {
                        category.name = newName.getText().toString();
                        repo.update(category);

                        Toast.makeText(ManageCategoryActivity.this, "Category Name Updated", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (category.name.equals("No Category")) {
                    Toast.makeText(ManageCategoryActivity.this, "Cannot Delete the Default Category", Toast.LENGTH_SHORT).show();
                } else {
                    repo.delete(categoryID);
                    Toast.makeText(ManageCategoryActivity.this, "Category Deleted", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
                startActivity(intent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_category, menu);
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
