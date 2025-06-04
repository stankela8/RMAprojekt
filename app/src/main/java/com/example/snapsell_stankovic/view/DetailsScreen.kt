package com.example.snapsell_stankovic.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.snapsell_stankovic.model.Oglas

@Composable
fun DetailsScreen(navController: NavHostController) {
    val oglas = navController.previousBackStackEntry
        ?.savedStateHandle?.get<Oglas>("oglas")

    if (oglas != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(oglas.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Text(text = oglas.ime, style = MaterialTheme.typography.headlineSmall)
            Text(text = "Cijena: ${oglas.cijena} €", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Kategorija: ${oglas.kategorija}")
            Text(text = "Opis: ${oglas.opis}")
            Text(text = "Vlasnik: ${oglas.vlasnik}", fontStyle = FontStyle.Italic)
        }
    } else {
        Text("Greška: Oglas nije pronađen", color = MaterialTheme.colorScheme.error)
    }
}
