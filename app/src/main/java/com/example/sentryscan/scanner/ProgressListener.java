package com.example.sentryscan.scanner;

public interface ProgressListener {
    void onProgress(int done, int total, int port);
}