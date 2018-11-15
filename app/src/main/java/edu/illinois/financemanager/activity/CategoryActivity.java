package edu.illinois.financemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import edu.illinois.financemanager.R;
import edu.illinois.financemanager.object.Category;
import edu.illinois.financemanager.object.User;
import edu.illinois.financemanager.repo.CategoryRepo;
import edu.illinois.financemanager.repo.UserRepo;

/**
 * Activity to view categories
 */
public class CategoryActivity extends MenuActivity {

    protected ListView mDrawerList;
    protected DrawerLayout mDrawerLayout;
    protected String mActivityTitle;

    protected ListView mListViewCategory;
    protected ArrayAdapter<String> mAdapterCategory;
    protected ArrayList<Long> categoryIDList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_category);
        mActivityTitle = getTitle().toString();
        mDrawerList = (ListView) findViewById(R.id.navList_category);
        createDrawer(CategoryActivity.this, mDrawerLayout, mActivityTitle, mDrawerList);

        mAdapterCategory = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        mListViewCategory = (ListView) findViewById(R.id.category_list_view);
        mListViewCategory.setAdapter(mAdapterCategory);

        mListViewCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ManageCategoryActivity.class);
                intent.putExtra("category_ID", categoryIDList.get(position));
                startActivity(intent);
            }
        });

        CategoryRepo cRepo = new CategoryRepo(CategoryActivity.this);
        UserRepo uRepo = new UserRepo(CategoryActivity.this);
        User user = uRepo.getUser();

        if (user != null) {
            ArrayList<Category> categoryList = cRepo.getCategoryList(user.id);
            if (categoryList.size() == 0) {
                Category category = new Category();
                category.name = "No Category";
                category.userID = user.id;
                cRepo.insert(category);
                categoryList = cRepo.getCategoryList(user.id);
            }

            for (int i = 0; i < categoryList.size(); i++) {
                Category category = categoryList.get(i);
                categoryIDList.add(category.id);
                mAdapterCategory.add(category.name);
            }
        }
    }

    /**
     * handler function for the ADD button
     * @param v view
     */
    public void handleAddCategoryButton(View v) {
        Intent intent = new Intent(getApplicationContext(), AddCategoryActivity.class);
        startActivity(intent);
    }
}
