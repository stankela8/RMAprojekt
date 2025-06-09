package com.example.snapsell_stankovic.viewmodel

import android.content.Context
import android.widget.Toast
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
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authStatus.value = "success"
                } else {
                    _authStatus.value = task.exception?.message ?: "Greška pri prijavi"
                }
            }
    }

    fun signUp(email: String, password: String, ime: String, prezime: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
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
                } else {
                    _authStatus.value = task.exception?.message ?: "Greška pri registraciji"
                }
            }
    }

    fun logout(context: Context) {
        auth.signOut()
        _authStatus.value = "logged_out"
        Toast.makeText(context, "Uspješno ste odjavljeni", Toast.LENGTH_SHORT).show()
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null
}
