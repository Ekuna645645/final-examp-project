package ge.btu.habittracker.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ge.btu.habittracker.ui.habits.AddEditHabitScreen
import ge.btu.habittracker.ui.habits.HabitsScreen
import ge.btu.habittracker.ui.navigation.Destination
import ge.btu.habittracker.ui.navigation.Routes
import ge.btu.habittracker.ui.stats.StatsScreen
import ge.btu.habittracker.ui.today.TodayScreen

/**
 * Root composable: a [Scaffold] with the bottom-navigation menu and a [NavHost] that
 * swaps between the Today / Habits / Stats screens and the add/edit form.
 */
@Composable
fun HabitTrackerApp(viewModel: HabitViewModel = viewModel()) {
    val navController = rememberNavController()
    RequestNotificationPermission()

    val uiState by viewModel.uiState.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = Destination.bottomBarItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    Destination.bottomBarItems.forEach { dest ->
                        NavigationBarItem(
                            selected = currentRoute == dest.route,
                            onClick = {
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(dest.icon, contentDescription = dest.label) },
                            label = { Text(dest.label) },
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentRoute == Destination.Habits.route) {
                FloatingActionButton(onClick = { navController.navigate(Routes.addEdit()) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add habit")
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Today.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Destination.Today.route) {
                TodayScreen(uiState = uiState, onToggle = viewModel::toggleToday)
            }
            composable(Destination.Habits.route) {
                HabitsScreen(
                    uiState = uiState,
                    onEdit = { habit -> navController.navigate(Routes.addEdit(habit.id)) },
                    onDelete = viewModel::deleteHabit,
                )
            }
            composable(Destination.Stats.route) {
                StatsScreen(uiState = uiState)
            }
            composable(
                route = "${Routes.ADD_EDIT}?${Routes.HABIT_ID_ARG}={${Routes.HABIT_ID_ARG}}",
                arguments = listOf(
                    navArgument(Routes.HABIT_ID_ARG) {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                ),
            ) { backStackEntry ->
                val habitId = backStackEntry.arguments?.getString(Routes.HABIT_ID_ARG).orEmpty()
                val existing = uiState.habits.firstOrNull { it.id == habitId }
                AddEditHabitScreen(
                    existing = existing,
                    onSave = { habit ->
                        viewModel.saveHabit(habit)
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() },
                )
            }
        }
    }
}

/** Asks for the POST_NOTIFICATIONS runtime permission on Android 13+ (needed for reminders). */
@Composable
private fun RequestNotificationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { /* result handled by the system UI */ }
        LaunchedEffect(Unit) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
