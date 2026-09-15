package com.prepcommerce.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun LoadingState(message: String = "Loading...") {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
            Text(message)
        }
    }
}

@Composable
fun EmptyState(message: String = "No content available yet.") {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.Info, contentDescription = null, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(12.dp))
            Text(message, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
fun SimpleTopBar(title: String, onBack: (() -> Unit)? = null) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
            }
        }
    )
}

@Composable
fun DifficultyChip(difficulty: String) {
    val color = when (difficulty) {
        "EASY" -> Color(0xFF2E7D32)
        "HARD" -> Color(0xFFC62828)
        else -> Color(0xFFEF6C00)
    }
    AssistChip(onClick = {}, label = { Text(difficulty) }, colors = AssistChipDefaults.assistChipColors(labelColor = color))
}

@Composable
fun StatBox(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun OptionRow(
    text: String, index: Int, selected: Boolean, showResult: Boolean,
    isCorrectOption: Boolean, onClick: () -> Unit
) {
    val bg = when {
        !showResult && selected -> MaterialTheme.colorScheme.primaryContainer
        showResult && isCorrectOption -> Color(0xFFA5D6A7)
        showResult && selected && !isCorrectOption -> Color(0xFFEF9A9A)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        onClick = onClick
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("${('A' + index)}. ", fontWeight = FontWeight.Bold)
            Text(text)
        }
    }
}
