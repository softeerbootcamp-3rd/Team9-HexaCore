package com.hexacore.tayo.lock;

import java.time.LocalDate;

public interface RangeLockManager {

    boolean acquireFullRangeLock(String key);
    boolean acquireFullRangeLock(String key, LocalDate start, LocalDate end);

    void releaseFullRangeLock(String key);
    void releaseFullRangeLock(String key, LocalDate start, LocalDate end);
}
