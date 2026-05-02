package com.example.ry0000tarodojo2026.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SearchHeaderCard(
    initialQuery: String,
    initialMinutes: String,
    initialExerciseType: String,
    isLoading: Boolean,
    onSearch: (String, Long, String) -> Unit
) {
    var query by remember { mutableStateOf(initialQuery) }
    var minutes by remember { mutableStateOf(initialMinutes) }
    var exerciseType by remember { mutableStateOf(initialExerciseType) }
    var expanded by remember { mutableStateOf(false) }
    val exerciseOptions = listOf("選択なし", "スマホを振る")

    LaunchedEffect(initialQuery, initialMinutes, initialExerciseType) {
        query = initialQuery
        minutes = initialMinutes
        exerciseType = initialExerciseType
    }

    ElevatedCard(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("タイマー設定", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("検索") }, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(value = minutes, onValueChange = { minutes = it }, label = { Text("分") }, modifier = Modifier.width(70.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            @OptIn(ExperimentalMaterial3Api::class)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = exerciseType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("運動 (Exercise)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    exerciseOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                exerciseType = option
                                expanded = false
                            }
                        )
                    }
                }
            }
            Button(
                onClick = { onSearch(query, (minutes.toLongOrNull() ?: 3L) * 60, exerciseType) },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                enabled = !isLoading
            ) {
                Text(if (isLoading) "動画を探しています..." else "動画を検索")
            }
        }
    }
}