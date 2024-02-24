package com.hexacore.tayo.lock;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

public class InMemoryRangeLockManager implements RangeLockManager {

    @Getter
    @AllArgsConstructor
    static class Range implements Comparable<Range> {
        private LocalDate start;
        private LocalDate end;

        @Override
        public int compareTo(Range o) {
            int cmp = this.start.compareTo(o.start);
            return cmp == 0 ? this.end.compareTo(o.end) : cmp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Range range = (Range) o;
            return start.equals(range.start) && end.equals(range.end);
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, end);
        }
    }

    private static final Map<String, TreeSet<Range>> rangeLocks = new HashMap<>();

    @Override
    public boolean acquireFullRangeLock(String key) {
        return acquireFullRangeLock(key, LocalDate.MIN, LocalDate.MAX);
    }

    @Override
    public synchronized boolean acquireFullRangeLock(String key, LocalDate start, LocalDate end) {
        TreeSet<Range> sortedRanges = rangeLocks.computeIfAbsent(key, k -> new TreeSet<>());

        Range newRange = new Range(start, end);
        if (sortedRanges.contains(newRange)) {
            return false;
        }

        Range higher = sortedRanges.higher(newRange);
        if (!(higher == null || higher.start.isAfter(end))) {
            return false;
        }

        Range lower = sortedRanges.lower(newRange);
        if (!(lower == null || lower.end.isBefore(start))) {
            return false;
        }

        sortedRanges.add(newRange);
        return true;
    }

    @Override
    public void releaseFullRangeLock(String key) {
        releaseFullRangeLock(key, LocalDate.MIN, LocalDate.MAX);
    }

    @Override
    public synchronized void releaseFullRangeLock(String key, LocalDate start, LocalDate end) {
        TreeSet<Range> ranges = rangeLocks.get(key);
        if (ranges == null) {
            return;
        }
        ranges.remove(new Range(start, end));
    }
}
