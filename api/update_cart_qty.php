<?php
header("Content-Type: application/json");

include 'db.php';

$data = json_decode(file_get_contents("php://input"));

if (!$data) {
    echo json_encode(["status" => "error", "msg" => "invalid_input"]);
    exit;
}

$user_id = $data->user_id;
$product_id = $data->product_id;
$delta = $data->delta;

$stmt = $conn->prepare("
    SELECT quantity 
    FROM cart 
    WHERE user_id = ? AND product_id = ?
");

$stmt->bind_param("ii", $user_id, $product_id);
$stmt->execute();

$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {

    $newQty = $row['quantity'] + $delta;

    if ($newQty <= 0) {

        $del = $conn->prepare("
            DELETE FROM cart 
            WHERE user_id = ? AND product_id = ?
        ");

        $del->bind_param("ii", $user_id, $product_id);
        $del->execute();

    } else {

        $up = $conn->prepare("
            UPDATE cart 
            SET quantity = ? 
            WHERE user_id = ? AND product_id = ?
        ");

        $up->bind_param("iii", $newQty, $user_id, $product_id);
        $up->execute();
    }

    echo json_encode(["status" => "success"]);
} else {

    echo json_encode(["status" => "error", "msg" => "not_found"]);
}
?>