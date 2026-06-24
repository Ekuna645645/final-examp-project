package ge.btu.flowershop.ui.customer

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ge.btu.flowershop.data.model.AppUser
import ge.btu.flowershop.ui.CartViewModel
import ge.btu.flowershop.ui.OrderViewModel
import ge.btu.flowershop.ui.ProductViewModel
import ge.btu.flowershop.ui.TicketViewModel
import ge.btu.flowershop.ui.common.AccountScreen

private data class CustomerTab(val route: String, val label: String, val icon: ImageVector)

/** Customer experience: a bottom-nav menu over Shop / Cart / Orders / Account. */
@Composable
fun CustomerHome(
    user: AppUser,
    onSignOut: () -> Unit,
    productViewModel: ProductViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel(),
    orderViewModel: OrderViewModel = viewModel(),
    ticketViewModel: TicketViewModel = viewModel(),
) {
    val navController = rememberNavController()
    val cartItems by cartViewModel.items.collectAsStateWithLifecycle()
    val cartCount = cartItems.sumOf { it.quantity }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val tabs = listOf(
        CustomerTab("shop", "Shop", Icons.Filled.Storefront),
        CustomerTab("cart", "Cart", Icons.Filled.ShoppingCart),
        CustomerTab("orders", "Orders", Icons.AutoMirrored.Filled.ReceiptLong),
        CustomerTab("account", "Account", Icons.Filled.Person),
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
                            icon = {
                                if (tab.route == "cart" && cartCount > 0) {
                                    BadgedBox(badge = { Badge { Text("$cartCount") } }) {
                                        Icon(tab.icon, contentDescription = tab.label)
                                    }
                                } else {
                                    Icon(tab.icon, contentDescription = tab.label)
                                }
                            },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "shop",
            modifier = Modifier.padding(padding),
        ) {
            composable("shop") {
                ShopScreen(
                    productViewModel = productViewModel,
                    onProductClick = { navController.navigate("product/${it.id}") },
                    onAdd = { cartViewModel.add(it) },
                )
            }
            composable("product/{id}") { entry ->
                val id = entry.arguments?.getString("id").orEmpty()
                ProductDetailScreen(
                    product = productViewModel.productById(id),
                    onBack = { navController.popBackStack() },
                    onAddToCart = { product, qty ->
                        cartViewModel.add(product, qty)
                        navController.popBackStack()
                    },
                )
            }
            composable("cart") {
                CartScreen(cartViewModel = cartViewModel, onCheckout = { navController.navigate("checkout") })
            }
            composable("checkout") {
                CheckoutScreen(
                    items = cartItems,
                    user = user,
                    orderViewModel = orderViewModel,
                    onBack = { navController.popBackStack() },
                    onPlaced = {
                        cartViewModel.clear()
                        navController.navigate("orders") {
                            popUpTo("shop")
                            launchSingleTop = true
                        }
                    },
                )
            }
            composable("orders") {
                CustomerOrdersScreen(orderViewModel = orderViewModel, user = user)
            }
            composable("account") {
                AccountScreen(
                    user = user,
                    onSignOut = onSignOut,
                    onSupport = { navController.navigate("tickets") },
                )
            }
            composable("tickets") {
                CustomerTicketsScreen(
                    ticketViewModel = ticketViewModel,
                    user = user,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
