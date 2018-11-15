package edu.illinois.financemanager.activity;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import edu.illinois.financemanager.R;
import edu.illinois.financemanager.object.Category;
import edu.illinois.financemanager.object.User;
import edu.illinois.financemanager.repo.CategoryRepo;
import edu.illinois.financemanager.repo.UserRepo;

public class CategoryActivityTest extends ActivityInstrumentationTestCase2<CategoryActivity> {

    private static final String testCategoryName = "test name";
    private static final String testCategoryNameNew = "test name new";
    private static final String defaultCategoryName = "No Category";
    private static final String defaultCategoryNameNew = "No Category New";

    private static final String testUserName = "test";
    private static final String testUserEmail = "test@test.test";
    private static final Long testUserID = 987654321L;

    private static final String TAG = "ActivityTest";
    private static boolean flagSetUp = true;
    private static boolean flagTearDown = false;
    private CategoryActivity testActivity;
    private Button testAddCategoryButton;
    private ListView testCategoryListView;

    public CategoryActivityTest() {
        super(CategoryActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Log.d(TAG, "setUp");

        if (flagSetUp) {
            flagSetUp = false;

            UserRepo uRepo = new UserRepo(getActivity());
            User user = uRepo.getUser();
            if (user != null) {
                uRepo.delete();
            }

            user = new User();
            user.name = testUserName;
            user.email = testUserEmail;
            user.id = testUserID;
            uRepo.insert(user);

            CategoryRepo cRepo = new CategoryRepo(getActivity());
            ArrayList<Category> reminderList = cRepo.getCategoryList(testUserID);
            for (int i = 0; i < reminderList.size(); i++) {
                cRepo.delete(reminderList.get(i).id);
            }
        }

        testActivity = getActivity();
        testAddCategoryButton = (Button) testActivity.findViewById(R.id.add_category_button);
        testCategoryListView = (ListView) testActivity.findViewById(R.id.category_list_view);
    }

    @Override
    protected void tearDown() throws Exception {
        if (flagTearDown) {
            testActivity = getActivity();
            CategoryRepo cRepo = new CategoryRepo(testActivity);
            Category category = cRepo.getCategoryByName(testCategoryName, testUserID);
            while (category != null) {
                cRepo.delete(category.id);
                category = cRepo.getCategoryByName(testCategoryName, testUserID);
            }

            category = cRepo.getCategoryByName(testCategoryNameNew, testUserID);
            while (category != null) {
                cRepo.delete(category.id);
                category = cRepo.getCategoryByName(testCategoryNameNew, testUserID);
            }

            UserRepo uRepo = new UserRepo(testActivity);
            uRepo.delete();
        }

        Log.d(TAG, "tearDown");
        super.tearDown();
    }

    @SmallTest
    public void test0Preconditions() {
        Log.d(TAG, "test0Preconditions");

        assertNotNull("testActivity is null", testActivity);
        assertNotNull("testAddCategoryButton is null", testAddCategoryButton);
        assertNotNull("testCategoryListView is null", testCategoryListView);
    }

    @LargeTest
    public void test1AddCategory() {
        Log.d(TAG, "test1AddCategory");

        // test add category button
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(AddCategoryActivity.class.getName(), null, false);
        TouchUtils.clickView(this, testAddCategoryButton);
        AddCategoryActivity addCategoryActivity = (AddCategoryActivity) receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNotNull("ReceiverActivity is null", addCategoryActivity);
        assertEquals("Monitor for ReceiverActivity has not been called", 1, receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", AddCategoryActivity.class, addCategoryActivity.getClass());
        getInstrumentation().removeMonitor(receiverActivityMonitor);

        // test add new category with new name
        final EditText newCategoryName = (EditText) addCategoryActivity.findViewById(R.id.new_category_name);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                newCategoryName.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync(testCategoryName);
        getInstrumentation().waitForIdleSync();
        assertEquals("Category name input failed", testCategoryName, newCategoryName.getText().toString());

        // check return to category activity
        final Button saveButton = (Button) addCategoryActivity.findViewById(R.id.save_category_button);
        receiverActivityMonitor = getInstrumentation().addMonitor(CategoryActivity.class.getName(), null, false);
        TouchUtils.clickView(this, saveButton);
        CategoryActivity categoryActivity = (CategoryActivity) receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNotNull("ReceiverActivity is null", categoryActivity);
        assertEquals("Monitor for ReceiverActivity has not been called", 1, receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", CategoryActivity.class, categoryActivity.getClass());
        getInstrumentation().removeMonitor(receiverActivityMonitor);

        ListView listView = (ListView) categoryActivity.findViewById(R.id.category_list_view);
        String categoryName = (String) listView.getAdapter().getItem(1);
        assertEquals("Wrong category name", testCategoryName, categoryName);

        addCategoryActivity.finish();
        categoryActivity.finish();
    }

    @LargeTest
    public void test2UpdateCategory() {
        Log.d(TAG, "test2UpdateCategory");

        // test category is in list
        String categoryName = (String) testCategoryListView.getAdapter().getItem(1);
        assertEquals("Wrong category name", testCategoryName, categoryName);

        // test click list view
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(ManageCategoryActivity.class.getName(), null, false);
        TouchUtils.clickView(this, testCategoryListView.getChildAt(1));
        ManageCategoryActivity manageCategoryActivity = (ManageCategoryActivity) receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNotNull("ReceiverActivity is null", manageCategoryActivity);
        assertEquals("Monitor for ReceiverActivity has not been called", 1, receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", ManageCategoryActivity.class, manageCategoryActivity.getClass());
        getInstrumentation().removeMonitor(receiverActivityMonitor);

        // test add new category with new name
        final EditText newCategoryName = (EditText) manageCategoryActivity.findViewById(R.id.new_category_name);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                newCategoryName.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        for (int i = 0; i < 10; i++) {
            getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_FORWARD_DEL);
        }
        getInstrumentation().sendStringSync(testCategoryNameNew);
        getInstrumentation().waitForIdleSync();
        assertEquals("Category name input failed", testCategoryNameNew, newCategoryName.getText().toString());

        // check return to category activity
        final Button updateButton = (Button) manageCategoryActivity.findViewById(R.id.update_category_button);
        receiverActivityMonitor = getInstrumentation().addMonitor(CategoryActivity.class.getName(), null, false);
        TouchUtils.clickView(this, updateButton);
        CategoryActivity categoryActivity = (CategoryActivity) receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNotNull("ReceiverActivity is null", categoryActivity);
        assertEquals("Monitor for ReceiverActivity has not been called", 1, receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", CategoryActivity.class, categoryActivity.getClass());
        getInstrumentation().removeMonitor(receiverActivityMonitor);

        ListView listView = (ListView) categoryActivity.findViewById(R.id.category_list_view);
        categoryName = (String) listView.getAdapter().getItem(1);
        assertEquals("Wrong category name", testCategoryNameNew, categoryName);

        manageCategoryActivity.finish();
        categoryActivity.finish();
    }

    @LargeTest
    public void test3DeleteCategory() {
        Log.d(TAG, "test2UpdateCategory");

        // test category is in list
        String categoryName = (String) testCategoryListView.getAdapter().getItem(1);
        assertEquals("Wrong category name", testCategoryNameNew, categoryName);

        // test click list view
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(ManageCategoryActivity.class.getName(), null, false);
        TouchUtils.clickView(this, testCategoryListView.getChildAt(1));
        ManageCategoryActivity manageCategoryActivity = (ManageCategoryActivity) receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNotNull("ReceiverActivity is null", manageCategoryActivity);
        assertEquals("Monitor for ReceiverActivity has not been called", 1, receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", ManageCategoryActivity.class, manageCategoryActivity.getClass());
        getInstrumentation().removeMonitor(receiverActivityMonitor);

        // check return to category activity
        final Button updateButton = (Button) manageCategoryActivity.findViewById(R.id.delete_category_button);
        receiverActivityMonitor = getInstrumentation().addMonitor(CategoryActivity.class.getName(), null, false);
        TouchUtils.clickView(this, updateButton);
        CategoryActivity categoryActivity = (CategoryActivity) receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNotNull("ReceiverActivity is null", categoryActivity);
        assertEquals("Monitor for ReceiverActivity has not been called", 1, receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", CategoryActivity.class, categoryActivity.getClass());
        getInstrumentation().removeMonitor(receiverActivityMonitor);

        ListView listView = (ListView) categoryActivity.findViewById(R.id.category_list_view);
        assertNull("Delete category failed", listView.getChildAt(1));

        manageCategoryActivity.finish();
        categoryActivity.finish();
    }

    @LargeTest
    public void test4UpdateDefaultCategory() {
        Log.d(TAG, "test2UpdateCategory");

        // test category is in list
        String categoryName = (String) testCategoryListView.getAdapter().getItem(0);
        assertEquals("Wrong category name", defaultCategoryName, categoryName);

        // test click list view
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(ManageCategoryActivity.class.getName(), null, false);
        TouchUtils.clickView(this, testCategoryListView.getChildAt(0));
        ManageCategoryActivity manageCategoryActivity = (ManageCategoryActivity) receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNotNull("ReceiverActivity is null", manageCategoryActivity);
        assertEquals("Monitor for ReceiverActivity has not been called", 1, receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", ManageCategoryActivity.class, manageCategoryActivity.getClass());
        getInstrumentation().removeMonitor(receiverActivityMonitor);

        // test add new category with new name
        final EditText newCategoryName = (EditText) manageCategoryActivity.findViewById(R.id.new_category_name);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                newCategoryName.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        for (int i = 0; i < 20; i++) {
            getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_FORWARD_DEL);
        }
        getInstrumentation().sendStringSync(defaultCategoryNameNew);
        getInstrumentation().waitForIdleSync();
        assertEquals("Category name input failed", defaultCategoryNameNew, newCategoryName.getText().toString());

        // check return to category activity
        final Button updateButton = (Button) manageCategoryActivity.findViewById(R.id.update_category_button);
        receiverActivityMonitor = getInstrumentation().addMonitor(CategoryActivity.class.getName(), null, false);
        TouchUtils.clickView(this, updateButton);
        CategoryActivity categoryActivity = (CategoryActivity) receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNotNull("ReceiverActivity is null", categoryActivity);
        assertEquals("Monitor for ReceiverActivity has not been called", 1, receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", CategoryActivity.class, categoryActivity.getClass());
        getInstrumentation().removeMonitor(receiverActivityMonitor);

        // check default name didn't change
        ListView listView = (ListView) categoryActivity.findViewById(R.id.category_list_view);
        categoryName = (String) listView.getAdapter().getItem(0);
        assertEquals("Wrong category name", defaultCategoryName, categoryName);

        manageCategoryActivity.finish();
        categoryActivity.finish();
    }

    @LargeTest
    public void test5DeleteDefaultCategory() {
        Log.d(TAG, "test2UpdateCategory");

        // test category is in list
        String categoryName = (String) testCategoryListView.getAdapter().getItem(0);
        assertEquals("Wrong category name", defaultCategoryName, categoryName);

        // test click list view
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(ManageCategoryActivity.class.getName(), null, false);
        TouchUtils.clickView(this, testCategoryListView.getChildAt(0));
        ManageCategoryActivity manageCategoryActivity = (ManageCategoryActivity) receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNotNull("ReceiverActivity is null", manageCategoryActivity);
        assertEquals("Monitor for ReceiverActivity has not been called", 1, receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", ManageCategoryActivity.class, manageCategoryActivity.getClass());
        getInstrumentation().removeMonitor(receiverActivityMonitor);

        // check return to category activity
        final Button updateButtonNew = (Button) manageCategoryActivity.findViewById(R.id.delete_category_button);
        receiverActivityMonitor = getInstrumentation().addMonitor(CategoryActivity.class.getName(), null, false);
        TouchUtils.clickView(this, updateButtonNew);
        CategoryActivity categoryActivity = (CategoryActivity) receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNotNull("ReceiverActivity is null", categoryActivity);
        assertEquals("Monitor for ReceiverActivity has not been called", 1, receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", CategoryActivity.class, categoryActivity.getClass());
        getInstrumentation().removeMonitor(receiverActivityMonitor);

        // check default name didn't change
        ListView listView = (ListView) categoryActivity.findViewById(R.id.category_list_view);
        categoryName = (String) listView.getAdapter().getItem(0);
        assertEquals("Wrong category name", defaultCategoryName, categoryName);

        manageCategoryActivity.finish();
        categoryActivity.finish();

        flagTearDown = true;
    }

}
