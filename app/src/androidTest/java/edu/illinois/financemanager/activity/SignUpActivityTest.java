package edu.illinois.financemanager.activity;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;

public class SignUpActivityTest extends
        ActivityInstrumentationTestCase2<SignUpActivity> {
    private static final String NAME = "B O B";
    private static final String EMAIL = "B O B AT B O B PERIOD C O M";
    private static final String PASSWORD = "H E";
    SignUpActivity signUpActivity;

    public SignUpActivityTest() {
        super(SignUpActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        signUpActivity = getActivity();
    }

    /**
     * Tests if user meets all requirements for sign up
     */
    public void testSignUp() {
        Instrumentation.ActivityMonitor receiverActivityMonitor =
                getInstrumentation().addMonitor(LoginActivity.class.getName(),
                        null, false);

        sendKeys(NAME);
        sendKeys("TAB");
        sendKeys(EMAIL);
        sendKeys("TAB");
        sendKeys(PASSWORD);
        sendKeys("TAB");
        sendKeys(PASSWORD);
        sendKeys("TAB ENTER");
        MainActivity receiverActivity = (MainActivity)
                receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNull(receiverActivity); //password is not long enough

        sendKeys("TAB TAB TAB L L O TAB L L O O TAB ENTER");
        receiverActivity = (MainActivity)
                receiverActivityMonitor.waitForActivityWithTimeout(5000);
        assertNull(receiverActivity); //password does not match

        getInstrumentation().removeMonitor(receiverActivityMonitor);
    }
}
