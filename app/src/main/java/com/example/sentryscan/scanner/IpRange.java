package com.example.sentryscan.scanner;

import java.util.ArrayList;
import java.util.List;

public class IpRange {

    public static final int MAX_HOSTS = 50;

    // true for text like 192.168.100.1-20
    public static boolean isRange(String text) {
        return text != null
                && text.trim().matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}-\\d{1,3}");
    }

    // Turns 192.168.100.1-20 into 20 addresses.
    // Returns an empty list if the range is invalid or has more than MAX_HOSTS addresses.
    public static List<String> expand(String text) {
        List<String> hosts = new ArrayList<>();
        if (!isRange(text)) {
            return hosts;
        }
        String t = text.trim();
        int dash = t.indexOf('-');
        String start = t.substring(0, dash);
        int end = Integer.parseInt(t.substring(dash + 1));

        // each part of the address must be 0 to 255
        String[] parts = start.split("\\.");
        for (String p : parts) {
            if (Integer.parseInt(p) > 255) {
                return hosts;
            }
        }

        int lastDot = start.lastIndexOf('.');
        String prefix = start.substring(0, lastDot + 1);
        int first = Integer.parseInt(start.substring(lastDot + 1));

        if (first > end || end > 255 || (end - first + 1) > MAX_HOSTS) {
            return hosts;
        }
        for (int i = first; i <= end; i++) {
            hosts.add(prefix + i);
        }
        return hosts;
    }
}