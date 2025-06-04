package com.example.snapsell_stankovic.view

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.snapsell_stankovic.viewmodel.AddViewModel
import java.io.File

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

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var photoFile by remember { mutableStateOf<File?>(null) }

    val kategorije = listOf("Elektronika", "Odjeća", "Namještaj", "Ostalo")
    var expanded by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) {
            imageUri = null
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val file = File.createTempFile("temp_image", ".jpg", context.cacheDir).apply {
                createNewFile()
                deleteOnExit()
            }
            photoFile = file
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            imageUri = uri
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
        imageUri = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(value = ime, onValueChange = { ime = it }, label = { Text("Ime artikla") }, modifier = Modifier.fillMaxWidth())

        TextField(
            value = cijena,
            onValueChange = { cijena = it },
            label = { Text("Cijena (€)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            TextField(
                value = kategorija,
                onValueChange = {},
                readOnly = true,
                label = { Text("Kategorija") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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

        TextField(value = opis, onValueChange = { opis = it }, label = { Text("Opis") }, modifier = Modifier.fillMaxWidth())

        Button(
            onClick = {
                val permissionGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                if (permissionGranted) {
                    val file = File.createTempFile("temp_image", ".jpg", context.cacheDir).apply {
                        createNewFile()
                        deleteOnExit()
                    }
                    photoFile = file
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                    imageUri = uri
                    cameraLauncher.launch(uri)
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Uslikaj artikl")
        }

        imageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(model = uri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp)
            )
        }

        Button(
            onClick = {
                if (imageUri != null) {
                    viewModel.uploadOglas(
                        ime = ime,
                        cijena = cijena,
                        kategorija = kategorija,
                        opis = opis,
                        imageUri = imageUri!!,
                        context = context
                    )
                } else {
                    Toast.makeText(context, "Uslikaj artikl prije dodavanja", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = imageUri != null,
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
