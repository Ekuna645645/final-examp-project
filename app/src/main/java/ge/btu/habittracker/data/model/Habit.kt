package ge.btu.habittracker.data.model

import com.google.firebase.database.Exclude

/**
 * A habit the user wants to build. Persisted in Firebase Realtime Database under
 * `/habits/{id}`. Every field has a default value so Firebase can use the required
 * no-argument constructor when deserializing snapshots.
 *
 * [id] is excluded from serialization because it IS the Realtime Database key — we
 * read it back from the snapshot key instead of duplicating it inside the node.
 */
data class Habit(
    @get:Exclude val id: String = "",
    val name: String = "",
    val description: String = "",
    val colorHex: String = "#6750A4",
    val iconKey: String = "Star",
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 9,
    val reminderMinute: Int = 0,
    val createdAt: Long = 0L,
    /** Map of "yyyy-MM-dd" -> true for each day the habit was completed. */
    val completions: Map<String, Boolean> = emptyMap(),
)
