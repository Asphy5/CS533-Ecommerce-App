<?php
header("Content-Type: application/json");

include 'db.php';

$data = json_decode(file_get_contents("php://input"));

$username = $data->username;
$password = hash('sha256', $data->password);

$stmt = $conn->prepare("SELECT id, role FROM users WHERE username=? AND password=?");
$stmt->bind_param("ss", $username, $password);
$stmt->execute();

$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    echo json_encode([
        "status" => "success",
        "user_id" => $row["id"],
        "role" => $row["role"]
    ]);
} else {
    echo json_encode([
        "status" => "fail"
    ]);
}
?>