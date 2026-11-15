<?php
// One shared database connection. Every call to db() returns the same object,
// the same idea as the Singleton DatabaseHelper in the Android app.
function db() {
    static $pdo = null;
    if ($pdo === null) {
        $pdo = new PDO(
            'mysql:host=localhost;dbname=sentryscan_web;charset=utf8mb4',
            'root',   // default XAMPP user
            '',       // default XAMPP password is empty
            [
                PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                PDO::ATTR_EMULATE_PREPARES   => false,
            ]
        );
    }
    return $pdo;
}
