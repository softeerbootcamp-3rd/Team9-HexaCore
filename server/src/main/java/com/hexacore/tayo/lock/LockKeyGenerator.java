package com.hexacore.tayo.lock;

public class LockKeyGenerator {
    static public String generateReservationLockKey(Long carId) {
        return "carId " + carId + ":reservation";
    }
}
