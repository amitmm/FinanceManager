package edu.illinois.financemanager.object;


public class Reminder {

    // Labels table name
    public static final String TABLE = "Reminder";

    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_MSG = "message";
    public static final String KEY_AMT = "amount";
    public static final String KEY_DTE = "start_date";
    public static final String KEY_TME = "start_time";
    public static final String KEY_RPT = "repeatID";
    public static final String KEY_UID = "user_id";

    // property help us to keep data
    public long id;
    public String message;
    public double amount;
    public String startDate;
    public String startTime;
    public long repeatID;
    public long userID;
}
