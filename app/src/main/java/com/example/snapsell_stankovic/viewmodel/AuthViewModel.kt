package com.example.snapsell_stankovic.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.snapsell_stankovic.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _authStatus = MutableStateFlow("")
    val authStatus: StateFlow<String> = _authStatus

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _authStatus.value = "success"
            }
            .addOnFailureListener {
                _authStatus.value = it.message ?: "Greška pri prijavi"
            }
    }

    fun signUp(email: String, password: String, ime: String, prezime: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = it.user?.uid ?: return@addOnSuccessListener
                val korisnik = User(uid = uid, email = email, ime = ime, prezime = prezime)

                firestore.collection("users")
                    .document(uid)
                    .set(korisnik)
                    .addOnSuccessListener {
                        _authStatus.value = "success"
                    }
                    .addOnFailureListener { e ->
                        _authStatus.value = "Greška pri spremanju korisnika: ${e.message}"
                    }
            }
            .addOnFailureListener {
                _authStatus.value = it.message ?: "Greška pri registraciji"
            }
    }

    fun getCurrentUserEmail(): String? = auth.currentUser?.email

    fun logout() {
        auth.signOut()
    }
}
