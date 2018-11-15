package edu.illinois.financemanager.object;

import java.util.Date;


public class Transaction {

    //Table labels
    public static final String TABLE = "Transactions";
    public static final String KEY_ID = "id";
    public static final String KEY_UID = "user_id";
    public static final String KEY_TYPE = "type";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_DATE = "date";
    public static final String KEY_PIC = "pic";

    //properties of transaction
    public String type;
    public float amount;
    public String message;
    public long userID;
    public int id;
    public String category;
    public Date date;
    public String pic;

    public Transaction() {
    }

    /**
     *
     * @param type type
     * @param amount amount
     * @param message message
     * @param userID userID
     * @param category category
     * @param date date
     * @param pic pic
     */
    public Transaction(String type, float amount, String message, long userID, String category, Date date, String pic) {
        this.type = type;
        this.amount = amount;
        this.message = message;
        this.userID = userID;
        this.category = category;
        this.date = date;
        this.pic = pic;
    }

}
