package com.example.multiplatformmaps.android.database

data class Mark (
    val id: Long,
    val latitude: String,
    val longitude: String,
    val name: String,
    val description: String,
    val factTitle: String,
    val factText: String
)