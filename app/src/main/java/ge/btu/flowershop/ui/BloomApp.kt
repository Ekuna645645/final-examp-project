package ge.btu.flowershop.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ge.btu.flowershop.data.model.AppUser
import ge.btu.flowershop.data.model.UserRole
import ge.btu.flowershop.ui.auth.AuthNavHost
import ge.btu.flowershop.ui.common.LoadingScreen
import ge.btu.flowershop.ui.common.roleLabel

/** Root composable: routes between loading / auth / role-based home based on session. */
@Composable
fun BloomApp(session: SessionViewModel = viewModel()) {
    val state by session.sessionState.collectAsStateWithLifecycle()
    when (val s = state) {
        SessionState.Loading -> LoadingScreen()
        SessionState.SignedOut -> AuthNavHost()
        is SessionState.SignedIn -> RoleHost(user = s.user, onSignOut = session::signOut)
    }
}

/** Sends each role to its own experience (filled out in later phases). */
@Composable
private fun RoleHost(user: AppUser, onSignOut: () -> Unit) {
    when (user.userRole) {
        UserRole.CUSTOMER -> RolePlaceholderHome(user, onSignOut)
        UserRole.COURIER -> RolePlaceholderHome(user, onSignOut)
        UserRole.ADMIN -> RolePlaceholderHome(user, onSignOut)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RolePlaceholderHome(user: AppUser, onSignOut: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bloom · ${roleLabel(user.role)}") },
                actions = {
                    IconButton(onClick = onSignOut) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Sign out")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                Icons.Filled.LocalFlorist,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(72.dp),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Welcome, ${user.name.ifBlank { user.email }}",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "You're signed in as ${roleLabel(user.role)}.\nThis dashboard is being built next.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
