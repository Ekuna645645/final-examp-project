package ge.btu.habittracker

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import ge.btu.habittracker.notifications.NotificationHelper

/**
 * Application entry point. Enables Realtime Database offline persistence and creates
 * the notification channel used by habit reminders.
 */
class HabitApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Must be called before the database is used anywhere else. Wrapped in runCatching
        // so the app still launches if Firebase isn't configured yet.
        runCatching { FirebaseDatabase.getInstance().setPersistenceEnabled(true) }
        NotificationHelper.ensureChannel(this)
    }
}
