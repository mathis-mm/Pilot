package com.example.pilot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pilot.data.model.Habit
import com.example.pilot.data.model.HabitEntry
import com.example.pilot.ui.theme.*
import com.example.pilot.ui.viewmodel.HabitViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    habits: List<Habit>,
    weekEntries: List<HabitEntry>,
    onAddHabit: (String, String) -> Unit,
    onToggleHabit: (Habit, List<HabitEntry>) -> Unit,
    onDeleteHabit: (Habit) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val todayStart = HabitViewModel.getTodayStart()

    // Generate week days
    val weekDays = remember {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        (0..6).map {
            val d = cal.clone() as Calendar
            cal.add(Calendar.DAY_OF_MONTH, 1)
            d
        }
    }

    val dayNameFormat = SimpleDateFormat("EEE", Locale.FRANCE)

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nouvelle habitude") },
                containerColor = Success,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Header
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text(
                        text = "Habitudes",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Suivez vos routines quotidiennes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Weekly overview
            if (habits.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Cette semaine",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                weekDays.forEach { day ->
                                    val dayStart = day.timeInMillis
                                    val completedForDay = weekEntries.count { it.date == dayStart }
                                    val isToday = dayStart == todayStart
                                    val percentage = if (habits.isNotEmpty()) completedForDay.toFloat() / habits.size else 0f

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            dayNameFormat.format(day.time).take(2).uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when {
                                                        percentage >= 1f -> Success
                                                        percentage > 0f -> Success.copy(alpha = 0.3f)
                                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                                    }
                                                )
                                                .then(
                                                    if (isToday) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                                    else Modifier
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (percentage >= 1f) {
                                                Icon(
                                                    Icons.Filled.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            } else {
                                                Text(
                                                    "${(percentage * 100).toInt()}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (habits.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon = Icons.Outlined.FitnessCenter,
                        title = "Aucune habitude",
                        subtitle = "Créez votre première habitude à suivre !"
                    )
                }
            }

            // Habits list
            items(habits, key = { it.id }) { habit ->
                val isCompletedToday = weekEntries.any { it.habitId == habit.id && it.date == todayStart }
                HabitItem(
                    habit = habit,
                    isCompletedToday = isCompletedToday,
                    weekEntries = weekEntries.filter { it.habitId == habit.id },
                    weekDays = weekDays,
                    onToggle = { onToggleHabit(habit, weekEntries) },
                    onDelete = { onDeleteHabit(habit) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddHabitDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, icon ->
                onAddHabit(name, icon)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun HabitItem(
    habit: Habit,
    isCompletedToday: Boolean,
    weekEntries: List<HabitEntry>,
    weekDays: List<Calendar>,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(habit.icon, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (isCompletedToday) "Fait aujourd'hui ✓" else "Pas encore fait",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isCompletedToday) Success else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                FilledIconButton(
                    onClick = onToggle,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (isCompletedToday) Success else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        if (isCompletedToday) Icons.Filled.Check else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = "Marquer fait",
                        tint = if (isCompletedToday) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = { showDeleteConfirm = true }, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Supprimer",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Mini week view
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                weekDays.forEach { day ->
                    val isDone = weekEntries.any { it.date == day.timeInMillis }
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                if (isDone) Success.copy(alpha = 0.8f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDone) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Supprimer cette habitude ?") },
            text = { Text("\"${habit.name}\" et toutes ses données seront supprimées.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteConfirm = false }) {
                    Text("Supprimer", color = Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Annuler") }
            }
        )
    }
}

@Composable
fun AddHabitDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("✅") }

    val icons = listOf("✅", "💪", "📚", "🏃", "💧", "🧘", "🎯", "💤", "🥗", "🎵", "✍️", "🧹")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouvelle habitude", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom de l'habitude") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Text("Icône", style = MaterialTheme.typography.labelLarge)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(icons) { icon ->
                        FilterChip(
                            selected = selectedIcon == icon,
                            onClick = { selectedIcon = icon },
                            label = { Text(icon, fontSize = 20.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name, selectedIcon) },
                enabled = name.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Créer") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        },
        shape = RoundedCornerShape(24.dp)
    )
}
