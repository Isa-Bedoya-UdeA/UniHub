package com.unihub.app.core.util

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

object DateUtils {
    private val localFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val dateFormatter = DateTimeFormatter.ISO_DATE
    
    fun getCurrentTime(): LocalDateTime {
        return LocalDateTime.now(ZoneId.systemDefault())
    }

    fun parseDateTime(dateTimeStr: String): LocalDateTime {
        var normalized = dateTimeStr.trim()
        
        normalized = normalized.replace("Z", "")
        
        if (normalized.contains("+") && normalized.lastIndexOf("+") > normalized.lastIndexOf("T")) {
            normalized = normalized.substring(0, normalized.lastIndexOf("+"))
        }
        
        val lastDash = normalized.lastIndexOf("-")
        val lastT = normalized.lastIndexOf("T")
        if (lastDash > lastT && lastDash > 10) {
            normalized = normalized.substring(0, lastDash)
        }
        
        if (!normalized.contains("T")) {
            normalized += "T00:00:00"
        } else {
            val timePart = normalized.split("T")[1]
            val colonCount = timePart.count { it == ':' }
            if (colonCount == 1) {
                normalized += ":00"
            } else if (colonCount == 0) {
                normalized += ":00:00"
            }
        }

        return try {
            LocalDateTime.parse(normalized, localFormatter)
        } catch (e: Exception) {
            LocalDateTime.now(ZoneId.systemDefault())
        }
    }

    fun formatDateTime(dateTime: LocalDateTime): String {
        return dateTime.format(localFormatter)
    }

    fun calculateNextOccurrence(
        startAt: String,
        frequency: String,
        interval: Int,
        endDate: String,
        recurrenceDays: List<Int>
    ): LocalDateTime? {
        val now = getCurrentTime()
        val startDateTime = parseDateTime(startAt)
        
        val endLocalDate = try {
            LocalDate.parse(endDate, dateFormatter)
        } catch (e: Exception) {
            LocalDate.MAX
        }
        
        val eventTime = startDateTime.toLocalTime()

        if (now.isBefore(startDateTime)) return startDateTime

        val safeInterval = if (interval < 1) 1 else interval
        
        if (frequency == "DAILY") {
            val daysBetween = ChronoUnit.DAYS.between(startDateTime.toLocalDate(), now.toLocalDate())
            val intervalsPassed = if (daysBetween <= 0) 0 else (daysBetween + safeInterval - 1) / safeInterval
            
            var nextDate = startDateTime.toLocalDate().plusDays(intervalsPassed * safeInterval.toLong())
            var candidate = LocalDateTime.of(nextDate, eventTime)
            
            if (!candidate.isAfter(now)) {
                nextDate = nextDate.plusDays(safeInterval.toLong())
                candidate = LocalDateTime.of(nextDate, eventTime)
            }
            
            return if (nextDate.isAfter(endLocalDate)) null else candidate
            
        } else if (frequency == "WEEKLY") {
            val daysOfWeek = recurrenceDays.map { DayOfWeek.of(it) }.sorted()
            if (daysOfWeek.isEmpty()) return null
            
            val weeksBetween = ChronoUnit.WEEKS.between(
                startDateTime.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                now.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            )
            val intervalsPassed = if (weeksBetween <= 0) 0 else (weeksBetween + safeInterval - 1) / safeInterval
            
            var currentIntervalWeekStart = startDateTime.toLocalDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .plusWeeks(intervalsPassed * safeInterval.toLong())
            
            repeat(10) {
                for (day in daysOfWeek) {
                    val candidateDate = currentIntervalWeekStart.with(TemporalAdjusters.nextOrSame(day))
                    val candidateDateTime = LocalDateTime.of(candidateDate, eventTime)
                    
                    if (candidateDateTime.isAfter(now)) {
                        return if (candidateDate.isAfter(endLocalDate)) null else candidateDateTime
                    }
                }
                currentIntervalWeekStart = currentIntervalWeekStart.plusWeeks(safeInterval.toLong())
            }
        }
        
        return null
    }
}
