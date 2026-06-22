package ge.btu.habittracker.ui.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ge.btu.habittracker.data.model.Habit
import ge.btu.habittracker.data.util.DateUtils
import ge.btu.habittracker.ui.components.habitColors
import ge.btu.habittracker.ui.components.habitIcons
import ge.btu.habittracker.ui.components.iconForKey
import ge.btu.habittracker.ui.components.parseColor

/**
 * Form for creating a new habit or editing an existing one. When [existing] is non-null
 * its id, creation time and completion history are preserved on save.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditHabitScreen(
    existing: Habit?,
    onSave: (Habit) -> Unit,
    onCancel: () -> Unit,
) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var iconKey by remember { mutableStateOf(existing?.iconKey ?: habitIcons.first().key) }
    var colorHex by remember { mutableStateOf(existing?.colorHex ?: habitColors.first()) }
    var reminderEnabled by remember { mutableStateOf(existing?.reminderEnabled ?: false) }
    var reminderHour by remember { mutableIntStateOf(existing?.reminderHour ?: 9) }
    var reminderMinute by remember { mutableIntStateOf(existing?.reminderMinute ?: 0) }
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existing == null) "New habit" else "Edit habit") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (optional)") },
                modifier = Modifier.fillMaxWidth(),
            )

            Text("Icon", style = MaterialTheme.typography.titleSmall)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(habitIcons, key = { it.key }) { option ->
                    SelectableCircle(
                        selected = option.key == iconKey,
                        background = parseColor(colorHex).copy(alpha = 0.18f),
                        ring = parseColor(colorHex),
                        onClick = { iconKey = option.key },
                    ) {
                        Icon(option.icon, contentDescription = option.key, tint = parseColor(colorHex))
                    }
                }
            }

            Text("Color", style = MaterialTheme.typography.titleSmall)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(habitColors, key = { it }) { hex ->
                    SelectableCircle(
                        selected = hex == colorHex,
                        background = parseColor(hex),
                        ring = MaterialTheme.colorScheme.onSurface,
                        onClick = { colorHex = hex },
                    ) {
                        if (hex == colorHex) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }

            ReminderSection(
                enabled = reminderEnabled,
                onToggle = { reminderEnabled = it },
                timeLabel = DateUtils.formatTime(reminderHour, reminderMinute),
                onPickTime = { showTimePicker = true },
            )

            Spacer(Modifier.width(0.dp))
            Button(
                onClick = {
                    val habit = (existing ?: Habit()).copy(
                        name = name.trim(),
                        description = description.trim(),
                        iconKey = iconKey,
                        colorHex = colorHex,
                        reminderEnabled = reminderEnabled,
                        reminderHour = reminderHour,
                        reminderMinute = reminderMinute,
                    )
                    onSave(habit)
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (existing == null) "Create habit" else "Save changes")
            }
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            initialHour = reminderHour,
            initialMinute = reminderMinute,
            onConfirm = { hour, minute ->
                reminderHour = hour
                reminderMinute = minute
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false },
        )
    }
}

@Composable
private fun SelectableCircle(
    selected: Boolean,
    background: Color,
    ring: Color,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(background)
            .then(
                if (selected) Modifier.border(3.dp, ring, CircleShape) else Modifier,
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
private fun ReminderSection(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    timeLabel: String,
    onPickTime: () -> Unit,
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Daily reminder", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Get a local notification every day",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(checked = enabled, onCheckedChange = onToggle)
            }
            if (enabled) {
                Spacer(Modifier.width(0.dp))
                TextButton(onClick = onPickTime) {
                    Text("Time:  $timeLabel")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true,
    )
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(state.hour, state.minute) }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        text = { TimePicker(state = state) },
    )
}
