package com.hexacore.tayo.lock;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

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

    static private final Map<String, SortedSet<Range>> rangeLocks = new ConcurrentHashMap<>();

    @Override
    public synchronized boolean acquireRangeLock(String key, LocalDate start, LocalDate end) {
        SortedSet<Range> sortedRanges = rangeLocks.computeIfAbsent(key, k -> new TreeSet<>());

        for (Range range : sortedRanges) {
            if (end.isBefore(range.getStart())) {
                break;
            }
            if (start.isAfter(range.getEnd())) {
                continue;
            }
            return false;
        }

        sortedRanges.add(new Range(start, end));
        return true;
    }

    @Override
    public synchronized void releaseRangeLock(String key, LocalDate start, LocalDate end) {
        SortedSet<Range> ranges = rangeLocks.get(key);
        if (ranges == null) {
            return;
        }
        ranges.remove(new Range(start, end));
    }
}
