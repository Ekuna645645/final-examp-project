package ge.btu.flowershop.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ge.btu.flowershop.data.model.AppUser
import ge.btu.flowershop.data.model.UserRole
import ge.btu.flowershop.ui.admin.AdminHome
import ge.btu.flowershop.ui.auth.AuthNavHost
import ge.btu.flowershop.ui.common.LoadingScreen
import ge.btu.flowershop.ui.courier.CourierHome
import ge.btu.flowershop.ui.customer.CustomerHome

/** Root composable: routes between loading / auth / role-based home based on session. */
@Composable
fun BloomApp(session: SessionViewModel = viewModel()) {
    val state by session.sessionState.collectAsStateWithLifecycle()
    when (val s = state) {
        SessionState.Loading -> LoadingScreen()
        SessionState.SignedOut -> AuthNavHost(
            showPreview = !session.isConfigured,
            onPreview = session::previewAs,
        )
        is SessionState.SignedIn -> RoleHost(user = s.user, onSignOut = session::signOut)
    }
}

/** Sends each role to its own experience. */
@Composable
private fun RoleHost(user: AppUser, onSignOut: () -> Unit) {
    when (user.userRole) {
        UserRole.CUSTOMER -> CustomerHome(user = user, onSignOut = onSignOut)
        UserRole.COURIER -> CourierHome(user = user, onSignOut = onSignOut)
        UserRole.ADMIN -> AdminHome(user = user, onSignOut = onSignOut)
    }
}
