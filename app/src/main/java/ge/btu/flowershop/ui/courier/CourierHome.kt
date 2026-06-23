package ge.btu.flowershop.ui.courier

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ge.btu.flowershop.data.model.AppUser
import ge.btu.flowershop.ui.common.ComingSoon

/** Courier experience — full delivery flow (accept, pick up, deliver) arrives in Phase 3. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourierHome(user: AppUser, onSignOut: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bloom · Courier") },
                actions = {
                    IconButton(onClick = onSignOut) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Sign out")
                    }
                },
            )
        },
    ) { padding ->
        ComingSoon(
            "Courier dashboard",
            "Accepting orders, pickup and delivery tracking arrive in Phase 3.",
            Modifier.padding(padding),
        )
    }
}
