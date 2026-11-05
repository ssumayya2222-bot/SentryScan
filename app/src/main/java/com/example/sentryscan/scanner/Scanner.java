package com.example.sentryscan.scanner;

import com.example.sentryscan.model.OpenPort;
import java.util.List;

public interface Scanner {
    List<OpenPort> scan(String target);
}