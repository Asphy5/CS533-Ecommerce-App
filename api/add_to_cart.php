<?php
header("Content-Type: application/json");

include 'db.php';

$data = json_decode(file_get_contents("php://input"));

$user_id = $data->user_id;
$product_id = $data->product_id;
$quantity = $data->quantity;

// check if already exists
$stmt = $conn->prepare("
    SELECT quantity 
    FROM cart 
    WHERE user_id = ? AND product_id = ?
");

$stmt->bind_param("ii", $user_id, $product_id);
$stmt->execute();

$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {

    // update quantity instead of inserting
    $newQty = $row['quantity'] + $quantity;

    $update = $conn->prepare("
        UPDATE cart 
        SET quantity = ? 
        WHERE user_id = ? AND product_id = ?
    ");

    $update->bind_param("iii", $newQty, $user_id, $product_id);
    $update->execute();

} else {

    // insert new item
    $insert = $conn->prepare("
        INSERT INTO cart (user_id, product_id, quantity)
        VALUES (?, ?, ?)
    ");

    $insert->bind_param("iii", $user_id, $product_id, $quantity);
    $insert->execute();
}

echo json_encode(["status" => "success"]);
?>