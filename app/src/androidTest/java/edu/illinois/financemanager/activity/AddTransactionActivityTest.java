package edu.illinois.financemanager.activity;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import edu.illinois.financemanager.object.User;
import edu.illinois.financemanager.repo.UserRepo;


public class AddTransactionActivityTest extends ActivityInstrumentationTestCase2<AddTransactionActivity> {

    private static final String NAME = "B U R G E R";
    private static final String AMOUNT = "5 0";

    private static final String testUserName = "test";
    private static final String testUserEmail = "test@test.test";
    private static final Long testUserID = 987654321L;

    AddTransactionActivity AddTransactionActivity;

    public AddTransactionActivityTest() {
        super(AddTransactionActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

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

        AddTransactionActivity = getActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        UserRepo uRepo = new UserRepo(getActivity());
        uRepo.delete();
        super.tearDown();
    }

    @SmallTest
    public void testAddTransaction() {
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(AddTransactionActivity.class.getName(), null, false);
        sendKeys("TAB");
        sendKeys(NAME);
        sendKeys("TAB");
        sendKeys(AMOUNT);
        sendKeys("TAB TAB ENTER");
        sendKeys("TAB ENTER ENTER");
        getInstrumentation().removeMonitor(receiverActivityMonitor);
    }
}
