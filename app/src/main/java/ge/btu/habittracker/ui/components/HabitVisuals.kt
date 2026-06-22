package ge.btu.habittracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/** A selectable habit icon, identified by a stable string key stored in the database. */
data class IconOption(val key: String, val icon: ImageVector)

val habitIcons: List<IconOption> = listOf(
    IconOption("Star", Icons.Filled.Star),
    IconOption("Favorite", Icons.Filled.Favorite),
    IconOption("FitnessCenter", Icons.Filled.FitnessCenter),
    IconOption("MenuBook", Icons.Filled.MenuBook),
    IconOption("WaterDrop", Icons.Filled.WaterDrop),
    IconOption("SelfImprovement", Icons.Filled.SelfImprovement),
    IconOption("DirectionsRun", Icons.Filled.DirectionsRun),
    IconOption("Bedtime", Icons.Filled.Bedtime),
    IconOption("Code", Icons.Filled.Code),
    IconOption("LocalCafe", Icons.Filled.LocalCafe),
)

fun iconForKey(key: String): ImageVector =
    habitIcons.firstOrNull { it.key == key }?.icon ?: Icons.Filled.Star

val habitColors: List<String> = listOf(
    "#6750A4", "#B3261E", "#1E88E5", "#2E7D32",
    "#F9A825", "#6D4C41", "#00897B", "#8E24AA",
)

/** Parses a "#RRGGBB" string into a Compose [Color], falling back to the brand purple. */
fun parseColor(hex: String): Color = runCatching {
    Color(android.graphics.Color.parseColor(hex))
}.getOrDefault(Color(0xFF6750A4))
