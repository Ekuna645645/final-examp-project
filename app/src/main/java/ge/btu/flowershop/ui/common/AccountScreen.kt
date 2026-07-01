package ge.btu.flowershop.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ge.btu.flowershop.data.model.AppUser

/** Shared profile screen for every role: identity, optional management entries, and sign out. */
@Composable
fun AccountScreen(
    user: AppUser,
    onSignOut: () -> Unit,
    onSupport: (() -> Unit)? = null,
    onManageAddresses: (() -> Unit)? = null,
    onManageUsers: (() -> Unit)? = null,
) {
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Account")
        Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    user.name.ifBlank { "Bloom user" },
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    user.email.ifBlank { "—" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(12.dp))
                AssistChip(onClick = {}, label = { Text(roleLabel(user.role)) })
            }
        }
        Spacer(Modifier.height(24.dp))
        if (onManageUsers != null) {
            AccountAction(Icons.Outlined.Group, "Manage users", onManageUsers)
            Spacer(Modifier.height(12.dp))
        }
        if (onManageAddresses != null) {
            AccountAction(Icons.Filled.LocationOn, "Delivery addresses", onManageAddresses)
            Spacer(Modifier.height(12.dp))
        }
        if (onSupport != null) {
            AccountAction(Icons.Outlined.SupportAgent, "Support tickets", onSupport)
            Spacer(Modifier.height(12.dp))
        }
        OutlinedButton(
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Sign out")
        }
    }
}

@Composable
private fun AccountAction(icon: ImageVector, label: String, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(label)
    }
}
