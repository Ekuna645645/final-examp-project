package ge.btu.habittracker.data.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Small date helpers. Uses Calendar/SimpleDateFormat (available since API 1) instead of
 * java.time so we don't need core-library desugaring at minSdk 24.
 */
object DateUtils {

    private val keyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    /** Stable storage key for today, e.g. "2026-06-22". */
    fun todayKey(): String = keyFormat.format(Date())

    /** Storage key for the day [daysAgo] days before today. */
    fun dayKey(daysAgo: Int): String = keyFormat.format(calendarFor(daysAgo).time)

    /** Short weekday label, e.g. "Mon", for the day [daysAgo] days ago. */
    fun dayLabel(daysAgo: Int): String =
        SimpleDateFormat("EEE", Locale.getDefault()).format(calendarFor(daysAgo).time)

    /** Pretty header date, e.g. "Monday, 22 June". */
    fun prettyToday(): String =
        SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date())

    private fun calendarFor(daysAgo: Int): Calendar =
        Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -daysAgo) }

    /** Formats an hour/minute as "HH:mm". */
    fun formatTime(hour: Int, minute: Int): String =
        String.format(Locale.US, "%02d:%02d", hour, minute)
}
