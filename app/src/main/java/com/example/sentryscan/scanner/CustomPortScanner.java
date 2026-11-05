package com.example.sentryscan.scanner;

import com.example.sentryscan.model.OpenPort;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class CustomPortScanner implements Scanner {

    private static final int CONNECT_TIMEOUT_MS = 800;
    private static final int READ_TIMEOUT_MS = 1500;

    private final List<Integer> ports;

    public CustomPortScanner(List<Integer> ports) {
        this.ports = ports;
    }

    @Override
    public List<OpenPort> scan(String target) {
        List<OpenPort> found = new ArrayList<>();
        for (int port : ports) {
            if (isOpen(target, port)) {
                found.add(new OpenPort(port, serviceName(port), grabBanner(target, port)));
            }
        }
        return found;
    }

    private boolean isOpen(String ip, int port) {
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(ip, port), CONNECT_TIMEOUT_MS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String grabBanner(String ip, int port) {
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(ip, port), CONNECT_TIMEOUT_MS);
            s.setSoTimeout(READ_TIMEOUT_MS);
            if (port == 80 || port == 8080) {
                s.getOutputStream().write("HEAD / HTTP/1.0\r\n\r\n".getBytes());
                s.getOutputStream().flush();
            }
            InputStream in = s.getInputStream();
            byte[] buffer = new byte[256];
            int n = in.read(buffer);
            if (n > 0) {
                return new String(buffer, 0, n).replaceAll("[\\r\\n]+", " ").trim();
            }
        } catch (Exception e) {
            // no banner received
        }
        return "";
    }

    private String serviceName(int port) {
        switch (port) {
            case 21: return "FTP";
            case 22: return "SSH";
            case 23: return "Telnet";
            case 25: return "SMTP";
            case 80: return "HTTP";
            case 110: return "POP3";
            case 139: return "NetBIOS";
            case 143: return "IMAP";
            case 443: return "HTTPS";
            case 445: return "SMB";
            case 3306: return "MySQL";
            case 3389: return "RDP";
            case 8080: return "HTTP-Alt";
            default: return "Unknown";
        }
    }
}