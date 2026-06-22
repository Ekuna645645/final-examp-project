package ge.btu.flowershop.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = RosePrimary,
    onPrimary = RoseOnPrimary,
    primaryContainer = RoseContainer,
    onPrimaryContainer = RoseOnContainer,
    secondary = LeafSecondary,
    secondaryContainer = LeafContainer,
    onSecondaryContainer = LeafOnContainer,
    tertiary = PinkTertiary,
)

private val DarkColors = darkColorScheme(
    primary = PinkDarkPrimary,
    secondary = GreenDarkSecondary,
    tertiary = PinkTertiary,
)

@Composable
fun BloomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Fixed brand colors (no dynamic color) so the florist identity stays consistent.
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content,
    )
}
