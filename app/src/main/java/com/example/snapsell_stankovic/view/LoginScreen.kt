package com.example.snapsell_stankovic.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.snapsell_stankovic.viewmodel.AuthViewModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var ime by remember { mutableStateOf("") }
    var prezime by remember { mutableStateOf("") }
    var isSignup by remember { mutableStateOf(false) }

    val authStatus by authViewModel.authStatus.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Snap & Sell",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isSignup) {
                AuthInputField(value = ime, onValueChange = { ime = it }, label = "Ime")
                Spacer(modifier = Modifier.height(8.dp))
                AuthInputField(value = prezime, onValueChange = { prezime = it }, label = "Prezime")
                Spacer(modifier = Modifier.height(8.dp))
            }

            AuthInputField(value = email, onValueChange = { email = it }, label = "Email")
            Spacer(modifier = Modifier.height(8.dp))
            AuthInputField(value = password, onValueChange = { password = it }, label = "Lozinka", isPassword = true)

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (isSignup) {
                        authViewModel.signUp(email, password, ime, prezime)
                    } else {
                        authViewModel.login(email, password)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(if (isSignup) "Registriraj se" else "Prijavi se")
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = { isSignup = !isSignup }) {
                Text(
                    text = if (isSignup) "Imaš račun? Prijavi se" else "Nemaš račun? Registriraj se",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (authStatus == "success") {
                LaunchedEffect(Unit) {
                    onLoginSuccess()
                }
            } else if (authStatus.isNotEmpty() && authStatus != "success") {
                Text(
                    text = authStatus,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}

@Composable
fun AuthInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
        )
    )
}
