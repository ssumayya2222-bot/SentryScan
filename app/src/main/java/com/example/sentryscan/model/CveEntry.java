package com.example.sentryscan.model;

public class CveEntry {
    private int id;
    private String cveId;
    private String service;
    private String version;
    private String severity;
    private String description;

    public CveEntry(String cveId, String service, String version,
                    String severity, String description) {
        this.cveId = cveId;
        this.service = service;
        this.version = version;
        this.severity = severity;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCveId() {
        return cveId;
    }

    public void setCveId(String cveId) {
        this.cveId = cveId;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}