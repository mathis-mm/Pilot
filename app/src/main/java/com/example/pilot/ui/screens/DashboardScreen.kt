package com.example.pilot.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pilot.data.model.Task
import com.example.pilot.data.model.TaskPriority
import com.example.pilot.data.model.DeviceCalendarEvent
import com.example.pilot.ui.theme.*
import kotlinx.coroutines.delay
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

    // Staggered entrance animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { -40 }
            ) {
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
        }

        // Stats row with staggered animation
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnimatedStatCard(
                    icon = Icons.Outlined.CheckCircle,
                    targetValue = activeTaskCount,
                    label = "Taches",
                    color = Primary,
                    modifier = Modifier.weight(1f),
                    delay = 100,
                    onClick = onNavigateToTasks
                )
                AnimatedStatCard(
                    icon = Icons.Outlined.CalendarMonth,
                    targetValue = upcomingEventCount,
                    label = "Evenements",
                    color = Secondary,
                    modifier = Modifier.weight(1f),
                    delay = 200,
                    onClick = onNavigateToAgenda
                )
                AnimatedStatCard(
                    icon = Icons.Outlined.StickyNote2,
                    targetValue = noteCount,
                    label = "Notes",
                    color = Warning,
                    modifier = Modifier.weight(1f),
                    delay = 300,
                    onClick = onNavigateToNotes
                )
                AnimatedStatCard(
                    icon = Icons.Outlined.FitnessCenter,
                    targetValue = habitCount,
                    label = "Habitudes",
                    color = Success,
                    modifier = Modifier.weight(1f),
                    delay = 400,
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
            itemsIndexed(upcomingDeviceEvents, key = { _, e -> "device_${e.id}" }) { index, event ->
                SlideInItem(delay = index * 80) {
                    DeviceEventItem(event)
                }
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
                SlideInItem(delay = 100) {
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
            }
        } else {
            itemsIndexed(recentTasks.take(5), key = { _, t -> t.id }) { index, task ->
                SlideInItem(delay = index * 60) {
                    QuickTaskItem(task = task, onToggle = { onToggleTask(task) })
                }
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
                BounceActionButton(
                    icon = Icons.Filled.Add,
                    label = "Tache",
                    color = Primary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTasks
                )
                BounceActionButton(
                    icon = Icons.Filled.Event,
                    label = "Evenement",
                    color = Secondary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToAgenda
                )
                BounceActionButton(
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
fun SlideInItem(delay: Int = 0, content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(delay.toLong())
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(400)) + slideInHorizontally(tween(400, easing = FastOutSlowInEasing)) { 60 }
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedStatCard(
    icon: ImageVector,
    targetValue: Int,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    delay: Int = 0,
    onClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(delay.toLong())
        visible = true
    }

    val animatedValue by animateIntAsState(
        targetValue = if (visible) targetValue else 0,
        animationSpec = tween(800, delayMillis = delay, easing = FastOutSlowInEasing),
        label = "counter"
    )

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.7f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(500, delayMillis = delay),
        label = "alpha"
    )

    Card(
        onClick = onClick,
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
        },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "$animatedValue",
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
                Text(event.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = Color.White)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "${timeFormat.format(Date(event.startTime))} - ${timeFormat.format(Date(event.endTime))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Primary.copy(alpha = 0.7f)
                )
            }
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

    // Animate checkbox
    val checkScale by animateFloatAsState(
        targetValue = if (task.isCompleted) 1.2f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 500f),
        label = "check"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp)
            .animateContentSize(animationSpec = spring(dampingRatio = 0.8f)),
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
                    color = if (task.isCompleted) Color.White.copy(alpha = 0.3f) else Color.White
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
                modifier = Modifier.scale(checkScale),
                colors = CheckboxDefaults.colors(
                    checkedColor = Primary,
                    uncheckedColor = Color.White.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
fun BounceActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 800f),
        label = "bounce"
    )

    Card(
        modifier = modifier
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                        onClick()
                    }
                )
            },
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
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = color)
        }
    }
}
