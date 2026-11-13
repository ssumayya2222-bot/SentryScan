package com.example.sentryscan;

import com.example.sentryscan.scanner.IpRange;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sentryscan.controller.ScanController;
import com.example.sentryscan.model.OpenPort;
import com.example.sentryscan.model.ScanResult;
import com.example.sentryscan.view.HistoryActivity;
import com.example.sentryscan.view.ResultsActivity;
import com.example.sentryscan.view.VulnDbActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScanController controller = new ScanController(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 96, 32, 32);

        // UC1: Enter Target
        EditText etTarget = new EditText(this);
        etTarget.setHint("Target IP or hostname (e.g. 192.168.100.1)");
        etTarget.setSingleLine(true);
        root.addView(etTarget);

        // UC2: Start Scan
        Button btnScan = new Button(this);
        btnScan.setText("Start Scan");
        root.addView(btnScan);

        // UC10: Manage Vulnerability DB
        Button btnVulnDb = new Button(this);
        btnVulnDb.setText("Manage Vulnerability DB");
        btnVulnDb.setOnClickListener(v ->
                startActivity(new Intent(this, VulnDbActivity.class)));
        root.addView(btnVulnDb);

        // UC9: View Scan History
        Button btnHistory = new Button(this);
        btnHistory.setText("Scan History");
        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));
        root.addView(btnHistory);
        // Result area
        TextView out = new TextView(this);
        out.setTextSize(16);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(out);
        root.addView(scroll);

        setContentView(root);

        btnScan.setOnClickListener(v -> {
            final String target = etTarget.getText().toString().trim();
            if (target.isEmpty()) {
                Toast.makeText(this, "Please enter a target", Toast.LENGTH_SHORT).show();
                return;
            }
            btnScan.setEnabled(false);

            if (IpRange.isRange(target)) {
                // A range like 192.168.100.1-5
                out.setText("Checking range " + target + " ...");
                final String[] label = {""};
                new Thread(() -> {
                    List<ScanResult> results = controller.onStartRangeScan(target, "common", null,
                            (number, total, host) -> {
                                label[0] = "Host " + number + " of " + total + " (" + host + ")";
                                runOnUiThread(() -> out.setText(label[0]));
                            },
                            (done, total, port) -> runOnUiThread(() ->
                                    out.setText(label[0] + "\nPort " + port
                                            + " (" + done + " of " + total + ")")));
                    runOnUiThread(() -> {
                        btnScan.setEnabled(true);
                        if (results == null) {
                            out.setText("Invalid range. Use a format like 192.168.100.1-20 "
                                    + "(last number only, maximum 50 addresses).");
                        } else {
                            out.setText("Range scan finished. Live hosts scanned: "
                                    + results.size());
                            startActivity(new Intent(this, HistoryActivity.class));
                        }
                    });
                }).start();
                return;
            }

            // A single IP or hostname
            out.setText("Scanning " + target + " ...");
            new Thread(() -> {
                ScanResult r = controller.onStartScan(target, "common", null,
                        (done, total, port) -> runOnUiThread(() ->
                                out.setText("Scanning port " + port
                                        + " (" + done + " of " + total + ")")));
                runOnUiThread(() -> {
                    btnScan.setEnabled(true);
                    Intent i = new Intent(this, ResultsActivity.class);
                    i.putExtra(ResultsActivity.EXTRA_SCAN_ID, r.getId());
                    startActivity(i);
                });
            }).start();
        });
    }
}