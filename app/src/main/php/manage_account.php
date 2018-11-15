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
error_log("Someone changed info.",0);
$json = file_get_contents('php://input');
$data = json_decode($json);

// include db handler
require_once 'DB_Functions.php';
$db = new DB_Functions();

// response Array
$response = array("status" => "");

// Request type is check Login
$id = $data->id;
$current_password = $data->current_password;
$name = $data->name;
$email = $data->email;
$new_password = $data->new_password;

// check for user
$user = $db->getUserByIDAndPassword($id, $current_password);
if ($user != false) {
    // user found
    // echo json with success = 1
    $response["status"] = "Success!";
    $user = $db->updateUser($id, $name, $email, $new_password);
    $response["name"] = $user["name"];
    $response["email"] = $user["email"];
    $response["id"] = $user["id"];
    echo json_encode($response);
} else {
    // user not found
    // echo json with error = 1
    $response["status"] = "Incorrect password!";
    echo json_encode($response);
}

?>