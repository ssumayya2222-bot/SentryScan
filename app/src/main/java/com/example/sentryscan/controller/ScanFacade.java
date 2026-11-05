package com.example.sentryscan.controller;

import android.content.Context;

import com.example.sentryscan.database.DatabaseHelper;
import com.example.sentryscan.model.CveEntry;
import com.example.sentryscan.model.OpenPort;
import com.example.sentryscan.model.ScanResult;
import com.example.sentryscan.scanner.Scanner;
import com.example.sentryscan.scanner.ScannerFactory;
import com.example.sentryscan.scanner.VulnerabilityMatcher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScanFacade {

    private final DatabaseHelper db;
    private final VulnerabilityMatcher matcher;

    public ScanFacade(Context context) {
        this.db = DatabaseHelper.getInstance(context);
        this.matcher = new VulnerabilityMatcher(context);
    }

    // One call does everything: scan, match, rate the risk, save
    public ScanResult runScan(String target, String mode, List<Integer> customPorts) {
        Scanner scanner = ScannerFactory.getScanner(mode, customPorts);
        List<OpenPort> ports = scanner.scan(target);

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(new Date());
        ScanResult result = new ScanResult(target, date);

        List<CveEntry> allMatches = new ArrayList<>();
        for (OpenPort p : ports) {
            result.addOpenPort(p);
            allMatches.addAll(matcher.match(p.getBanner()));
        }
        result.setRiskLevel(matcher.riskLevel(allMatches));

        db.saveScan(result);
        return result;
    }

    public List<ScanResult> getHistory() {
        return db.getAllScans();
    }
}