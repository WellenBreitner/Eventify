package com.example.eventify.ModelData
// only use for seat setup not for database
data class SeatModelData(
    val label: String,
    val row: String,
    val number: Int,
    var isSelected: Boolean = false
)
