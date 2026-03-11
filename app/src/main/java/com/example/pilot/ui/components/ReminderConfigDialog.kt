package com.example.pilot.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pilot.data.model.ReminderOffset

@Composable
fun ReminderConfigDialog(
    onDismiss: () -> Unit,
    onConfirm: (ReminderOffset) -> Unit
) {
    var selectedOffset by remember { mutableStateOf(ReminderOffset.FIFTEEN_MIN) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Configurer le rappel",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    "Quand souhaitez-vous etre rappele ?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                ReminderOffset.entries.forEach { offset ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedOffset == offset,
                            onClick = { selectedOffset = offset }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = offset.label,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedOffset) },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Confirmer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
