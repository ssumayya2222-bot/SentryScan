# SentryScan

An Android network vulnerability scanner written in Java. It scans a host for open ports, reads service banners, matches them against a local CVE database and produces a risk-rated report. It runs directly on a phone connected to the target Wi-Fi network.

Semester project, Software Construction and Development (SCD), BSSE Semester 5, Foundation University Islamabad.
Author: Sumayya (Student ID 043)

## Features

- Enter a target: single IP, hostname, or IP range such as 192.168.100.1-20 (maximum 50 addresses)
- TCP connect scan of 13 common ports, with banner grabbing
- Live progress while scanning (host and port counters)
- CVE matching against a local SQLite database, with an overall risk level (Low, Medium, High, Critical)
- Results screen with each open port, its banner and matching CVEs
- Export Report: a colour-coded HTML report that can be shared
- Scan History: view and delete saved scans
- Manage Vulnerability DB: a form to add, edit and delete CVE entries

## Design patterns

- Singleton: DatabaseHelper (one database object for the whole app)
- Factory: ScannerFactory creates CommonPortScanner or CustomPortScanner (both implement the Scanner interface)
- Facade: ScanFacade (one call runs scan, match, risk rating and save)
- MVC: Views (MainActivity, ResultsActivity, HistoryActivity, VulnDbActivity), Controller (ScanController), Models (ScanResult, OpenPort, CveEntry)

## Database

SQLite through DatabaseHelper, with three tables: scans, open_ports and cve_entries. The seeded CVE entries were checked against the National Vulnerability Database (nvd.nist.gov).

## Project structure

- model: ScanResult, OpenPort, CveEntry
- database: DatabaseHelper
- scanner: Scanner, CommonPortScanner, CustomPortScanner, ScannerFactory, VulnerabilityMatcher, IpRange, ProgressListener, HostListener
- controller: ScanController, ScanFacade
- report: ReportGenerator
- view: ResultsActivity, HistoryActivity, VulnDbActivity
- docs: proposal and UML diagrams
- screenshots: proof of each working feature

## How to run

1. Open the project in Android Studio.
2. Connect an Android phone (USB or Wi-Fi debugging) and connect it to the Wi-Fi network you want to test.
3. Press Run.
4. Enter a target and tap Start Scan.

## Limitations

- Matching works on banner text, so it only flags services that announce their name and version.
- The vulnerability database is a small local subset, so results are not exhaustive.
- Range scanning skips hosts that do not answer a quick reachability check.
- TCP connect scan only (no SYN or UDP scan).

## Ethical use

Only scan devices and networks you own or have written permission to test.

## Website

The `website` folder holds the SentryScan website, built with PHP and MySQL.

Pages:

- Home (index.php)
- Sign up (signup.php): saves a new user, with the password stored as a hash
- Sign in (signin.php) and Log out (logout.php): checks the password against the stored hash
- Contact us (contact.php): saves the message in the database

Security measures: prepared statements (against SQL injection), password hashing, a form token on every form, and escaped output.

How to run it:

1. Install XAMPP and start Apache and MySQL.
2. Copy the `website` folder to `C:\xampp\htdocs\sentryscan_web`.
3. In phpMyAdmin, open the SQL tab, paste the contents of `website/database.sql` and click Go.
4. Open http://localhost/sentryscan_web/ in a browser.

The database settings in `includes/db.php` are the XAMPP defaults (user root, empty password). They are for local use only.

Screenshots of each page are in `screenshots/website`.
