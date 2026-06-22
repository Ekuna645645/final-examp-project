package ge.btu.flowershop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ge.btu.flowershop.ui.BloomApp
import ge.btu.flowershop.ui.theme.BloomTheme

/** Single Activity hosting the whole Compose UI. No XML layouts, no findViewById. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BloomTheme {
                BloomApp()
            }
        }
    }
}
