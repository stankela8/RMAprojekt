package com.example.snapsell_stankovic.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.snapsell_stankovic.viewmodel.AddViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen() {
    val context = LocalContext.current
    val viewModel: AddViewModel = viewModel()
    val status by viewModel.status.collectAsState()

    var ime by remember { mutableStateOf("") }
    var cijena by remember { mutableStateOf("") }
    var kategorija by remember { mutableStateOf("") }
    var opis by remember { mutableStateOf("") }

    val kategorije = listOf("Elektronika", "Odjeća", "Kozmetika","Obuća", "Ostalo")
    var expanded by remember { mutableStateOf(false) }

    var capturedImageUri by remember { mutableStateOf<Uri>(Uri.EMPTY) }
    var photoFile by remember { mutableStateOf<File?>(null) }

    // Funkcija za kreiranje slike
    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        return File.createTempFile(
            imageFileName, ".jpg", context.externalCacheDir
        )
    }

    // Pripremi file
    val file = remember { createImageFile(context) }
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            capturedImageUri = uri
            photoFile = file
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Dozvola za kameru je odbijena", Toast.LENGTH_SHORT).show()
        }
    }

    if (status == "success") {
        Toast.makeText(context, "Oglas uspješno dodan", Toast.LENGTH_SHORT).show()
        viewModel.resetStatus()
        ime = ""
        cijena = ""
        kategorija = ""
        opis = ""
        capturedImageUri = Uri.EMPTY
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = ime,
            onValueChange = { ime = it },
            label = { Text("Ime artikla") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = cijena,
            onValueChange = { cijena = it },
            label = { Text("Cijena (€)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = kategorija,
                onValueChange = {},
                readOnly = true,
                label = { Text("Kategorija") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                kategorije.forEach { kat ->
                    DropdownMenuItem(
                        text = { Text(kat) },
                        onClick = {
                            kategorija = kat
                            expanded = false
                        }
                    )
                }
            }
        }

        TextField(
            value = opis,
            onValueChange = { opis = it },
            label = { Text("Opis") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (capturedImageUri != Uri.EMPTY) {
                Image(
                    painter = rememberImagePainter(capturedImageUri),
                    contentDescription = "Uslikana fotografija",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { capturedImageUri = Uri.EMPTY },
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Ukloni sliku",
                        tint = Color.White
                    )
                }
            }

            Button(
                onClick = {
                    val permissionGranted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                    if (permissionGranted) {
                        cameraLauncher.launch(uri)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text("Uslikaj artikl")
            }
        }

        Button(
            onClick = onClick@{
                if (capturedImageUri == Uri.EMPTY) {
                    Toast.makeText(context, "Uslikajte artikl", Toast.LENGTH_SHORT).show()
                    return@onClick
                }

                if (ime.isBlank()) {
                    Toast.makeText(context, "Unesite ime artikla", Toast.LENGTH_SHORT).show()
                    return@onClick
                }

                if (cijena.isBlank() || cijena.toDoubleOrNull() == null) {
                    Toast.makeText(context, "Unesite ispravnu cijenu", Toast.LENGTH_SHORT).show()
                    return@onClick
                }

                if (kategorija.isBlank()) {
                    Toast.makeText(context, "Odaberite kategoriju", Toast.LENGTH_SHORT).show()
                    return@onClick
                }

                if (opis.isBlank()) {
                    Toast.makeText(context, "Unesite opis", Toast.LENGTH_SHORT).show()
                    return@onClick
                }

                viewModel.uploadOglas(
                    ime = ime,
                    cijena = cijena,
                    kategorija = kategorija,
                    opis = opis,
                    imageUri = capturedImageUri,
                    context = context
                )
            },
            enabled = capturedImageUri != Uri.EMPTY,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Dodaj oglas")
        }

        if (status == "error") {
            Text("Greška pri dodavanju!", color = MaterialTheme.colorScheme.error)
        }
    }
}
