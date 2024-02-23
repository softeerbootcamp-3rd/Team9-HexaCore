package com.hexacore.tayo.lock;

import java.time.LocalDate;

public interface RangeLockManager {

    boolean acquireRangeLock(String key);
    boolean acquireRangeLock(String key, LocalDate start, LocalDate end);

    void releaseRangeLock(String key);
    void releaseRangeLock(String key, LocalDate start, LocalDate end);
}
