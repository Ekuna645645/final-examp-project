package ge.btu.habittracker.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Receives a habit's daily alarm and posts the reminder notification. */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getStringExtra(EXTRA_HABIT_ID) ?: return
        val habitName = intent.getStringExtra(EXTRA_HABIT_NAME) ?: "your habit"
        NotificationHelper.showReminder(context, habitId, habitName)
    }

    companion object {
        const val EXTRA_HABIT_ID = "extra_habit_id"
        const val EXTRA_HABIT_NAME = "extra_habit_name"
    }
}
