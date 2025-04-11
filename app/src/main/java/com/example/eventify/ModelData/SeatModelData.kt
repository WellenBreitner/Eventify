package com.example.eventify.ModelData

data class SeatModelData(
    val label: String,
    val row: String,
    val number: Int,
    var isSelected: Boolean = false
)
