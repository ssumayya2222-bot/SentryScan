package com.example.sentryscan.controller;

import com.example.sentryscan.scanner.ProgressListener;
import android.content.Context;

import com.example.sentryscan.model.CveEntry;
import com.example.sentryscan.model.ScanResult;

import java.io.File;
import java.util.List;

public class ScanController {

    private final ScanFacade facade;

    public ScanController(Context context) {
        this.facade = new ScanFacade(context);
    }

    // Returns null if the target box is empty
    public ScanResult onStartScan(String target, String mode, List<Integer> customPorts) {
        if (target == null || target.trim().isEmpty()) {
            return null;
        }
        return facade.runScan(target.trim(), mode, customPorts);
    }

    public List<ScanResult> onViewHistory() {
        return facade.getHistory();
    }
    public void onDeleteScan(int id) {
        facade.deleteScan(id);
    }
    public List<CveEntry> onMatchBanner(String banner) {
        return facade.matchBanner(banner);
    }
    public File onExportReport(ScanResult result) {
        return facade.exportReport(result);
    }

    public ScanResult onStartScan(String target, String mode, List<Integer> customPorts,
                                  ProgressListener listener) {
        if (target == null || target.trim().isEmpty()) {
            return null;
        }
        return facade.runScan(target.trim(), mode, customPorts, listener);
    }
}