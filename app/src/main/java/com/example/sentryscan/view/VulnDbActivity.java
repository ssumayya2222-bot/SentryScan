package com.example.sentryscan.view;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sentryscan.R;
import com.example.sentryscan.database.DatabaseHelper;
import com.example.sentryscan.model.CveEntry;

import java.util.ArrayList;
import java.util.List;

public class VulnDbActivity extends AppCompatActivity {
    private final String[] severities = {"Low", "Medium", "High", "Critical"};

    private EditText etCveId, etService, etVersion, etDescription;
    private Spinner spSeverity;
    private ListView lvCves;
    private DatabaseHelper db;
    private List<CveEntry> entries = new ArrayList<>();
    private CveEntry selected = null; // null means "adding a new entry"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vuln_db);

        etCveId = findViewById(R.id.etCveId);
        etService = findViewById(R.id.etService);
        etVersion = findViewById(R.id.etVersion);
        etDescription = findViewById(R.id.etDescription);
        spSeverity = findViewById(R.id.spSeverity);
        lvCves = findViewById(R.id.lvCves);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnClear = findViewById(R.id.btnClear);

        db = DatabaseHelper.getInstance(this);

        spSeverity.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, severities));

        btnSave.setOnClickListener(v -> save());
        btnDelete.setOnClickListener(v -> delete());
        btnClear.setOnClickListener(v -> clearForm());
        lvCves.setOnItemClickListener((parent, view, position, id) -> select(position));

        refreshList();
    }

    private void save() {
        String cveId = etCveId.getText().toString().trim();
        String service = etService.getText().toString().trim();
        String version = etVersion.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String severity = severities[spSeverity.getSelectedItemPosition()];

        if (cveId.isEmpty() || service.isEmpty()) {
            Toast.makeText(this, "CVE ID and Service are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selected == null) {
            db.insertCve(new CveEntry(cveId, service, version, severity, description));
            Toast.makeText(this, "Entry added", Toast.LENGTH_SHORT).show();
        } else {
            selected.setCveId(cveId);
            selected.setService(service);
            selected.setVersion(version);
            selected.setSeverity(severity);
            selected.setDescription(description);
            db.updateCve(selected);
            Toast.makeText(this, "Entry updated", Toast.LENGTH_SHORT).show();
        }
        clearForm();
        refreshList();
    }

    private void delete() {
        if (selected == null) {
            Toast.makeText(this, "Tap an entry in the list first", Toast.LENGTH_SHORT).show();
            return;
        }
        db.deleteCve(selected.getId());
        Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show();
        clearForm();
        refreshList();
    }

    private void select(int position) {
        selected = entries.get(position);
        etCveId.setText(selected.getCveId());
        etService.setText(selected.getService());
        etVersion.setText(selected.getVersion());
        etDescription.setText(selected.getDescription());
        for (int i = 0; i < severities.length; i++) {
            if (severities[i].equals(selected.getSeverity())) {
                spSeverity.setSelection(i);
            }
        }
    }

    private void clearForm() {
        selected = null;
        etCveId.setText("");
        etService.setText("");
        etVersion.setText("");
        etDescription.setText("");
        spSeverity.setSelection(0);
    }

    private void refreshList() {
        entries = db.getAllCves();
        List<String> rows = new ArrayList<>();
        for (CveEntry e : entries) {
            rows.add(e.getCveId() + " | " + e.getService() + " " + e.getVersion()
                    + " | " + e.getSeverity());
        }
        lvCves.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, rows));
    }
}