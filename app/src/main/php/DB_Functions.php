<?php

class DB_Functions {

    private $db;

    //put your code here
    // constructor
    function __construct() {
        require_once 'DB_Connect.php';
        // connecting to database
        $this->db = new DB_Connect();
        $this->db->connect();
    }

    // destructor
    function __destruct() {

    }

    /**
     * Storing new user
     * returns user details
     */
    public function storeUser($name, $email, $password) {
        $name = mysql_real_escape_string($name);
        $email = mysql_real_escape_string($email);
        $hash = $this->hashSSHA($password);
        $encrypted_password = $hash["encrypted"]; // encrypted password
        $salt = $hash["salt"]; // salt
        $result = mysql_query("INSERT INTO Users(name, email, encrypted_password, salt) VALUES('$name', '$email', '$encrypted_password', '$salt')");
        // check for successful store
        if ($result) {
            // get user details
            $uid = mysql_insert_id(); // last inserted id
            $result = mysql_query("SELECT * FROM Users WHERE id = $uid");
            // return user details
            return mysql_fetch_array($result);
        } else {
            return false;
        }
    }

     /**
     * Updating new user
     * returns user details
     */
    public function updateUser($id, $name, $email, $new_password) {
        $name = mysql_real_escape_string($name);
        $email = mysql_real_escape_string($email);
        if (isset($new_password) && $new_password != '') {
            $hash = $this->hashSSHA($new_password);
            $encrypted_password = $hash["encrypted"]; // encrypted password
            $salt = $hash["salt"]; // salt
            $result = mysql_query("UPDATE Users SET name = '$name', email = '$email', encrypted_password = '$encrypted_password', salt = '$salt' WHERE id = '$id'");
        } else {
            $result = mysql_query("UPDATE Users SET name = '$name', email = '$email' WHERE id = '$id'");
        }
        // check for successful update
        if ($result) {
            $result = mysql_query("SELECT * FROM Users WHERE id = $id");
            // return user details
            return mysql_fetch_array($result);
        } else {
            return false;
        }
    }

    /**
     * Get user by email and password
     */
    public function getUserByEmailAndPassword($email, $password) {
//error_log("Getting user by email", 0);
        $result = mysql_query("SELECT * FROM Users WHERE email = '$email'") or die(mysql_error());
        // check for result
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
//error_log("Result found", 0);
            $result = mysql_fetch_array($result);
            $salt = $result['salt'];
            $encrypted_password = $result['encrypted_password'];
            $hash = $this->checkhashSSHA($salt, $password);
            // check for password equality
            if ($encrypted_password == $hash) {
                // user authentication details are correct
//error_log("Password is the same", 0);
                return $result;
            }
        } else {
            // user not found
            return false;
        }
    }

     /**
         * Get user by ID and password
         */
        public function getUserByIDAndPassword($id, $password) {
    //error_log("Getting user by email", 0);
            $result = mysql_query("SELECT * FROM Users WHERE id = '$id'") or die(mysql_error());
            // check for result
            $no_of_rows = mysql_num_rows($result);
            if ($no_of_rows > 0) {
    //error_log("Result found", 0);
                $result = mysql_fetch_array($result);
                $salt = $result['salt'];
                $encrypted_password = $result['encrypted_password'];
                $hash = $this->checkhashSSHA($salt, $password);
                // check for password equality
                if ($encrypted_password == $hash) {
                    // user authentication details are correct
    //error_log("Password is the same", 0);
                    return $result;
                }
            } else {
                // user not found
                return false;
            }
        }

    /**
     * Check user is existed or not
     */
    public function isUserExisted($email) {
        $result = mysql_query("SELECT email from Users WHERE email = '$email'");
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            // user existed
            return true;
        } else {
            // user not existed
            return false;
        }
    }

    /**
     * Encrypting password
     * @param password
     * returns salt and encrypted password
     */
    public function hashSSHA($password) {

        $salt = sha1(rand());
        $salt = substr($salt, 0, 10);
        $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
        $hash = array("salt" => $salt, "encrypted" => $encrypted);
        return $hash;
    }

    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     */
    public function checkhashSSHA($salt, $password) {

        $hash = base64_encode(sha1($password . $salt, true) . $salt);

        return $hash;
    }

}