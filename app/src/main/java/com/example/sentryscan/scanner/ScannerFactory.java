package com.example.sentryscan.scanner;

import java.util.List;

public class ScannerFactory {

    // "common" mode: the fixed list of common ports
    public static Scanner getScanner(String mode) {
        return getScanner(mode, null);
    }

    // "custom" mode: the user supplies the ports
    public static Scanner getScanner(String mode, List<Integer> customPorts) {
        if ("custom".equalsIgnoreCase(mode)) {
            return new CustomPortScanner(customPorts);
        }
        return new CommonPortScanner();
    }
}