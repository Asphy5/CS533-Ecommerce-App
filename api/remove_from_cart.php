<?php
header("Content-Type: application/json");

include 'db.php';

$data = json_decode(file_get_contents("php://input"));

$user_id = $data->user_id;
$product_id = $data->product_id;

error_log("UID: $user_id PID: $product_id");

$stmt = $conn->prepare("
    DELETE FROM cart 
    WHERE user_id = ? AND product_id = ?
");

$stmt->bind_param("ii", $user_id, $product_id);
$stmt->execute();

echo json_encode(["status" => "success"]);
?>