<?php
/**
 * File to handle all API requests
 * Accepts GET and POST
 *
 * Each request will be identified by TAG
 * Response will be JSON data

  /**
 * check for POST request
 */
error_log("Someone was here.",0);
$json = file_get_contents('php://input');
$data = json_decode($json);
if (isset($data->tag) && $data->tag != '') {
    // get tag
    $tag = $data->tag;

    // include db handler
    require_once 'DB_Functions.php';
    $db = new DB_Functions();

    // response Array
    $response = array("tag" => $tag, "success" => 0, "error" => 0);

    // check for tag type
    if ($tag == 'login') {
        // Request type is check Login
        $email = $data->email;
        $password = $data->password;

        // check for user
        $user = $db->getUserByEmailAndPassword($email, $password);
        if ($user != false) {
            // user found
            // echo json with success = 1
            $response["success"] = 1;
            $response["user"]["name"] = $user["name"];
            $response["user"]["email"] = $user["email"];
            $response["user"]["id"] = $user["id"];
            echo json_encode($response);
        } else {
            // user not found
            // echo json with error = 1
            $response["error"] = 1;
            $response["error_msg"] = "Incorrect email or password!";
            echo json_encode($response);
        }
    } else if ($tag == 'register') {
        // Request type is Register new user
//error_log("Begin registration", 0);
        $name = $data->name;
        $email = $data->email;
        $password = $data->password;

        // check if user is already existed
        if ($db->isUserExisted($email)) {
            // user is already existed - error response
            $response["error"] = 2;
            $response["error_msg"] = "User already exists!";
            echo json_encode($response);
        } else {
//error_log("User stored", 0);
            // store user
            $user = $db->storeUser($name, $email, $password);
            if ($user) {
                // user stored successfully
                $response["success"] = 1;
                $response["user"]["name"] = $user["name"];
                $response["user"]["email"] = $user["email"];
                echo json_encode($response);
            } else {
                // user failed to store
                $response["error"] = 1;
                $response["error_msg"] = "Error occurred in Registration!";
                echo json_encode($response);
            }
        }
    } else {
//error_log("Invalid request",0);
        echo "Invalid Request";
    }
} else {
//error_log("Access denied", 0);
    echo "Access Denied";
}
?>