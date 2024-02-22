package com.hexacore.tayo.lock;

public class LockKeyGenerator {
    static public String generateCarDateRangeLockKey(Long carId) {
        return "car(" + carId + "):dateRangeLock";
    }
}
