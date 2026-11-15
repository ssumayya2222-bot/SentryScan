<?php
require __DIR__ . '/includes/init.php';
$pageTitle = 'SentryScan: network vulnerability scanner';
$active = 'home';
include __DIR__ . '/includes/header.php';
?>
<section class="hero">
    <div class="wrap hero-grid">
        <div>
            <h1>Check a network for known weaknesses from your phone</h1>
            <p class="lead">SentryScan looks for open ports, reads what each service says about itself, and matches it against known vulnerabilities. You get a risk-rated report you can share.</p>
            <?php if (is_logged_in()): ?>
                <p class="lead">Welcome back, <?= e($_SESSION['user_name']) ?>. Use the Android app to run your next scan.</p>
                <div class="actions">
                    <a class="btn" href="contact.php">Contact us</a>
                </div>
            <?php else: ?>
                <div class="actions">
                    <a class="btn" href="signup.php">Create an account</a>
                    <a class="btn btn-quiet" href="signin.php">Sign in</a>
                </div>
            <?php endif; ?>
        </div>

        <aside class="scan-card" aria-label="Example scan result">
            <div class="scan-head">
                <span class="scan-label">Example result</span>
                <span>Overall risk <span class="badge badge-high">High</span></span>
            </div>
            <div class="scan-target">Scan of 192.168.100.1</div>
            <div class="port-row">
                <strong>Port 80 (HTTP)</strong>
                <div class="banner">HTTP/1.1 200 OK Server: Apache/2.4.49 (Unix)</div>
                <div class="cve">
                    <span class="badge badge-high">High</span>
                    <span><strong>CVE-2021-41773</strong>: path traversal in Apache HTTP Server 2.4.49</span>
                </div>
            </div>
        </aside>
    </div>
</section>

<section class="section">
    <div class="wrap">
        <h2>What SentryScan does</h2>
        <dl class="feature-list">
            <div class="feature">
                <dt>Scan ports and banners</dt>
                <dd>Checks the common ports of a single address, a hostname, or a range such as 192.168.100.1-20, and reads each open service's banner.</dd>
            </div>
            <div class="feature">
                <dt>Match known vulnerabilities</dt>
                <dd>Compares each banner with a local list of CVEs checked against the National Vulnerability Database, and sets a risk level from Low to Critical.</dd>
            </div>
            <div class="feature">
                <dt>Show live progress</dt>
                <dd>Shows which host and port is being checked while the scan runs.</dd>
            </div>
            <div class="feature">
                <dt>Export a report</dt>
                <dd>Saves the result as a colour-coded HTML report that you can open in any browser or send to someone.</dd>
            </div>
            <div class="feature">
                <dt>Keep a scan history</dt>
                <dd>Saves every scan on the phone, so you can open an old result or delete it.</dd>
            </div>
            <div class="feature">
                <dt>Manage the vulnerability list</dt>
                <dd>Add, edit and delete the CVE entries that scans are matched against.</dd>
            </div>
        </dl>
    </div>
</section>

<section class="section">
    <div class="wrap">
        <h2>How a scan works</h2>
        <ol class="steps">
            <li>Enter a target: one address, a hostname, or a short range.</li>
            <li>SentryScan checks the ports and reads the banners.</li>
            <li>It matches the banners with known CVEs, rates the risk and saves the result.</li>
            <li>Open the result, or export it as a report.</li>
        </ol>
        <p class="note">SentryScan only reports what a service announces in its banner. A clean result does not prove a system is safe.</p>
    </div>
</section>
<?php include __DIR__ . '/includes/footer.php'; ?>
