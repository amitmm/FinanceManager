package edu.illinois.financemanager.repo;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.LargeTest;

import edu.illinois.financemanager.object.User;

/**
 * Unit tests for UserRepo
 */
public class UserRepoTest extends AndroidTestCase {

    private RenamingDelegatingContext testContext;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        testContext = new RenamingDelegatingContext(getContext(), "test_context");
        UserRepo ur = new UserRepo(testContext);
        ur.delete();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        UserRepo ur = new UserRepo(testContext);
        ur.delete();
    }

    @LargeTest
    public void testInsert() {
        UserRepo ur = new UserRepo(testContext);

        User original = new User(1, "Ed", "ed@ed.com");
        ur.insert(original);

        User result = ur.getUser();
        assertEquals(original.id, result.id);
        assertTrue(result.name.equals(original.name));
        assertTrue(result.email.equals(original.email));
    }

    public void testDelete() {
        UserRepo ur = new UserRepo(testContext);

        User addition = new User(1, "Ed", "ed@ed.com");
        ur.insert(addition);

        ur.delete();
        User result = ur.getUser();
        assertEquals(null, result);
    }

    public void testUpdate() {
        UserRepo ur = new UserRepo(testContext);

        User original = new User(1, "Ed", "ed@ed.com");
        ur.insert(original);

        User newData = new User(1, "Edward", "edward@edward.com");
        ur.updateUser(newData);

        User result = ur.getUser();
        assertEquals(original.id, result.id);
        assertTrue(result.name.equals(newData.name));
        assertTrue(result.email.equals(newData.email));
    }
}
