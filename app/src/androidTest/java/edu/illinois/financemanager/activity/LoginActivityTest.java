package edu.illinois.financemanager.activity;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;

import edu.illinois.financemanager.repo.UserRepo;

public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {
    private static final String EMAIL = "J A N E AT J A N E PERIOD C O M";
    private static final String PASSWORD = "J A N E W A N G";
    LoginActivity loginActivity;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loginActivity = getActivity();
    }

    /**
     * Tests if a user is able to login successfully or unsuccessfully
     */
    public void testLogin() {
        Instrumentation.ActivityMonitor receiverActivityMonitor =
                getInstrumentation().addMonitor(MainActivity.class.getName(),
                        null, false);

        sendKeys(EMAIL + " M");
        sendKeys("TAB");
        sendKeys(PASSWORD);
        sendKeys("TAB ENTER");
        MainActivity receiverActivity = (MainActivity)
                receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNull(receiverActivity);

        sendKeys("TAB DEL TAB TAB ENTER");
        receiverActivity = (MainActivity)
                receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNotNull(receiverActivity);

        getInstrumentation().removeMonitor(receiverActivityMonitor);

        UserRepo ur = new UserRepo(loginActivity);
        ur.delete();
    }
}
