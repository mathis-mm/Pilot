package com.example.pilot.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pilot.data.model.Note
import com.example.pilot.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    notes: List<Note>,
    onAddNote: (String, String, Int) -> Unit,
    onTogglePin: (Note) -> Unit,
    onDeleteNote: (Note) -> Unit,
    onUpdateNote: (Note) -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf<Note?>(null) }

    // Entrance animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

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
                text = { Text("Nouvelle note") },
                containerColor = Warning,
                contentColor = Color.White,
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
            // Header
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { -40 }
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text(
                        text = "Notes",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${notes.size} note(s)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.StickyNote2,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Aucune note",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            "Capturez vos idées !",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalItemSpacing = 8.dp,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(notes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onTogglePin = { onTogglePin(note) },
                            onDelete = { onDeleteNote(note) },
                            onEdit = { editingNote = note }
                        )
                    }
                }
            }
        }
        }
    }

    if (showAddDialog) {
        AddNoteDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, content, color ->
                onAddNote(title, content, color)
                showAddDialog = false
            }
        )
    }

    editingNote?.let { note ->
        EditNoteDialog(
            note = note,
            onDismiss = { editingNote = null },
            onConfirm = { updated ->
                onUpdateNote(updated)
                editingNote = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(note: Note, onTogglePin: () -> Unit, onDelete: () -> Unit, onEdit: () -> Unit = {}) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd/MM/yy HH:mm", Locale.FRANCE)

    // Entrance scale animation
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appeared = true }
    val scale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.8f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "noteScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(400),
        label = "noteAlpha"
    )

    Card(
        onClick = onEdit,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .animateContentSize(spring(dampingRatio = 0.8f)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(note.color))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (note.isPinned) {
                    Icon(
                        Icons.Filled.PushPin,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF5D4037)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Row {
                    IconButton(onClick = onTogglePin, modifier = Modifier.size(28.dp)) {
                        Icon(
                            if (note.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Épingler",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF5D4037).copy(alpha = 0.6f)
                        )
                    }
                    IconButton(onClick = { showDeleteConfirm = true }, modifier = Modifier.size(28.dp)) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Supprimer",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF5D4037).copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF3E2723),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (note.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF5D4037),
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = dateFormat.format(Date(note.updatedAt)),
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF5D4037).copy(alpha = 0.5f)
            )
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Supprimer cette note ?") },
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
fun AddNoteDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedColorIndex by remember { mutableIntStateOf(0) }

    val noteColors = listOf(
        NoteYellow,
        NoteGreen,
        NoteBlue,
        NotePink,
        NoteOrange,
        NotePurple
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouvelle note", fontWeight = FontWeight.Bold) },
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
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Contenu") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp)
                )
                Text("Couleur", style = MaterialTheme.typography.labelLarge)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    noteColors.forEachIndexed { index, color ->
                        FilterChip(
                            selected = selectedColorIndex == index,
                            onClick = { selectedColorIndex = index },
                            label = { Text("") },
                            modifier = Modifier.size(36.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = color,
                                selectedContainerColor = color
                            ),
                            border = if (selectedColorIndex == index)
                                FilterChipDefaults.filterChipBorder(
                                    borderColor = MaterialTheme.colorScheme.primary,
                                    borderWidth = 2.dp,
                                    enabled = true,
                                    selected = true
                                )
                            else null
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val colorInt = noteColors[selectedColorIndex].value.toInt()
                        onConfirm(title, content, colorInt)
                    }
                },
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Créer") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun EditNoteDialog(
    note: Note,
    onDismiss: () -> Unit,
    onConfirm: (Note) -> Unit
) {
    var title by remember { mutableStateOf(note.title) }
    var content by remember { mutableStateOf(note.content) }

    val noteColors = listOf(NoteYellow, NoteGreen, NoteBlue, NotePink, NoteOrange, NotePurple)
    var selectedColorIndex by remember {
        mutableIntStateOf(noteColors.indexOfFirst { it.value.toInt() == note.color }.coerceAtLeast(0))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifier la note", fontWeight = FontWeight.Bold) },
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
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Contenu") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 5
                )
                Text("Couleur", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    noteColors.forEachIndexed { index, color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(color)
                                .clickable { selectedColorIndex = index }
                                .then(
                                    if (selectedColorIndex == index)
                                        Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                                    else Modifier
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
                        onConfirm(note.copy(
                            title = title,
                            content = content,
                            color = noteColors[selectedColorIndex].value.toInt(),
                            updatedAt = System.currentTimeMillis()
                        ))
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
