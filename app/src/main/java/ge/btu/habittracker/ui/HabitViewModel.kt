package ge.btu.habittracker.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ge.btu.habittracker.data.model.Habit
import ge.btu.habittracker.data.repository.HabitRepository
import ge.btu.habittracker.data.util.DateUtils
import ge.btu.habittracker.notifications.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Immutable UI state exposed to the Compose screens. */
data class HabitUiState(
    val habits: List<Habit> = emptyList(),
    val isFirebaseConfigured: Boolean = true,
)

/**
 * The single shared ViewModel for the habit screens (MVVM). It exposes the habit list as
 * a [StateFlow] streamed from Firebase, and performs all mutations through the repository,
 * keeping reminders in sync via [ReminderScheduler].
 *
 * It's an [AndroidViewModel] only so it can hand the application [android.content.Context]
 * to the alarm scheduler — no Activity context is ever held.
 */
class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HabitRepository()
    private val scheduler = ReminderScheduler(application)

    val uiState: StateFlow<HabitUiState> =
        repository.observeHabits()
            .map { HabitUiState(habits = it, isFirebaseConfigured = repository.isConfigured) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HabitUiState(isFirebaseConfigured = repository.isConfigured),
            )

    /** Toggle whether the habit is done for today. */
    fun toggleToday(habit: Habit) {
        val todayKey = DateUtils.todayKey()
        val markDone = habit.completions[todayKey] != true
        viewModelScope.launch {
            // runCatching keeps a network/Firebase failure from crashing the app.
            runCatching { repository.setCompletion(habit.id, todayKey, markDone) }
                .onFailure { Log.e(TAG, "toggleToday failed", it) }
        }
    }

    /** Create a new habit (blank id) or update an existing one, then sync its reminder. */
    fun saveHabit(habit: Habit) {
        viewModelScope.launch {
            runCatching {
                if (habit.id.isBlank()) {
                    val newId = repository.addHabit(habit.copy(createdAt = System.currentTimeMillis()))
                    scheduler.sync(habit.copy(id = newId))
                } else {
                    repository.updateHabit(habit)
                    scheduler.sync(habit)
                }
            }.onFailure { Log.e(TAG, "saveHabit failed", it) }
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            runCatching {
                repository.deleteHabit(habit.id)
                scheduler.cancel(habit.id)
            }.onFailure { Log.e(TAG, "deleteHabit failed", it) }
        }
    }

    private companion object {
        const val TAG = "HabitViewModel"
    }
}
