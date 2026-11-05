package com.example.sentryscan.view;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sentryscan.controller.ScanController;
import com.example.sentryscan.model.OpenPort;
import com.example.sentryscan.model.ScanResult;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ScanController controller;
    private ArrayAdapter<String> adapter;
    private TextView hint;
    private List<ScanResult> scans = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = new ScanController(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 96, 32, 32);

        TextView title = new TextView(this);
        title.setText("Scan History");
        title.setTextSize(22);
        root.addView(title);

        hint = new TextView(this);
        root.addView(hint);

        ListView listView = new ListView(this);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, new ArrayList<String>());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) ->
                showScan(scans.get(position)));
        root.addView(listView);

        setContentView(root);
        refresh();
    }

    // Reload the list from the database
    private void refresh() {
        scans = controller.onViewHistory();
        adapter.clear();
        for (ScanResult r : scans) {
            adapter.add(r.getDate() + " | " + r.getTarget() + " | "
                    + r.getRiskLevel() + " | " + r.getOpenPorts().size() + " ports");
        }
        adapter.notifyDataSetChanged();
        hint.setText(scans.isEmpty()
                ? "No scans saved yet"
                : "Tap a scan to see details or delete it");
    }

    private void showScan(ScanResult r) {
        StringBuilder sb = new StringBuilder();
        sb.append("Date: ").append(r.getDate()).append("\n");
        sb.append("Risk: ").append(r.getRiskLevel()).append("\n");
        sb.append("Open ports: ").append(r.getOpenPorts().size()).append("\n");
        for (OpenPort p : r.getOpenPorts()) {
            sb.append("  ").append(p.getPort()).append(" ")
                    .append(p.getService()).append("\n");
        }
        new AlertDialog.Builder(this)
                .setTitle("Scan of " + r.getTarget())
                .setMessage(sb.toString())
                .setPositiveButton("Close", null)
                .setNegativeButton("Delete", (dialog, which) -> {
                    controller.onDeleteScan(r.getId());
                    refresh();
                })
                .show();
    }
}