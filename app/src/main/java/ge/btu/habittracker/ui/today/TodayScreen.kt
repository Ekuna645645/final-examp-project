package ge.btu.habittracker.ui.today

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ge.btu.habittracker.data.model.Habit
import ge.btu.habittracker.data.util.DateUtils
import ge.btu.habittracker.ui.HabitUiState
import ge.btu.habittracker.ui.components.EmptyState
import ge.btu.habittracker.ui.components.FirebaseWarningBanner
import ge.btu.habittracker.ui.components.HabitIconBadge

/** "Today" tab: check off the habits you've completed today and see your progress. */
@Composable
fun TodayScreen(uiState: HabitUiState, onToggle: (Habit) -> Unit) {
    val todayKey = DateUtils.todayKey()
    val doneCount = uiState.habits.count { it.completions[todayKey] == true }

    Column(Modifier.fillMaxSize()) {
        ge.btu.habittracker.ui.components.ScreenHeader("Today", DateUtils.prettyToday())

        if (!uiState.isFirebaseConfigured) FirebaseWarningBanner()

        if (uiState.habits.isNotEmpty()) {
            ProgressCard(done = doneCount, total = uiState.habits.size)
        }

        if (uiState.habits.isEmpty()) {
            EmptyState(
                title = "Nothing to do yet",
                message = "Add habits from the Habits tab and they'll show up here each day.",
                icon = Icons.Outlined.TaskAlt,
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(uiState.habits, key = { it.id }) { habit ->
                    TodayHabitCard(
                        habit = habit,
                        done = habit.completions[todayKey] == true,
                        onToggle = { onToggle(habit) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressCard(done: Int, total: Int) {
    val progress = if (total == 0) 0f else done.toFloat() / total
    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("$done of $total completed", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun TodayHabitCard(habit: Habit, done: Boolean, onToggle: () -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HabitIconBadge(habit)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(habit.name, style = MaterialTheme.typography.titleMedium)
                if (habit.description.isNotBlank()) {
                    Text(
                        habit.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Checkbox(checked = done, onCheckedChange = { onToggle() })
        }
    }
}
