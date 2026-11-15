<?php
// Set $pageTitle and $active ('home', 'contact' or 'signin') before including this file.
$pageTitle = $pageTitle ?? 'SentryScan';
$active = $active ?? '';
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><?= e($pageTitle) ?></title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<a class="skip-link" href="#main">Skip to content</a>
<header class="site-header">
    <div class="wrap bar">
        <a class="brand" href="index.php"><span class="brand-mark" aria-hidden="true"></span>SentryScan</a>
        <nav aria-label="Main">
            <ul>
                <li><a href="index.php"<?= $active === 'home' ? ' aria-current="page"' : '' ?>>Home</a></li>
                <li><a href="contact.php"<?= $active === 'contact' ? ' aria-current="page"' : '' ?>>Contact us</a></li>
                <?php if (is_logged_in()): ?>
                    <li><span class="nav-user">Signed in as <?= e($_SESSION['user_name']) ?></span></li>
                    <li><a href="logout.php">Log out</a></li>
                <?php else: ?>
                    <li><a href="signin.php"<?= $active === 'signin' ? ' aria-current="page"' : '' ?>>Sign in</a></li>
                    <li><a class="btn" href="signup.php">Sign up</a></li>
                <?php endif; ?>
            </ul>
        </nav>
    </div>
</header>
<main id="main">
