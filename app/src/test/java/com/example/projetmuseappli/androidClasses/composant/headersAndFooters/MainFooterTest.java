package com.example.projetmuseappli.androidClasses.composant.headersAndFooters;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MainFooterTest {

    @Test
    public void testFormatTimeShortDuration() {
        // 1 min 30s / 3 min 45s
        long currentMs = 90_000L;
        long totalMs = 225_000L;
        String formatted = MainFooter.formatTime(currentMs, totalMs);
        assertEquals("01:30 / 03:45", formatted);
    }

    @Test
    public void testFormatTimeLongDurationMoreThan10Hours() {
        // 1 hour 23 min 45 sec / 10 hours 15 min 30 sec
        long currentMs = (3600 + 23 * 60 + 45) * 1000L;
        long totalMs = (10 * 3600 + 15 * 60 + 30) * 1000L;
        String formatted = MainFooter.formatTime(currentMs, totalMs);
        assertEquals("01:23:45 / 10:15:30", formatted);
    }

    @Test
    public void testFormatTimeZeroAndNegative() {
        String formatted = MainFooter.formatTime(-100L, 0L);
        assertEquals("00:00 / 00:00", formatted);
    }
}
