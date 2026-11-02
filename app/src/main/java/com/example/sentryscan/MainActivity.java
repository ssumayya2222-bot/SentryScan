package com.example.sentryscan;

import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sentryscan.database.DatabaseHelper;
import com.example.sentryscan.model.CveEntry;
import com.example.sentryscan.model.OpenPort;
import com.example.sentryscan.model.ScanResult;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView out = new TextView(this);
        out.setPadding(32, 96, 32, 32);
        out.setTextSize(16);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(out);
        setContentView(scroll);

        // Singleton check: two requests must give the same object
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        DatabaseHelper again = DatabaseHelper.getInstance(this);
        out.append("Singleton check, same object? " + (db == again) + "\n\n");

        // save a scan
        ScanResult r = new ScanResult("192.168.1.10", "2026-11-01");
        r.addOpenPort(new OpenPort(21, "FTP", "220 vsFTPd 2.3.4"));
        r.addOpenPort(new OpenPort(80, "HTTP", "Apache/2.4.49"));
        r.setRiskLevel("High");
        long id = db.saveScan(r);
        out.append("Saved scan with id " + id + "\n\n");

        // read the scans back
        List<ScanResult> scans = db.getAllScans();
        out.append("Scans in the database: " + scans.size() + "\n");
        for (ScanResult s : scans) {
            out.append("- " + s.getTarget() + " | risk " + s.getRiskLevel()
                    + " | " + s.getOpenPorts().size() + " open ports\n");
        }

        // read the sample CVE rows
        out.append("\nCVE entries:\n");
        for (CveEntry e : db.getAllCves()) {
            out.append("- " + e.getCveId() + " " + e.getService() + " "
                    + e.getVersion() + " (" + e.getSeverity() + ")\n");
        }
    }
}