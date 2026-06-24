package ge.btu.flowershop.ui.courier

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ge.btu.flowershop.data.model.AppUser
import ge.btu.flowershop.ui.OrderViewModel
import ge.btu.flowershop.ui.common.AccountScreen

private data class CourierTab(val route: String, val label: String, val icon: ImageVector)

/** Courier experience: bottom-nav menu over Available / Deliveries / Account. */
@Composable
fun CourierHome(
    user: AppUser,
    onSignOut: () -> Unit,
    orderViewModel: OrderViewModel = viewModel(),
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val tabs = listOf(
        CourierTab("available", "Available", Icons.Outlined.Inbox),
        CourierTab("deliveries", "Deliveries", Icons.Filled.LocalShipping),
        CourierTab("account", "Account", Icons.Filled.Person),
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentRoute == tab.route,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "available",
            modifier = Modifier.padding(padding),
        ) {
            composable("available") { CourierAvailableScreen(orderViewModel, user) }
            composable("deliveries") { CourierDeliveriesScreen(orderViewModel, user) }
            composable("account") { AccountScreen(user = user, onSignOut = onSignOut) }
        }
    }
}
