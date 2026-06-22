package ge.btu.habittracker.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ge.btu.habittracker.data.model.Habit
import ge.btu.habittracker.data.model.currentStreak
import ge.btu.habittracker.data.model.isCompletedToday
import ge.btu.habittracker.data.model.totalCompletions
import ge.btu.habittracker.data.util.DateUtils
import ge.btu.habittracker.ui.HabitUiState
import ge.btu.habittracker.ui.components.EmptyState
import ge.btu.habittracker.ui.components.FirebaseWarningBanner
import ge.btu.habittracker.ui.components.HabitIconBadge
import ge.btu.habittracker.ui.components.ScreenHeader
import ge.btu.habittracker.ui.components.parseColor

/** "Stats" tab: an overall summary plus a per-habit last-7-days strip and streaks. */
@Composable
fun StatsScreen(uiState: HabitUiState) {
    val habits = uiState.habits

    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Stats", "Your progress at a glance")

        if (!uiState.isFirebaseConfigured) FirebaseWarningBanner()

        if (habits.isEmpty()) {
            EmptyState(
                title = "No stats yet",
                message = "Once you start tracking habits, your streaks show up here.",
                icon = Icons.Outlined.QueryStats,
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { OverallSummaryCard(habits) }
                items(habits, key = { it.id }) { habit -> HabitStatCard(habit) }
            }
        }
    }
}

@Composable
private fun OverallSummaryCard(habits: List<Habit>) {
    val doneToday = habits.count { it.isCompletedToday() }
    val bestStreak = habits.maxOfOrNull { it.currentStreak() } ?: 0
    val totalDone = habits.sumOf { it.totalCompletions() }

    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            StatNumber("$doneToday/${habits.size}", "Done today")
            StatNumber("$bestStreak", "Best streak")
            StatNumber("$totalDone", "Total check-ins")
        }
    }
}

@Composable
private fun StatNumber(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun HabitStatCard(habit: Habit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                HabitIconBadge(habit, size = 36.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(habit.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Current streak: ${habit.currentStreak()} day(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            WeeklyStrip(habit)
        }
    }
}

/** Simple "chart": one square per day for the last 7 days, filled when completed. */
@Composable
private fun WeeklyStrip(habit: Habit) {
    val color = parseColor(habit.colorHex)
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        for (daysAgo in 6 downTo 0) {
            val done = habit.completions[DateUtils.dayKey(daysAgo)] == true
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (done) color else color.copy(alpha = 0.15f)),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    DateUtils.dayLabel(daysAgo),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
