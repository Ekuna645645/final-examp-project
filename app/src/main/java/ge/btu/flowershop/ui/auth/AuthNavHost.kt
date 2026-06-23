package ge.btu.flowershop.ui.auth

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ge.btu.flowershop.data.model.UserRole

/** Login / Register navigation shown when nobody is signed in. */
@Composable
fun AuthNavHost(
    showPreview: Boolean = false,
    onPreview: (UserRole) -> Unit = {},
    authViewModel: AuthViewModel = viewModel(),
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate("register") },
                showPreview = showPreview,
                onPreview = onPreview,
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() },
            )
        }
    }
}
