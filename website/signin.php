<?php
require __DIR__ . '/includes/init.php';
require __DIR__ . '/includes/db.php';

// Already signed in? Go to the Home page.
if (is_logged_in()) {
    header('Location: index.php');
    exit;
}

$error = '';
$email = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $email    = strtolower(trim($_POST['email'] ?? ''));
    $password = $_POST['password'] ?? '';

    if (!csrf_ok()) {
        $error = 'Your session expired. Reload the page and try again.';
    } elseif ($email === '' || $password === '') {
        $error = 'Enter your email and password.';
    } else {
        try {
            $stmt = db()->prepare('SELECT id, full_name, password_hash FROM users WHERE email = ?');
            $stmt->execute([$email]);
            $user = $stmt->fetch();

            // password_verify compares the typed password with the stored hash.
            if ($user && password_verify($password, $user['password_hash'])) {
                session_regenerate_id(true);   // new session ID after login
                $_SESSION['user_id']   = (int)$user['id'];
                $_SESSION['user_name'] = $user['full_name'];
                header('Location: index.php');
                exit;
            }
            // Same message for a wrong email and a wrong password.
            $error = 'Incorrect email or password.';
        } catch (PDOException $ex) {
            $error = 'Sign in is not available right now. Check that MySQL is running and try again.';
        }
    }
}

$signedOut = isset($_GET['out']);
$pageTitle = 'Sign in: SentryScan';
$active = 'signin';
include __DIR__ . '/includes/header.php';
?>
<section class="page">
    <div class="wrap">
        <h1>Sign in</h1>
        <p class="page-intro">Use the email and password you registered with.</p>

        <form class="form-card" method="post" action="signin.php">
            <?= csrf_field() ?>

            <?php if ($signedOut): ?>
                <div class="alert alert-ok" role="status">You have been signed out.</div>
            <?php endif; ?>
            <?php if ($error !== ''): ?>
                <div class="alert alert-error" role="alert"><?= e($error) ?></div>
            <?php endif; ?>

            <label for="email">Email</label>
            <input type="email" id="email" name="email" value="<?= e($email) ?>"
                   required maxlength="150" autocomplete="email">

            <label for="password">Password</label>
            <input type="password" id="password" name="password"
                   required maxlength="72" autocomplete="current-password">

            <div class="form-actions">
                <button type="submit" class="btn">Sign in</button>
            </div>
            <p class="form-foot">New here? <a href="signup.php">Create an account</a></p>
        </form>
    </div>
</section>
<?php include __DIR__ . '/includes/footer.php'; ?>
