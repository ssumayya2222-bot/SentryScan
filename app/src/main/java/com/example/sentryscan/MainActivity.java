package com.example.sentryscan;

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

        // Result area
        TextView out = new TextView(this);
        out.setTextSize(16);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(out);
        root.addView(scroll);

        setContentView(root);

        btnScan.setOnClickListener(v -> {
            String target = etTarget.getText().toString();
            if (target.trim().isEmpty()) {
                Toast.makeText(this, "Please enter a target", Toast.LENGTH_SHORT).show();
                return;
            }
            btnScan.setEnabled(false);
            out.setText("Scanning " + target.trim() + " ...");

            new Thread(() -> {
                ScanResult r = controller.onStartScan(target, "common", null);
                StringBuilder sb = new StringBuilder();
                sb.append("Target: ").append(r.getTarget()).append("\n");
                sb.append("Date: ").append(r.getDate()).append("\n");
                sb.append("Risk: ").append(r.getRiskLevel()).append("\n");
                sb.append("Open ports: ").append(r.getOpenPorts().size()).append("\n");
                for (OpenPort p : r.getOpenPorts()) {
                    sb.append("  ").append(p.getPort()).append(" ")
                            .append(p.getService()).append("\n");
                }
                runOnUiThread(() -> {
                    out.setText(sb.toString());
                    btnScan.setEnabled(true);
                });
            }).start();
        });
    }
}