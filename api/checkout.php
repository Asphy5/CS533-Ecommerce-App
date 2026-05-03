<?php
header("Content-Type: application/json");

include 'db.php';

$data = json_decode(file_get_contents("php://input"));

$user_id = $data->user_id;
$card_number = $data->card_number; // fake validation only

// 1. Get cart items
$stmt = $conn->prepare("SELECT * FROM cart WHERE user_id = ?");
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows == 0) {
    echo json_encode(["status" => "empty_cart"]);
    exit;
}

// 2. (Fake) validate card format
if (strlen($card_number) != 16) {
    echo json_encode(["status" => "invalid_card"]);
    exit;
}

// 3. Clear cart after checkout
$stmt2 = $conn->prepare("DELETE FROM cart WHERE user_id = ?");
$stmt2->bind_param("i", $user_id);
$stmt2->execute();

echo json_encode([
    "status" => "success",
    "message" => "Order placed successfully"
]);
?>