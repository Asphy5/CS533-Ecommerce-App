<?php
header("Content-Type: application/json");
include 'db.php';

$data = json_decode(file_get_contents("php://input"));

$id = $data->id;
$name = $data->name;
$price = $data->price;

if ($id == -1) {

    // CREATE NEW PRODUCT
    $stmt = $conn->prepare("
        INSERT INTO products (name, price)
        VALUES (?, ?)
    ");

    $stmt->bind_param("sd", $name, $price);
    $stmt->execute();

    echo json_encode(["status" => "created"]);

} else {

    // UPDATE EXISTING PRODUCT
    $stmt = $conn->prepare("
        UPDATE products 
        SET name = ?, price = ?
        WHERE id = ?
    ");

    $stmt->bind_param("sdi", $name, $price, $id);
    $stmt->execute();

    echo json_encode(["status" => "updated"]);
}
?>