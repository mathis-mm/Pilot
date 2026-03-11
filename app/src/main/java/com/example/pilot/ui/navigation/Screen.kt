package com.example.pilot.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Dashboard : Screen("dashboard", "Accueil", Icons.Filled.Dashboard, Icons.Outlined.Dashboard)
    data object Tasks : Screen("tasks", "Tâches", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle)
    data object Agenda : Screen("agenda", "Agenda", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth)
    data object Notes : Screen("notes", "Notes", Icons.Filled.StickyNote2, Icons.Outlined.StickyNote2)
    data object Habits : Screen("habits", "Habitudes", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter)

    companion object {
        val bottomNavItems = listOf(Dashboard, Tasks, Agenda, Notes, Habits)
    }
}
