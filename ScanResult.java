package com.example.sentryscan.model;

import java.util.ArrayList;
import java.util.List;

public class ScanResult {
    private int id;
    private String target;
    private String date;
    private String riskLevel;
    private List<OpenPort> openPorts;

    public ScanResult(String target, String date) {
        this.target = target;
        this.date = date;
        this.riskLevel = "Low";
        this.openPorts = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTarget() {
        return target;
    }

    public String getDate() {
        return date;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public List<OpenPort> getOpenPorts() {
        return openPorts;
    }

    public void addOpenPort(OpenPort port) {
        openPorts.add(port);
    }
}