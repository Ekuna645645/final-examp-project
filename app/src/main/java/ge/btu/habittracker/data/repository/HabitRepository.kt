package ge.btu.habittracker.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import ge.btu.habittracker.data.model.Habit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Single source of truth for habit data, backed by Firebase Realtime Database.
 * All habits live under the top-level `habits` node.
 *
 * If Firebase isn't configured yet (no google-services.json), [habitsRef] is null and
 * every operation degrades gracefully so the UI still runs.
 */
class HabitRepository {

    private val habitsRef: DatabaseReference? = runCatching {
        FirebaseDatabase.getInstance().getReference("habits")
    }.getOrNull()

    val isConfigured: Boolean get() = habitsRef != null

    /** Emits the full habit list and re-emits whenever the data changes in the cloud. */
    fun observeHabits(): Flow<List<Habit>> = callbackFlow {
        val ref = habitsRef
        if (ref == null) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.toHabits())
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    /** One-shot read (used by the boot receiver to reschedule reminders). */
    suspend fun getHabitsOnce(): List<Habit> {
        val ref = habitsRef ?: return emptyList()
        return ref.get().await().toHabits()
    }

    suspend fun addHabit(habit: Habit): String {
        val ref = habitsRef ?: error("Firebase is not configured")
        val key = ref.push().key ?: error("Could not generate a habit id")
        ref.child(key).setValue(habit).await()
        return key
    }

    suspend fun updateHabit(habit: Habit) {
        val ref = habitsRef ?: error("Firebase is not configured")
        ref.child(habit.id).setValue(habit).await()
    }

    suspend fun deleteHabit(habitId: String) {
        habitsRef?.child(habitId)?.removeValue()?.await()
    }

    /** Marks (or unmarks) a habit as completed on a given day. */
    suspend fun setCompletion(habitId: String, dateKey: String, done: Boolean) {
        val ref = habitsRef ?: return
        // Writing null removes the key, which keeps the map clean for "not done".
        ref.child(habitId).child("completions").child(dateKey)
            .setValue(if (done) true else null)
            .await()
    }

    private fun DataSnapshot.toHabits(): List<Habit> =
        children.mapNotNull { child ->
            val key = child.key ?: return@mapNotNull null
            child.getValue<Habit>()?.copy(id = key)
        }.sortedBy { it.createdAt }
}
