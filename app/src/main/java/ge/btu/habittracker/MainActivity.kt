package ge.btu.habittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ge.btu.habittracker.ui.HabitTrackerApp
import ge.btu.habittracker.ui.theme.HabitTrackerTheme

/**
 * The single Activity that hosts the whole Compose UI. No XML layouts, no findViewById.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitTrackerTheme {
                HabitTrackerApp()
            }
        }
    }
}
