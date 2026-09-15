package com.prepcommerce.app.ui.subject

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prepcommerce.app.navigation.Screen
import com.prepcommerce.app.ui.home.SUBJECTS

@Composable
fun SubjectListScreen(navController: NavController) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Choose a Subject", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(SUBJECTS) { subject ->
                Card(onClick = { navController.navigate(Screen.Chapters.build(subject)) }) {
                    Box(Modifier.fillMaxWidth().padding(24.dp)) { Text(subject) }
                }
            }
        }
    }
}
