package com.example.sentryscan.view;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sentryscan.controller.ScanController;
import com.example.sentryscan.model.CveEntry;
import com.example.sentryscan.model.OpenPort;
import com.example.sentryscan.model.ScanResult;

import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    public static final String EXTRA_SCAN_ID = "scan_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScanController controller = new ScanController(this);

        // Find the scan we were asked to show
        int scanId = getIntent().getIntExtra(EXTRA_SCAN_ID, -1);
        ScanResult result = null;
        for (ScanResult r : controller.onViewHistory()) {
            if (r.getId() == scanId) {
                result = r;
                break;
            }
        }

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 96, 32, 32);

        TextView title = new TextView(this);
        title.setText("Scan Results");
        title.setTextSize(22);
        root.addView(title);

        TextView body = new TextView(this);
        body.setTextSize(16);
        body.setText(result == null
                ? "Scan not found"
                : buildReport(controller, result));

        ScrollView scroll = new ScrollView(this);
        scroll.addView(body);
        root.addView(scroll);
        setContentView(root);
    }

    // The text for the whole page
    private String buildReport(ScanController controller, ScanResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("Target: ").append(result.getTarget()).append("\n");
        sb.append("Date: ").append(result.getDate()).append("\n");
        sb.append("Overall risk: ").append(result.getRiskLevel()).append("\n");
        sb.append("Open ports: ").append(result.getOpenPorts().size()).append("\n\n");

        for (OpenPort p : result.getOpenPorts()) {
            sb.append("Port ").append(p.getPort())
                    .append(" (").append(p.getService()).append(")\n");

            String banner = p.getBanner();
            sb.append("Banner: ")
                    .append(banner == null || banner.isEmpty() ? "(none)" : banner)
                    .append("\n");

            List<CveEntry> hits = controller.onMatchBanner(banner);
            if (hits.isEmpty()) {
                sb.append("Vulnerabilities: none found in the local database\n");
            } else {
                sb.append("Vulnerabilities:\n");
                for (CveEntry c : hits) {
                    sb.append("  - ").append(c.getCveId())
                            .append(" (").append(c.getSeverity()).append("): ")
                            .append(c.getDescription()).append("\n");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}