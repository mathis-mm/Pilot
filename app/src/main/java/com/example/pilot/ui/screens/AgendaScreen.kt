package com.example.pilot.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pilot.data.model.Event
import com.example.pilot.data.model.DeviceCalendarEvent
import com.example.pilot.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    events: List<Event>,
    deviceEvents: List<DeviceCalendarEvent> = emptyList(),
    onAddEvent: (String, String, Long, Long) -> Unit,
    onDeleteEvent: (Event) -> Unit,
    onDateSelected: (Int, Int, Int) -> Unit = { _, _, _ -> }
) {
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var showAddDialog by remember { mutableStateOf(false) }

    // Entrance animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val dayFormat = SimpleDateFormat("d", Locale.FRANCE)
    val dayNameFormat = SimpleDateFormat("EEE", Locale.FRANCE)
    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.FRANCE)
    val fullDateFormat = SimpleDateFormat("EEEE d MMMM", Locale.FRANCE)
    val timeFormat = SimpleDateFormat("HH:mm", Locale.FRANCE)

    // Load calendar events for today on first composition
    LaunchedEffect(Unit) {
        val now = Calendar.getInstance()
        onDateSelected(
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        )
    }

    // Generate days around selected date
    val days = remember(selectedDate) {
        val cal = selectedDate.clone() as Calendar
        cal.add(Calendar.DAY_OF_MONTH, -3)
        (0..13).map {
            val d = cal.clone() as Calendar
            cal.add(Calendar.DAY_OF_MONTH, 1)
            d
        }
    }

    // Filter events for selected day
    val selectedDayStart = remember(selectedDate) {
        val cal = selectedDate.clone() as Calendar
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.timeInMillis
    }
    val selectedDayEnd = selectedDayStart + 86400000L

    val dayEvents = events.filter { it.startTime in selectedDayStart until selectedDayEnd }
    val allDayEvents = dayEvents + deviceEvents.map { null } // just for count display
    val totalCount = dayEvents.size + deviceEvents.size

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Evenement") },
                containerColor = Primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Header with month + arrows
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { -40 }
                ) {
                    Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 20.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        val newDate = selectedDate.clone() as Calendar
                        newDate.add(Calendar.MONTH, -1)
                        selectedDate = newDate
                        onDateSelected(
                            newDate.get(Calendar.YEAR),
                            newDate.get(Calendar.MONTH),
                            newDate.get(Calendar.DAY_OF_MONTH)
                        )
                    }) {
                        Icon(
                            Icons.Filled.ChevronLeft,
                            contentDescription = "Mois precedent",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = monthFormat.format(selectedDate.time).replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    IconButton(onClick = {
                        val newDate = selectedDate.clone() as Calendar
                        newDate.add(Calendar.MONTH, 1)
                        selectedDate = newDate
                        onDateSelected(
                            newDate.get(Calendar.YEAR),
                            newDate.get(Calendar.MONTH),
                            newDate.get(Calendar.DAY_OF_MONTH)
                        )
                    }) {
                        Icon(
                            Icons.Filled.ChevronRight,
                            contentDescription = "Mois suivant",
                            tint = Color.White
                        )
                    }
                }
                }
            }

            // Day selector with left/right arrows
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val newDate = selectedDate.clone() as Calendar
                            newDate.add(Calendar.DAY_OF_MONTH, -1)
                            selectedDate = newDate
                            onDateSelected(
                                newDate.get(Calendar.YEAR),
                                newDate.get(Calendar.MONTH),
                                newDate.get(Calendar.DAY_OF_MONTH)
                            )
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Filled.ChevronLeft,
                            contentDescription = "Jour precedent",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    LazyRow(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(days) { day ->
                            val isSelected = day.get(Calendar.DAY_OF_YEAR) == selectedDate.get(Calendar.DAY_OF_YEAR)
                                    && day.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR)
                            val isToday = day.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                                    && day.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)

                            DayChip(
                                dayName = dayNameFormat.format(day.time).uppercase(),
                                dayNumber = dayFormat.format(day.time),
                                isSelected = isSelected,
                                isToday = isToday,
                                onClick = {
                                    selectedDate = day
                                    onDateSelected(
                                        day.get(Calendar.YEAR),
                                        day.get(Calendar.MONTH),
                                        day.get(Calendar.DAY_OF_MONTH)
                                    )
                                }
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            val newDate = selectedDate.clone() as Calendar
                            newDate.add(Calendar.DAY_OF_MONTH, 1)
                            selectedDate = newDate
                            onDateSelected(
                                newDate.get(Calendar.YEAR),
                                newDate.get(Calendar.MONTH),
                                newDate.get(Calendar.DAY_OF_MONTH)
                            )
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Filled.ChevronRight,
                            contentDescription = "Jour suivant",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Selected day label
            item {
                Text(
                    text = fullDateFormat.format(selectedDate.time).replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)
                )
            }

            // App events
            if (dayEvents.isEmpty() && deviceEvents.isEmpty()) {
                item {
                    EmptyStateCard(
                        icon = Icons.Outlined.EventBusy,
                        title = "Aucun evenement",
                        subtitle = "Journee libre !"
                    )
                }
            }

            items(dayEvents, key = { it.id }) { event ->
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(400, delayMillis = 200)) +
                            slideInHorizontally(tween(400, delayMillis = 200)) { 80 }
                ) {
                    EventItem(
                        event = event,
                        timeFormat = timeFormat,
                        onDelete = { onDeleteEvent(event) }
                    )
                }
            }

            // Device calendar events
            if (deviceEvents.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.PhoneAndroid,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Google Calendar",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
                itemsIndexed(deviceEvents, key = { _, e -> "device_${e.id}" }) { index, deviceEvent ->
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(400, delayMillis = 300 + index * 60)) +
                                slideInHorizontally(tween(400, delayMillis = 300 + index * 60)) { 80 }
                    ) {
                        DeviceCalendarEventItem(
                            event = deviceEvent,
                            timeFormat = timeFormat
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddEventDialog(
            selectedDate = selectedDate,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, desc, start, end ->
                onAddEvent(title, desc, start, end)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun DayChip(
    dayName: String,
    dayNumber: String,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(50.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> Primary
                isToday -> Primary.copy(alpha = 0.15f)
                else -> Color(0xFF111111)
            }
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dayName,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) Color.White
                else Color.White.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = dayNumber,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White
                else if (isToday) Primary
                else Color.White
            )
        }
    }
}

@Composable
fun EventItem(event: Event, timeFormat: SimpleDateFormat, onDelete: () -> Unit) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp)
            .animateContentSize(spring(dampingRatio = 0.8f)),
        shape = RoundedCornerShape(14.dp),
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
                    .height(44.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(event.color))
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                if (event.description.isNotBlank()) {
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${timeFormat.format(Date(event.startTime))} - ${timeFormat.format(Date(event.endTime))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Primary
                    )
                }
            }
            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Supprimer",
                    tint = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Supprimer l'evenement ?") },
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
fun AddEventDialog(
    selectedDate: Calendar,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long, Long) -> Unit
) {
    val now = Calendar.getInstance()
    val currentH = String.format("%02d", now.get(Calendar.HOUR_OF_DAY))
    val currentM = String.format("%02d", now.get(Calendar.MINUTE))
    val endH = String.format("%02d", (now.get(Calendar.HOUR_OF_DAY) + 1) % 24)

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startHour by remember { mutableStateOf(currentH) }
    var startMinute by remember { mutableStateOf(currentM) }
    var endHour by remember { mutableStateOf(endH) }
    var endMinute by remember { mutableStateOf(currentM) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouvel evenement", fontWeight = FontWeight.Bold) },
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
                    shape = RoundedCornerShape(12.dp)
                )
                Text("Debut", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.5f))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startHour,
                        onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) startHour = it },
                        label = { Text("Heure") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = startMinute,
                        onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) startMinute = it },
                        label = { Text("Min") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
                Text("Fin", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.5f))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = endHour,
                        onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) endHour = it },
                        label = { Text("Heure") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = endMinute,
                        onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) endMinute = it },
                        label = { Text("Min") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val cal = selectedDate.clone() as Calendar
                        cal.set(Calendar.HOUR_OF_DAY, startHour.toIntOrNull() ?: now.get(Calendar.HOUR_OF_DAY))
                        cal.set(Calendar.MINUTE, startMinute.toIntOrNull() ?: 0)
                        cal.set(Calendar.SECOND, 0)
                        val start = cal.timeInMillis
                        cal.set(Calendar.HOUR_OF_DAY, endHour.toIntOrNull() ?: (now.get(Calendar.HOUR_OF_DAY) + 1))
                        cal.set(Calendar.MINUTE, endMinute.toIntOrNull() ?: 0)
                        val end = cal.timeInMillis
                        onConfirm(title, description, start, end)
                    }
                },
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Ajouter") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun DeviceCalendarEventItem(
    event: DeviceCalendarEvent,
    timeFormat: SimpleDateFormat
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp),
        shape = RoundedCornerShape(14.dp),
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
                    .height(44.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Tertiary)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                if (event.location.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.White.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = event.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Tertiary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (event.isAllDay) "Toute la journee"
                        else "${timeFormat.format(Date(event.startTime))} - ${timeFormat.format(Date(event.endTime))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Tertiary
                    )
                }
            }
        }
    }
}
