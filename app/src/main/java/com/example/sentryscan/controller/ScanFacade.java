package com.example.sentryscan.controller;

import com.example.sentryscan.scanner.HostListener;
import java.net.InetAddress;

import com.example.sentryscan.scanner.ProgressListener;
import android.content.Context;

import com.example.sentryscan.database.DatabaseHelper;
import com.example.sentryscan.model.CveEntry;
import com.example.sentryscan.model.OpenPort;
import com.example.sentryscan.model.ScanResult;
import com.example.sentryscan.report.ReportGenerator;
import com.example.sentryscan.scanner.Scanner;
import com.example.sentryscan.scanner.ScannerFactory;
import com.example.sentryscan.scanner.VulnerabilityMatcher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import java.io.File;
import java.io.IOException;
import com.example.sentryscan.report.ReportGenerator;

public class ScanFacade {

    private final DatabaseHelper db;
    private final VulnerabilityMatcher matcher;
    private final ReportGenerator reportGenerator;

    public ScanFacade(Context context) {
        this.db = DatabaseHelper.getInstance(context);
        this.matcher = new VulnerabilityMatcher(context);
        this.reportGenerator = new ReportGenerator(context);
    }

    // One call does everything: scan, match, rate the risk, save
    public ScanResult runScan(String target, String mode, List<Integer> customPorts,
                              ProgressListener listener) {
        Scanner scanner = ScannerFactory.getScanner(mode, customPorts);
        List<OpenPort> ports = scanner.scan(target, listener);

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
    public void deleteScan(int id) {
        db.deleteScan(id);
    }
    public List<CveEntry> matchBanner(String banner) {
        return matcher.match(banner);
    }
    public File exportReport(ScanResult result) {
        try {
            return reportGenerator.generate(result);
        } catch (IOException e) {
            return null;
        }
    }
    public ScanResult runScan(String target, String mode, List<Integer> customPorts) {
        return runScan(target, mode, customPorts, null);
    }
    // Scans every live address in the list; each live host is saved as its own scan
    public List<ScanResult> runRangeScan(List<String> hosts, String mode,
                                         List<Integer> customPorts,
                                         HostListener hostListener,
                                         ProgressListener portListener) {
        List<ScanResult> results = new ArrayList<>();
        for (int i = 0; i < hosts.size(); i++) {
            String host = hosts.get(i);
            if (hostListener != null) {
                hostListener.onHost(i + 1, hosts.size(), host);
            }
            if (!isHostUp(host)) {
                continue;   // nobody home, skip it
            }
            results.add(runScan(host, mode, customPorts, portListener));
        }
        return results;
    }

    // Does this address answer within half a second?
    private boolean isHostUp(String host) {
        try {
            return InetAddress.getByName(host).isReachable(500);
        } catch (Exception e) {
            return false;
        }
    }
}