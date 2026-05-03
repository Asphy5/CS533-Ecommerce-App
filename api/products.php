<?php
header("Content-Type: application/json");

include 'db.php';

$result = $conn->query("SELECT id, name, price FROM products");

$products = [];

while ($row = $result->fetch_assoc()) {
    $products[] = $row;
}

echo json_encode($products);
?>