<?php
require __DIR__ . '/includes/init.php';
require __DIR__ . '/includes/db.php';

$errors = [];
$name = is_logged_in() ? $_SESSION['user_name'] : '';
$email = '';
$message = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $name    = trim($_POST['name'] ?? '');
    $email   = strtolower(trim($_POST['email'] ?? ''));
    $message = trim($_POST['message'] ?? '');

    if (!csrf_ok()) {
        $errors['form'] = 'Your session expired. Reload the page and try again.';
    }
    if (mb_strlen($name) < 2 || mb_strlen($name) > 100) {
        $errors['name'] = 'Enter your name (2 to 100 characters).';
    }
    if (!filter_var($email, FILTER_VALIDATE_EMAIL) || strlen($email) > 150) {
        $errors['email'] = 'Enter a valid email address.';
    }
    if (mb_strlen($message) < 10 || mb_strlen($message) > 2000) {
        $errors['message'] = 'Write a message between 10 and 2000 characters.';
    }

    if (!$errors) {
        try {
            $stmt = db()->prepare(
                'INSERT INTO contact_messages (name, email, message) VALUES (?, ?, ?)'
            );
            $stmt->execute([$name, $email, $message]);

            header('Location: contact.php?sent=1');
            exit;
        } catch (PDOException $ex) {
            $errors['form'] = 'Your message could not be saved. Check that MySQL is running and try again.';
        }
    }
}

$sent = isset($_GET['sent']);
$pageTitle = 'Contact us: SentryScan';
$active = 'contact';
include __DIR__ . '/includes/header.php';
?>
<section class="page">
    <div class="wrap">
        <h1>Contact us</h1>
        <p class="page-intro">Have a question or feedback about SentryScan? Send a message and it is saved in the project database.</p>

        <?php if ($sent): ?>
            <div class="form-card wide">
                <div class="alert alert-ok" role="status">Thank you. Your message has been sent.</div>
                <div class="form-actions">
                    <a class="btn" href="contact.php">Send another message</a>
                    <a class="btn btn-quiet" href="index.php">Back to Home</a>
                </div>
            </div>
        <?php else: ?>
            <form class="form-card wide" method="post" action="contact.php">
                <?= csrf_field() ?>

                <?php if (isset($errors['form'])): ?>
                    <div class="alert alert-error" role="alert"><?= e($errors['form']) ?></div>
                <?php endif; ?>

                <label for="name">Name</label>
                <input type="text" id="name" name="name" value="<?= e($name) ?>"
                       required maxlength="100" autocomplete="name"
                       <?= isset($errors['name']) ? 'class="invalid" aria-invalid="true" aria-describedby="err-name"' : '' ?>>
                <?php if (isset($errors['name'])): ?>
                    <p class="field-error" id="err-name"><?= e($errors['name']) ?></p>
                <?php endif; ?>

                <label for="email">Email</label>
                <input type="email" id="email" name="email" value="<?= e($email) ?>"
                       required maxlength="150" autocomplete="email"
                       <?= isset($errors['email']) ? 'class="invalid" aria-invalid="true" aria-describedby="err-email"' : '' ?>>
                <?php if (isset($errors['email'])): ?>
                    <p class="field-error" id="err-email"><?= e($errors['email']) ?></p>
                <?php endif; ?>

                <label for="message">Message</label>
                <textarea id="message" name="message" required minlength="10" maxlength="2000"
                          <?= isset($errors['message']) ? 'class="invalid" aria-invalid="true" aria-describedby="err-message"' : '' ?>><?= e($message) ?></textarea>
                <?php if (isset($errors['message'])): ?>
                    <p class="field-error" id="err-message"><?= e($errors['message']) ?></p>
                <?php else: ?>
                    <p class="hint">At least 10 characters.</p>
                <?php endif; ?>

                <div class="form-actions">
                    <button type="submit" class="btn">Send message</button>
                </div>
            </form>
        <?php endif; ?>
    </div>
</section>
<?php include __DIR__ . '/includes/footer.php'; ?>
