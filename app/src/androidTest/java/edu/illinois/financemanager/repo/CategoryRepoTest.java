package edu.illinois.financemanager.repo;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import java.util.ArrayList;

import edu.illinois.financemanager.object.Category;


public class CategoryRepoTest extends AndroidTestCase {

    private static final String testCategoryName = "test name";
    private static final Long testCategoryUID = 987654321L;
    private static final String updateCategoryName = "update name";

    private RenamingDelegatingContext testContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        testContext = new RenamingDelegatingContext(getContext(), "test_context");

        CategoryRepo cRepo = new CategoryRepo(testContext);
        Category category = cRepo.getCategoryByName(testCategoryName, testCategoryUID);
        while (category != null) {
            cRepo.delete(category.id);
            category = cRepo.getCategoryByName(testCategoryName, testCategoryUID);
        }

        category = cRepo.getCategoryByName(updateCategoryName, testCategoryUID);
        while (category != null) {
            cRepo.delete(category.id);
            category = cRepo.getCategoryByName(updateCategoryName, testCategoryUID);
        }
    }

    @Override
    public void tearDown() throws Exception {
        CategoryRepo cRepo = new CategoryRepo(testContext);
        Category category = cRepo.getCategoryByName(testCategoryName, testCategoryUID);
        while (category != null) {
            cRepo.delete(category.id);
            category = cRepo.getCategoryByName(testCategoryName, testCategoryUID);
        }

        category = cRepo.getCategoryByName(updateCategoryName, testCategoryUID);
        while (category != null) {
            cRepo.delete(category.id);
            category = cRepo.getCategoryByName(updateCategoryName, testCategoryUID);
        }
        super.tearDown();
    }

    @MediumTest
    public void testInsertDelete() throws Exception {
        CategoryRepo cRepo = new CategoryRepo(testContext);
        Category newCategory = new Category();
        newCategory.name = testCategoryName;
        newCategory.userID = testCategoryUID;
        cRepo.insert(newCategory);

        Category checkCategory = cRepo.getCategoryByName(testCategoryName, testCategoryUID);
        assertEquals(testCategoryName, checkCategory.name);
        assertEquals(String.valueOf(testCategoryUID), String.valueOf(checkCategory.userID));

        cRepo.delete(checkCategory.id);
        checkCategory = cRepo.getCategoryByName(testCategoryName, testCategoryUID);
        assertNull(checkCategory);
    }

    @MediumTest
    public void testUpdate() throws Exception {
        CategoryRepo cRepo = new CategoryRepo(testContext);
        Category newCategory = new Category();
        newCategory.name = testCategoryName;
        newCategory.userID = testCategoryUID;
        cRepo.insert(newCategory);

        Category checkCategory = cRepo.getCategoryByName(testCategoryName, testCategoryUID);
        assertEquals(testCategoryName, checkCategory.name);
        assertEquals(String.valueOf(testCategoryUID), String.valueOf(checkCategory.userID));

        checkCategory.name = updateCategoryName;
        checkCategory.userID = testCategoryUID;
        cRepo.update(checkCategory);

        checkCategory = cRepo.getCategoryByName(updateCategoryName, testCategoryUID);
        assertEquals(updateCategoryName, checkCategory.name);
        assertEquals(String.valueOf(testCategoryUID), String.valueOf(checkCategory.userID));

        cRepo.delete(checkCategory.id);
        checkCategory = cRepo.getCategoryByName(testCategoryName, testCategoryUID);
        assertNull(checkCategory);
    }

    @SmallTest
    public void testGetCategoryList() throws Exception {
        CategoryRepo cRepo = new CategoryRepo(testContext);
        Category newCategory = new Category();

        newCategory.name = testCategoryName;
        newCategory.userID = testCategoryUID;
        long cID1 = cRepo.insert(newCategory);

        newCategory.name = updateCategoryName;
        newCategory.userID = testCategoryUID;
        long cID2 = cRepo.insert(newCategory);

        ArrayList<Category> categoryList = cRepo.getCategoryList(testCategoryUID);
        assertEquals(2, categoryList.size());

        assertEquals(cID1, categoryList.get(0).id);
        assertEquals(testCategoryName, categoryList.get(0).name);
        assertEquals(cID2, categoryList.get(1).id);
        assertEquals(updateCategoryName, categoryList.get(1).name);

        cRepo.delete(cID1);
        cRepo.delete(cID2);
        Category checkCategory = cRepo.getCategoryByID(cID1);
        assertNull(checkCategory);

        checkCategory = cRepo.getCategoryByID(cID2);
        assertNull(checkCategory);
    }

    @SmallTest
    public void testGetCategoryByID() throws Exception {
        CategoryRepo cRepo = new CategoryRepo(testContext);
        Category newCategory = new Category();
        newCategory.name = testCategoryName;
        newCategory.userID = testCategoryUID;
        cRepo.insert(newCategory);

        Category checkCategory = cRepo.getCategoryByName(testCategoryName, testCategoryUID);
        assertEquals(testCategoryName, checkCategory.name);
        assertEquals(String.valueOf(testCategoryUID), String.valueOf(checkCategory.userID));

        cRepo.delete(checkCategory.id);
        checkCategory = cRepo.getCategoryByName(testCategoryName, testCategoryUID);
        assertNull(checkCategory);
    }

    @SmallTest
    public void testGetCategoryByName() throws Exception {
        CategoryRepo cRepo = new CategoryRepo(testContext);
        Category newCategory = new Category();
        newCategory.name = testCategoryName;
        newCategory.userID = testCategoryUID;
        long categoryID = cRepo.insert(newCategory);

        Category checkCategory = cRepo.getCategoryByID(categoryID);
        assertEquals(testCategoryName, checkCategory.name);
        assertEquals(String.valueOf(testCategoryUID), String.valueOf(checkCategory.userID));

        cRepo.delete(checkCategory.id);
        checkCategory = cRepo.getCategoryByName(testCategoryName, testCategoryUID);
        assertNull(checkCategory);
    }
}
