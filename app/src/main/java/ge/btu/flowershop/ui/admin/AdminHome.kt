package ge.btu.flowershop.ui.admin

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FloatingActionButton
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ge.btu.flowershop.data.model.AppUser
import ge.btu.flowershop.ui.ProductViewModel
import ge.btu.flowershop.ui.common.AccountScreen
import ge.btu.flowershop.ui.common.ComingSoon

private data class AdminTab(val route: String, val label: String, val icon: ImageVector)

/** Admin experience: bottom-nav menu over Products / Orders / Stats / Account. */
@Composable
fun AdminHome(
    user: AppUser,
    onSignOut: () -> Unit,
    productViewModel: ProductViewModel = viewModel(),
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val tabs = listOf(
        AdminTab("products", "Products", Icons.Filled.Inventory2),
        AdminTab("orders", "Orders", Icons.AutoMirrored.Filled.ReceiptLong),
        AdminTab("stats", "Stats", Icons.Filled.BarChart),
        AdminTab("account", "Account", Icons.Filled.Person),
    )
    val showBottomBar = tabs.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
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
            }
        },
        floatingActionButton = {
            if (currentRoute == "products") {
                FloatingActionButton(onClick = { navController.navigate("editor") }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add product")
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "products",
            modifier = Modifier.padding(padding),
        ) {
            composable("products") {
                AdminProductsScreen(
                    productViewModel = productViewModel,
                    onEdit = { navController.navigate("editor?id=${it.id}") },
                )
            }
            composable(
                route = "editor?id={id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType; defaultValue = "" }),
            ) { entry ->
                val id = entry.arguments?.getString("id").orEmpty()
                AddEditProductScreen(
                    existing = productViewModel.productById(id),
                    onSave = { product ->
                        productViewModel.save(product)
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() },
                )
            }
            composable("orders") {
                ComingSoon("Live orders", "The live order board arrives in Phase 3.")
            }
            composable("stats") {
                ComingSoon("Statistics", "The admin statistics dashboard arrives in Phase 4.")
            }
            composable("account") {
                AccountScreen(user = user, onSignOut = onSignOut)
            }
        }
    }
}
