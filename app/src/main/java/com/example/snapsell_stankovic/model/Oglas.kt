package com.example.snapsell_stankovic.model

import java.io.Serializable

data class Oglas(
    val ime: String = "",
    val cijena: String = "",
    val kategorija: String = "",
    val opis: String = "",
    val imageUrl: String = "",
    val vlasnik: String = ""
) : Serializable
