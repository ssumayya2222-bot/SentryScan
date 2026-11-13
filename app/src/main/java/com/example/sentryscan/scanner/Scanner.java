package com.example.sentryscan.scanner;

import com.example.sentryscan.model.OpenPort;
import java.util.List;

public interface Scanner {
    List<OpenPort> scan(String target);

    // Same scan, but calls the listener after each port. The default does no reporting.
    default List<OpenPort> scan(String target, ProgressListener listener) {
        return scan(target);
    }
}