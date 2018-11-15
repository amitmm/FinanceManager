package edu.illinois.financemanager.object;


public class Budget {
    // Labels table name
    public static final String TABLE = "Budget";

    // Labels Table Columns names
    public static final String KEY_UID = "user_id";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_DATE = "date";

    // property help us to keep data
    public long userID;
    public double amount;
    public String date;
}
