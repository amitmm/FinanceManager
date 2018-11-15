package edu.illinois.financemanager.object;


public class Category {
    // Labels table name
    public static final String TABLE = "Categories";

    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PID = "parent_id";
    public static final String KEY_CID = "children_id";
    public static final String KEY_UID = "user_id";

    // property help us to keep data
    public long id;
    public String name;
    public long parentID;
    public long childrenID;
    public long userID;
}
