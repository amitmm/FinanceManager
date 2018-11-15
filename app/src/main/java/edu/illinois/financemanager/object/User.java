package edu.illinois.financemanager.object;


public class User {
    //Table Labels
    public static final String TABLE = "User";

    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";

    //Properties of a User
    public long id;
    public String name;
    public String email;

    public User() {
    }

    /**
     * Constructor for a User object
     *
     * @param id id
     * @param name name
     * @param email email
     */
    public User(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
