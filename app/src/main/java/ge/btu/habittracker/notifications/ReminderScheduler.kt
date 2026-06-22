package ge.btu.habittracker.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import ge.btu.habittracker.data.model.Habit
import java.util.Calendar

/**
 * Schedules and cancels each habit's daily reminder via [AlarmManager].
 *
 * Uses inexact repeating alarms (INTERVAL_DAY) so the app does NOT need the special
 * SCHEDULE_EXACT_ALARM permission introduced in Android 12.
 */
class ReminderScheduler(private val context: Context) {

    private val alarmManager: AlarmManager
        get() = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /** Schedule if the reminder is on, otherwise make sure it's cancelled. */
    fun sync(habit: Habit) {
        if (habit.reminderEnabled) schedule(habit) else cancel(habit.id)
    }

    fun schedule(habit: Habit) {
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            nextTriggerMillis(habit.reminderHour, habit.reminderMinute),
            AlarmManager.INTERVAL_DAY,
            pendingIntent(habit.id, habit.name),
        )
    }

    fun cancel(habitId: String) {
        alarmManager.cancel(pendingIntent(habitId, ""))
    }

    /** Next occurrence of hour:minute; if it already passed today, schedule for tomorrow. */
    private fun nextTriggerMillis(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val next = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (next.timeInMillis <= now.timeInMillis) {
            next.add(Calendar.DAY_OF_YEAR, 1)
        }
        return next.timeInMillis
    }

    private fun pendingIntent(habitId: String, habitName: String): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_HABIT_ID, habitId)
            putExtra(ReminderReceiver.EXTRA_HABIT_NAME, habitName)
        }
        var flags = PendingIntent.FLAG_UPDATE_CURRENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags = flags or PendingIntent.FLAG_IMMUTABLE
        }
        // Unique request code per habit so each one has an independent alarm.
        return PendingIntent.getBroadcast(context, habitId.hashCode(), intent, flags)
    }
}
