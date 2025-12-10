package com.example.core.presentation.ui.model

data class RunUi(
    val id: String,
    val duration: String,
    val dateTime: String,
    val distance: String,
    val pace: String,
    val avgSpeed: String,
    val maxSpeed: String,
    val totalElevation: String,
    val mapPictureUrl: String?,
)
