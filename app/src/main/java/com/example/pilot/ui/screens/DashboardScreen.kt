package com.example.pilot.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pilot.data.model.Task
import com.example.pilot.data.model.TaskPriority
import com.example.pilot.data.model.DeviceCalendarEvent
import com.example.pilot.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    activeTaskCount: Int,
    upcomingEventCount: Int,
    noteCount: Int,
    habitCount: Int,
    recentTasks: List<Task>,
    upcomingDeviceEvents: List<DeviceCalendarEvent> = emptyList(),
    onToggleTask: (Task) -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToAgenda: () -> Unit,
    onNavigateToNotes: () -> Unit,
    onNavigateToHabits: () -> Unit,
    onNavigateToAbout: () -> Unit = {}
) {
    val dateFormat = SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRANCE)
    val today = dateFormat.format(Date())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header - date + about icon
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 48.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = today.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.4f)
                )
                IconButton(onClick = onNavigateToAbout, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Outlined.Info,
                        contentDescription = "A propos",
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Stats row
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MiniStatCard(
                    icon = Icons.Outlined.CheckCircle,
                    value = "$activeTaskCount",
                    label = "Taches",
                    color = Primary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTasks
                )
                MiniStatCard(
                    icon = Icons.Outlined.CalendarMonth,
                    value = "$upcomingEventCount",
                    label = "Evenements",
                    color = Secondary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToAgenda
                )
                MiniStatCard(
                    icon = Icons.Outlined.StickyNote2,
                    value = "$noteCount",
                    label = "Notes",
                    color = Warning,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToNotes
                )
                MiniStatCard(
                    icon = Icons.Outlined.FitnessCenter,
                    value = "$habitCount",
                    label = "Habitudes",
                    color = Success,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToHabits
                )
            }
        }

        // Upcoming Device Calendar Events
        if (upcomingDeviceEvents.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Prochains evenements",
                    actionText = "Voir tout",
                    onAction = onNavigateToAgenda
                )
            }
            items(upcomingDeviceEvents, key = { "device_${it.id}" }) { event ->
                DeviceEventItem(event)
            }
        }

        // Recent Tasks
        item {
            SectionHeader(
                title = "Taches en cours",
                actionText = "Voir tout",
                onAction = onNavigateToTasks
            )
        }

        if (recentTasks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Outlined.TaskAlt,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Color.White.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Aucune tache en cours",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        } else {
            items(recentTasks.take(5), key = { it.id }) { task ->
                QuickTaskItem(task = task, onToggle = { onToggleTask(task) })
            }
        }

        // Quick Actions
        item {
            SectionHeader(title = "Acces rapide")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Filled.Add,
                    label = "Tache",
                    color = Primary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTasks
                )
                QuickActionButton(
                    icon = Icons.Filled.Event,
                    label = "Evenement",
                    color = Secondary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToAgenda
                )
                QuickActionButton(
                    icon = Icons.Filled.EditNote,
                    label = "Note",
                    color = Warning,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToNotes
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(top = 28.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        if (actionText != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(actionText, color = Primary, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun DeviceEventItem(event: DeviceCalendarEvent) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.FRANCE)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Primary)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${timeFormat.format(Date(event.startTime))} - ${timeFormat.format(Date(event.endTime))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Primary.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun MiniStatCard(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun QuickTaskItem(task: Task, onToggle: () -> Unit) {
    val priorityColor = when (task.priority) {
        TaskPriority.HIGH -> PriorityHigh
        TaskPriority.MEDIUM -> PriorityMedium
        TaskPriority.LOW -> PriorityLow
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(priorityColor)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.35f),
                        maxLines = 1
                    )
                }
            }
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Primary,
                    uncheckedColor = Color.White.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = color
            )
        }
    }
}
