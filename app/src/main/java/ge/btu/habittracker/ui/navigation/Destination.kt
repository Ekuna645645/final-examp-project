package ge.btu.habittracker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.ui.graphics.vector.ImageVector

/** The three top-level destinations shown in the bottom-navigation menu. */
sealed class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    data object Today : Destination("today", "Today", Icons.Filled.CheckCircle)
    data object Habits : Destination("habits", "Habits", Icons.Filled.FormatListBulleted)
    data object Stats : Destination("stats", "Stats", Icons.Filled.BarChart)

    companion object {
        val bottomBarItems = listOf(Today, Habits, Stats)
    }
}

/** Routes that aren't part of the bottom bar (the add/edit form). */
object Routes {
    const val ADD_EDIT = "add_edit"
    const val HABIT_ID_ARG = "habitId"

    /** Builds the add/edit route; pass an id to edit, or null to create a new habit. */
    fun addEdit(habitId: String? = null): String = "$ADD_EDIT?$HABIT_ID_ARG=${habitId ?: ""}"
}
