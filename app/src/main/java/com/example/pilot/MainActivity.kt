package com.example.pilot

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pilot.notifications.NotificationHelper
import com.example.pilot.ui.components.PermissionScreen
import com.example.pilot.ui.navigation.Screen
import com.example.pilot.ui.screens.*
import com.example.pilot.ui.theme.PilotTheme
import com.example.pilot.ui.theme.Primary
import com.example.pilot.ui.viewmodel.*
import com.example.pilot.update.AppUpdate
import com.example.pilot.update.UpdateChecker
import com.example.pilot.update.UpdateDialog
import com.example.pilot.update.UpdateManager
import com.example.pilot.util.PermissionHelper

private enum class AppState { SPLASH, PERMISSIONS, MAIN }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )

        NotificationHelper.createNotificationChannels(this)

        var keepSplash = true
        splash.setKeepOnScreenCondition { keepSplash }

        val allGranted = PermissionHelper.getRequiredPermissions().all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        setContent {
            PilotTheme {
                var appState by remember { mutableStateOf(AppState.SPLASH) }

                LaunchedEffect(Unit) { keepSplash = false }

                Box {
                    when (appState) {
                        AppState.MAIN -> PilotApp()
                        AppState.PERMISSIONS -> {
                            PermissionScreen(
                                onAllGranted = { appState = AppState.MAIN },
                                onSkip = { appState = AppState.MAIN }
                            )
                        }
                        else -> {}
                    }

                    AnimatedVisibility(
                        visible = appState == AppState.SPLASH,
                        exit = fadeOut(animationSpec = tween(500))
                    ) {
                        SplashScreen(
                            onSplashFinished = {
                                appState = if (allGranted) AppState.MAIN else AppState.PERMISSIONS
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun androidx.navigation.NavController.navigateBottomNav(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun PilotApp() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Auto-update check
    var availableUpdate by remember { mutableStateOf<AppUpdate?>(null) }
    LaunchedEffect(Unit) {
        val update = UpdateChecker.checkForUpdate(context)
        if (update != null) {
            NotificationHelper.showUpdateNotification(context, update.versionName)
        }
        availableUpdate = update
    }

    val taskViewModel: TaskViewModel = viewModel()
    val eventViewModel: EventViewModel = viewModel()
    val noteViewModel: NoteViewModel = viewModel()
    val habitViewModel: HabitViewModel = viewModel()
    val calendarViewModel: CalendarViewModel = viewModel()

    LaunchedEffect(Unit) {
        calendarViewModel.loadUpcomingEvents()
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black,
                tonalElevation = 0.dp
            ) {
                Screen.bottomNavItems.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigateBottomNav(screen.route)
                        },
                        icon = {
                            Icon(
                                if (selected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        },
                        label = { Text(screen.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Primary,
                            selectedTextColor = Primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            indicatorColor = Primary.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(300)) + slideInVertically { it / 8 } },
            exitTransition = { fadeOut(animationSpec = tween(200)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            popExitTransition = { fadeOut(animationSpec = tween(200)) }
        ) {
            composable(Screen.Dashboard.route) {
                val allTasks by taskViewModel.allTasks.collectAsStateWithLifecycle()
                val allEvents by eventViewModel.allEvents.collectAsStateWithLifecycle()
                val allNotes by noteViewModel.allNotes.collectAsStateWithLifecycle()
                val allHabits by habitViewModel.allHabits.collectAsStateWithLifecycle()
                val upcomingDeviceEvents by calendarViewModel.upcomingEvents.collectAsStateWithLifecycle()

                DashboardScreen(
                    activeTaskCount = allTasks.count { !it.isCompleted },
                    upcomingEventCount = allEvents.size,
                    noteCount = allNotes.size,
                    habitCount = allHabits.size,
                    recentTasks = allTasks.filter { !it.isCompleted }.take(5),
                    upcomingDeviceEvents = upcomingDeviceEvents.take(3),
                    onToggleTask = { taskViewModel.toggleTask(it) },
                    onNavigateToTasks = { navController.navigateBottomNav(Screen.Tasks.route) },
                    onNavigateToAgenda = { navController.navigateBottomNav(Screen.Agenda.route) },
                    onNavigateToNotes = { navController.navigateBottomNav(Screen.Notes.route) },
                    onNavigateToHabits = { navController.navigateBottomNav(Screen.Habits.route) }
                )
            }

            composable(Screen.Tasks.route) {
                val tasks by taskViewModel.allTasks.collectAsStateWithLifecycle()
                TasksScreen(
                    tasks = tasks,
                    onAddTask = { title, desc, priority -> taskViewModel.addTask(title, desc, priority) },
                    onToggleTask = { taskViewModel.toggleTask(it) },
                    onDeleteTask = { taskViewModel.deleteTask(it) }
                )
            }

            composable(Screen.Agenda.route) {
                val events by eventViewModel.allEvents.collectAsStateWithLifecycle()
                val deviceEvents by calendarViewModel.deviceEvents.collectAsStateWithLifecycle()

                AgendaScreen(
                    events = events,
                    deviceEvents = deviceEvents,
                    onAddEvent = { title, desc, start, end -> eventViewModel.addEvent(title, desc, start, end) },
                    onDeleteEvent = { eventViewModel.deleteEvent(it) },
                    onDateSelected = { year, month, day ->
                        calendarViewModel.loadEventsForDate(year, month, day)
                    }
                )
            }

            composable(Screen.Notes.route) {
                val notes by noteViewModel.allNotes.collectAsStateWithLifecycle()
                NotesScreen(
                    notes = notes,
                    onAddNote = { title, content, color -> noteViewModel.addNote(title, content, color) },
                    onTogglePin = { noteViewModel.togglePin(it) },
                    onDeleteNote = { noteViewModel.deleteNote(it) }
                )
            }

            composable(Screen.Habits.route) {
                val habits by habitViewModel.allHabits.collectAsStateWithLifecycle()
                val allEntries by habitViewModel.allEntries.collectAsStateWithLifecycle()
                HabitsScreen(
                    habits = habits,
                    weekEntries = allEntries,
                    onAddHabit = { name, icon -> habitViewModel.addHabit(name, icon) },
                    onToggleHabit = { habit, entries -> habitViewModel.toggleHabitForToday(habit, entries) },
                    onDeleteHabit = { habitViewModel.deleteHabit(it) }
                )
            }
        }
    }

    // Update dialog
    availableUpdate?.let { update ->
        UpdateDialog(
            update = update,
            onUpdate = {
                UpdateManager.downloadAndInstall(context, update)
                availableUpdate = null
            },
            onDismiss = { availableUpdate = null }
        )
    }
}
