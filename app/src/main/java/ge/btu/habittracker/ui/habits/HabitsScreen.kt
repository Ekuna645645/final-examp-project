package ge.btu.habittracker.ui.habits

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.PlaylistAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ge.btu.habittracker.data.model.Habit
import ge.btu.habittracker.data.model.currentStreak
import ge.btu.habittracker.data.util.DateUtils
import ge.btu.habittracker.ui.HabitUiState
import ge.btu.habittracker.ui.components.EmptyState
import ge.btu.habittracker.ui.components.FirebaseWarningBanner
import ge.btu.habittracker.ui.components.HabitIconBadge
import ge.btu.habittracker.ui.components.ScreenHeader

/** "Habits" tab: the full list with edit-on-tap and delete. The "+" FAB lives in the app shell. */
@Composable
fun HabitsScreen(
    uiState: HabitUiState,
    onEdit: (Habit) -> Unit,
    onDelete: (Habit) -> Unit,
) {
    var habitToDelete by remember { mutableStateOf<Habit?>(null) }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Habits", "${uiState.habits.size} total")

        if (!uiState.isFirebaseConfigured) FirebaseWarningBanner()

        if (uiState.habits.isEmpty()) {
            EmptyState(
                title = "No habits yet",
                message = "Tap the + button to create your first habit.",
                icon = Icons.Outlined.PlaylistAdd,
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(uiState.habits, key = { it.id }) { habit ->
                    HabitRow(
                        habit = habit,
                        onClick = { onEdit(habit) },
                        onDelete = { habitToDelete = habit },
                    )
                }
            }
        }
    }

    habitToDelete?.let { habit ->
        AlertDialog(
            onDismissRequest = { habitToDelete = null },
            title = { Text("Delete habit?") },
            text = { Text("\"${habit.name}\" and its history will be removed.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(habit)
                    habitToDelete = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { habitToDelete = null }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun HabitRow(habit: Habit, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HabitIconBadge(habit)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(habit.name, style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (habit.reminderEnabled) Icons.Filled.Notifications else Icons.Filled.NotificationsOff,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.width(4.dp))
                    val reminder = if (habit.reminderEnabled) {
                        DateUtils.formatTime(habit.reminderHour, habit.reminderMinute)
                    } else {
                        "No reminder"
                    }
                    Text(
                        "$reminder  ·  streak ${habit.currentStreak()}d",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.DeleteOutline, contentDescription = "Delete habit")
            }
        }
    }
}
