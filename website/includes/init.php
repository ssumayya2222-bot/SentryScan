<?php
// Shared setup for every page: session, small helpers and a form security token.
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

// Makes text safe to print inside HTML (stops scripts injected through forms).
function e($text) {
    return htmlspecialchars((string)$text, ENT_QUOTES, 'UTF-8');
}

// A secret token that every form carries. A form without it is rejected.
function csrf_token() {
    if (empty($_SESSION['csrf'])) {
        $_SESSION['csrf'] = bin2hex(random_bytes(32));
    }
    return $_SESSION['csrf'];
}

function csrf_field() {
    return '<input type="hidden" name="csrf" value="' . e(csrf_token()) . '">';
}

function csrf_ok() {
    return isset($_POST['csrf'], $_SESSION['csrf'])
        && hash_equals($_SESSION['csrf'], $_POST['csrf']);
}

function is_logged_in() {
    return !empty($_SESSION['user_id']);
}
