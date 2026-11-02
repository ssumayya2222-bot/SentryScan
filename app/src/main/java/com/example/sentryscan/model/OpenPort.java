package com.example.sentryscan.model;

public class OpenPort {
    private int port;
    private String service;
    private String banner;

    public OpenPort(int port, String service, String banner) {
        this.port = port;
        this.service = service;
        this.banner = banner;
    }

    public int getPort() {
        return port;
    }

    public String getService() {
        return service;
    }

    public String getBanner() {
        return banner;
    }
}