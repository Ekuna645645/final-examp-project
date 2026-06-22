package ge.btu.habittracker.data.model

import ge.btu.habittracker.data.util.DateUtils

/** True if the habit was completed today. */
fun Habit.isCompletedToday(): Boolean = completions[DateUtils.todayKey()] == true

/**
 * Current streak: the number of consecutive completed days ending today (or yesterday,
 * if today hasn't been marked done yet).
 */
fun Habit.currentStreak(): Int {
    var index = if (completions[DateUtils.todayKey()] == true) 0 else 1
    var streak = 0
    while (completions[DateUtils.dayKey(index)] == true) {
        streak++
        index++
    }
    return streak
}

/** Total number of days this habit was ever completed. */
fun Habit.totalCompletions(): Int = completions.count { it.value }

/** Fraction (0f..1f) of the last [days] days that were completed. */
fun Habit.completionRate(days: Int): Float {
    if (days <= 0) return 0f
    val done = (0 until days).count { completions[DateUtils.dayKey(it)] == true }
    return done.toFloat() / days
}
