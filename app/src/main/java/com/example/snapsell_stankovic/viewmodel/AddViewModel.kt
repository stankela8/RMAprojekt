package com.example.snapsell_stankovic.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.snapsell_stankovic.model.Oglas
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class AddViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _status = MutableStateFlow("")
    val status: StateFlow<String> = _status

    fun resetStatus() {
        _status.value = ""
    }
    fun uploadOglas(
        ime: String,
        cijena: String,
        kategorija: String,
        opis: String,
        imageUri: Uri,
        context: Context
    ) {
        _status.value = "loading"

        val currentUser = auth.currentUser
        val uid = currentUser?.uid ?: run {
            _status.value = "error"
            Toast.makeText(context, "Korisnik nije prijavljen", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val imeVlasnika = document.getString("ime") ?: "Nepoznat"

                val fileName = "images/${UUID.randomUUID()}.jpg"
                val imageRef = storage.reference.child(fileName)

                imageRef.putFile(imageUri)
                    .continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let { throw it }
                        }
                        imageRef.downloadUrl
                    }
                    .addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        // Ispravak: Uklonjeno 'id = it.id' jer 'it' ovdje referira na URI slike
                        val oglas = Oglas(
                            ime = ime,
                            cijena = cijena,
                            kategorija = kategorija,
                            opis = opis,
                            imageUrl = imageUrl,
                            vlasnik = currentUser.uid, // Spremamo UID umjesto imena
                            id = ""
                        )


                        db.collection("oglasi").add(oglas)
                            .addOnSuccessListener { documentReference ->
                                // Ovdje možete dohvatiti ID novog dokumenta ako je potrebno
                                val docId = documentReference.id
                                _status.value = "success"
                            }
                            .addOnFailureListener {
                                _status.value = "error"
                                Toast.makeText(context, "Greška pri spremanju oglasa", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener {
                        _status.value = "error"
                        Toast.makeText(context, "Greška pri uploadu slike", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                _status.value = "error"
                Toast.makeText(context, "Ne mogu dohvatiti korisnika", Toast.LENGTH_SHORT).show()
            }
    }

}
