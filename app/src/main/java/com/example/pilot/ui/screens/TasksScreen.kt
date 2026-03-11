package com.example.pilot.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.pilot.data.model.Task
import com.example.pilot.data.model.TaskPriority
import com.example.pilot.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    tasks: List<Task>,
    onAddTask: (String, String, TaskPriority) -> Unit,
    onToggleTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit = {},
    onDeleteCompleted: () -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showCompletedTasks by remember { mutableStateOf(true) }
    var editingTask by remember { mutableStateOf<Task?>(null) }

    val activeTasks = tasks.filter { !it.isCompleted }
    val completedTasks = tasks.filter { it.isCompleted }

    // Entrance animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    // Animated progress
    val progress = if (tasks.isNotEmpty()) completedTasks.size.toFloat() / tasks.size else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = if (visible) progress else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "progress"
    )
    val animatedPercent by animateIntAsState(
        targetValue = if (visible) (progress * 100).toInt() else 0,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "percent"
    )

    // Pull to refresh
    var isRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            onRefresh()
            delay(500)
            isRefreshing = false
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nouvelle tâche") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { isRefreshing = true },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Header with fade in
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { -40 }
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                            Text(
                                text = "Mes Tâches",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${activeTasks.size} tâche(s) en cours",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                // Animated Progress Card
                if (tasks.isNotEmpty()) {
                    item {
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(500, delayMillis = 150)) + slideInVertically(tween(500, delayMillis = 150)) { 30 }
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 8.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "Progression",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            "${animatedPercent}%",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LinearProgressIndicator(
                                        progress = { animatedProgress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = MaterialTheme.colorScheme.primary,
                                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Active Tasks
                if (activeTasks.isEmpty() && completedTasks.isEmpty()) {
                    item {
                        EmptyStateCard(
                            icon = Icons.Outlined.TaskAlt,
                            title = "Aucune tâche",
                            subtitle = "Appuyez sur + pour créer votre première tâche"
                        )
                    }
                }

                itemsIndexed(activeTasks, key = { _, task -> task.id }) { index, task ->
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(400, delayMillis = 200 + index * 60)) +
                                slideInHorizontally(tween(400, delayMillis = 200 + index * 60)) { 80 }
                    ) {
                        TaskItem(
                            task = task,
                            onToggle = { onToggleTask(task) },
                            onDelete = { onDeleteTask(task) },
                            onEdit = { editingTask = task }
                        )
                    }
                }

                // Completed section
                if (completedTasks.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Terminées (${completedTasks.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Row {
                                IconButton(onClick = { onDeleteCompleted() }) {
                                    Icon(
                                        Icons.Outlined.DeleteSweep,
                                        contentDescription = "Supprimer terminées",
                                        tint = Error.copy(alpha = 0.6f)
                                    )
                                }
                                IconButton(onClick = { showCompletedTasks = !showCompletedTasks }) {
                                    Icon(
                                        if (showCompletedTasks) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                    if (showCompletedTasks) {
                        items(completedTasks, key = { it.id }) { task ->
                            TaskItem(
                                task = task,
                                onToggle = { onToggleTask(task) },
                                onDelete = { onDeleteTask(task) },
                                onEdit = { editingTask = task }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, desc, priority ->
                onAddTask(title, desc, priority)
                showAddDialog = false
            }
        )
    }

    editingTask?.let { task ->
        EditTaskDialog(
            task = task,
            onDismiss = { editingTask = null },
            onConfirm = { updated ->
                onUpdateTask(updated)
                editingTask = null
            }
        )
    }
}

@Composable
fun TaskItem(task: Task, onToggle: () -> Unit, onDelete: () -> Unit, onEdit: () -> Unit = {}) {
    val priorityColor = when (task.priority) {
        TaskPriority.HIGH -> PriorityHigh
        TaskPriority.MEDIUM -> PriorityMedium
        TaskPriority.LOW -> PriorityLow
    }
    val priorityLabel = when (task.priority) {
        TaskPriority.HIGH -> "Haute"
        TaskPriority.MEDIUM -> "Moyenne"
        TaskPriority.LOW -> "Basse"
    }

    var showDeleteConfirm by remember { mutableStateOf(false) }

    val checkScale by animateFloatAsState(
        targetValue = if (task.isCompleted) 1.15f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 500f),
        label = "checkScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .animateContentSize(spring(dampingRatio = 0.8f))
            .clickable { onEdit() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (task.isCompleted) Color.Gray else priorityColor)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.onSurface
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        maxLines = 2
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                SuggestionChip(
                    onClick = {},
                    label = { Text(priorityLabel, style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.height(24.dp),
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = priorityColor.copy(alpha = 0.1f),
                        labelColor = priorityColor
                    ),
                    border = null
                )
            }
            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Supprimer",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.scale(checkScale)
            )
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Supprimer la tâche ?") },
            text = { Text("\"${task.title}\" sera supprimée définitivement.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteConfirm = false }) {
                    Text("Supprimer", color = Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
fun EditTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onConfirm: (Task) -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var priority by remember { mutableStateOf(task.priority) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifier la tâche", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titre") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3
                )
                Text("Priorité", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskPriority.entries.forEach { p ->
                        val color = when (p) {
                            TaskPriority.HIGH -> PriorityHigh
                            TaskPriority.MEDIUM -> PriorityMedium
                            TaskPriority.LOW -> PriorityLow
                        }
                        val label = when (p) {
                            TaskPriority.HIGH -> "Haute"
                            TaskPriority.MEDIUM -> "Moy."
                            TaskPriority.LOW -> "Basse"
                        }
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = color.copy(alpha = 0.2f),
                                selectedLabelColor = color
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(task.copy(title = title, description = description, priority = priority))
                    }
                },
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Enregistrer") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, TaskPriority) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Nouvelle tâche", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titre") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optionnel)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3
                )
                Text("Priorité", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskPriority.entries.forEach { p ->
                        val color = when (p) {
                            TaskPriority.HIGH -> PriorityHigh
                            TaskPriority.MEDIUM -> PriorityMedium
                            TaskPriority.LOW -> PriorityLow
                        }
                        val label = when (p) {
                            TaskPriority.HIGH -> "Haute"
                            TaskPriority.MEDIUM -> "Moy."
                            TaskPriority.LOW -> "Basse"
                        }
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = color.copy(alpha = 0.2f),
                                selectedLabelColor = color
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onConfirm(title, description, priority) },
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun EmptyStateCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}
