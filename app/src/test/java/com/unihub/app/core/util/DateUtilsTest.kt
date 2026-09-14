package com.unihub.app.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class DateUtilsTest {

    @Test
    fun `calculateNextOccurrence for future event returns start time`() {
        val future = LocalDateTime.now().plusDays(1)
        val startAt = DateUtils.formatDateTime(future)
        
        val next = DateUtils.calculateNextOccurrence(
            startAt = startAt,
            frequency = "DAILY",
            interval = 1,
            endDate = "2030-01-01",
            recurrenceDays = emptyList()
        )
        
        assertNotNull(next)
        assertEquals(future.withNano(0), next?.withNano(0))
    }

    @Test
    fun `calculateNextOccurrence for past daily event returns next occurrence`() {
        val start = LocalDateTime.now().minusDays(5).withHour(10).withMinute(0)
        val startAt = DateUtils.formatDateTime(start)
        
        val next = DateUtils.calculateNextOccurrence(
            startAt = startAt,
            frequency = "DAILY",
            interval = 1,
            endDate = "2030-01-01",
            recurrenceDays = emptyList()
        )
        
        assertNotNull(next)
        assertTrue(next!!.isAfter(LocalDateTime.now()))
        assertEquals(10, next.hour)
    }

    @Test
    fun `calculateNextOccurrence for weekly event returns correct day`() {
        // Start 2 weeks ago on a Monday
        val start = LocalDateTime.now().minusWeeks(2).with(java.time.DayOfWeek.MONDAY).withHour(10).withMinute(0)
        val startAt = DateUtils.formatDateTime(start)
        
        // Recurrence on Wednesday and Friday
        val next = DateUtils.calculateNextOccurrence(
            startAt = startAt,
            frequency = "WEEKLY",
            interval = 1,
            endDate = "2030-01-01",
            recurrenceDays = listOf(3, 5) // Wednesday, Friday
        )
        
        assertNotNull(next)
        assertTrue(next!!.isAfter(LocalDateTime.now()))
        assertTrue(next.dayOfWeek == java.time.DayOfWeek.WEDNESDAY || next.dayOfWeek == java.time.DayOfWeek.FRIDAY)
    }
}
