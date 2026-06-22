package ge.btu.habittracker.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ge.btu.habittracker.data.repository.HabitRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Alarms don't survive a reboot, so after BOOT_COMPLETED we read the habits once and
 * re-schedule every reminder.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val scheduler = ReminderScheduler(appContext)
                HabitRepository().getHabitsOnce().forEach { scheduler.sync(it) }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
