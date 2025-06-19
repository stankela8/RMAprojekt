package com.example.snapsell_stankovic.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.snapsell_stankovic.model.Oglas
import com.example.snapsell_stankovic.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    onNavigateToDetails: (Oglas) -> Unit
) {
    val viewModel: HomeViewModel = viewModel()
    val oglasi by viewModel.oglasi.collectAsState()
    val kategorije = viewModel.kategorije

    var expanded by remember { mutableStateOf(false) }
    var selectedKategorija by remember { mutableStateOf("Sve") }

    LaunchedEffect(selectedKategorija) {
        viewModel.listenToOglasi(selectedKategorija)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Dropdown filter
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedKategorija,
                onValueChange = {},
                readOnly = true,
                label = { Text("Filtriraj po kategoriji") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                kategorije.forEach { kategorija ->
                    DropdownMenuItem(
                        text = { Text(kategorija) },
                        onClick = {
                            selectedKategorija = kategorija
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(oglasi) { oglas ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onNavigateToDetails(oglas)
                        },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        Image(
                            painter = rememberAsyncImagePainter(oglas.imageUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .padding(end = 12.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Column {
                            Text(text = oglas.ime, style = MaterialTheme.typography.titleMedium)
                            Text(text = oglas.kategorija, style = MaterialTheme.typography.bodySmall)
                            Text(text = "${oglas.cijena} €", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
