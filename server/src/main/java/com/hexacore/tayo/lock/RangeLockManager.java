package com.hexacore.tayo.lock;

import java.time.LocalDate;

public interface RangeLockManager {

    boolean acquireFullRangeLock(String key);
    boolean acquireRangeLock(String key, LocalDate start, LocalDate end);

    void releaseFullRangeLock(String key);
    void releaseRangeLock(String key, LocalDate start, LocalDate end);
}
