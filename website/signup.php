<?php
require __DIR__ . '/includes/init.php';
require __DIR__ . '/includes/db.php';

// Already signed in? No need to register again.
if (is_logged_in()) {
    header('Location: index.php');
    exit;
}

$errors = [];
$name = '';
$email = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $name     = trim($_POST['full_name'] ?? '');
    $email    = strtolower(trim($_POST['email'] ?? ''));
    $password = $_POST['password'] ?? '';
    $confirm  = $_POST['confirm'] ?? '';

    if (!csrf_ok()) {
        $errors['form'] = 'Your session expired. Reload the page and try again.';
    }
    if (mb_strlen($name) < 2 || mb_strlen($name) > 100) {
        $errors['full_name'] = 'Enter your full name (2 to 100 characters).';
    }
    if (!filter_var($email, FILTER_VALIDATE_EMAIL) || strlen($email) > 150) {
        $errors['email'] = 'Enter a valid email address.';
    }
    if (strlen($password) < 8 || strlen($password) > 72) {
        $errors['password'] = 'Use between 8 and 72 characters.';
    }
    if ($password !== $confirm) {
        $errors['confirm'] = 'The two passwords do not match.';
    }

    if (!$errors) {
        try {
            // Is this email already registered?
            $check = db()->prepare('SELECT id FROM users WHERE email = ?');
            $check->execute([$email]);

            if ($check->fetch()) {
                $errors['email'] = 'This email is already registered. Try signing in.';
            } else {
                // Save a scrambled version of the password, never the password itself.
                $hash = password_hash($password, PASSWORD_DEFAULT);
                $insert = db()->prepare(
                    'INSERT INTO users (full_name, email, password_hash) VALUES (?, ?, ?)'
                );
                $insert->execute([$name, $email, $hash]);

                header('Location: signup.php?done=1');
                exit;
            }
        } catch (PDOException $ex) {
            $errors['form'] = 'The account could not be created. Check that MySQL is running and try again.';
        }
    }
}

$done = isset($_GET['done']);
$pageTitle = 'Sign up: SentryScan';
$active = 'signup';
include __DIR__ . '/includes/header.php';
?>
<section class="page">
    <div class="wrap">
        <h1>Create your account</h1>
        <p class="page-intro">An account lets you sign in to the SentryScan website.</p>

        <?php if ($done): ?>
            <div class="form-card">
                <div class="alert alert-ok" role="status">Your account is created. You can sign in now.</div>
                <div class="form-actions">
                    <a class="btn" href="signin.php">Sign in</a>
                </div>
            </div>
        <?php else: ?>
            <form class="form-card" method="post" action="signup.php">
                <?= csrf_field() ?>

                <?php if (isset($errors['form'])): ?>
                    <div class="alert alert-error" role="alert"><?= e($errors['form']) ?></div>
                <?php endif; ?>

                <label for="full_name">Full name</label>
                <input type="text" id="full_name" name="full_name" value="<?= e($name) ?>"
                       required maxlength="100" autocomplete="name"
                       <?= isset($errors['full_name']) ? 'class="invalid" aria-invalid="true" aria-describedby="err-full_name"' : '' ?>>
                <?php if (isset($errors['full_name'])): ?>
                    <p class="field-error" id="err-full_name"><?= e($errors['full_name']) ?></p>
                <?php endif; ?>

                <label for="email">Email</label>
                <input type="email" id="email" name="email" value="<?= e($email) ?>"
                       required maxlength="150" autocomplete="email"
                       <?= isset($errors['email']) ? 'class="invalid" aria-invalid="true" aria-describedby="err-email"' : '' ?>>
                <?php if (isset($errors['email'])): ?>
                    <p class="field-error" id="err-email"><?= e($errors['email']) ?></p>
                <?php endif; ?>

                <label for="password">Password</label>
                <input type="password" id="password" name="password"
                       required minlength="8" maxlength="72" autocomplete="new-password"
                       <?= isset($errors['password']) ? 'class="invalid" aria-invalid="true" aria-describedby="err-password"' : '' ?>>
                <?php if (isset($errors['password'])): ?>
                    <p class="field-error" id="err-password"><?= e($errors['password']) ?></p>
                <?php else: ?>
                    <p class="hint">At least 8 characters.</p>
                <?php endif; ?>

                <label for="confirm">Confirm password</label>
                <input type="password" id="confirm" name="confirm"
                       required minlength="8" maxlength="72" autocomplete="new-password"
                       <?= isset($errors['confirm']) ? 'class="invalid" aria-invalid="true" aria-describedby="err-confirm"' : '' ?>>
                <?php if (isset($errors['confirm'])): ?>
                    <p class="field-error" id="err-confirm"><?= e($errors['confirm']) ?></p>
                <?php endif; ?>

                <div class="form-actions">
                    <button type="submit" class="btn">Create account</button>
                </div>
                <p class="form-foot">Already registered? <a href="signin.php">Sign in</a></p>
            </form>
        <?php endif; ?>
    </div>
</section>
<?php include __DIR__ . '/includes/footer.php'; ?>
