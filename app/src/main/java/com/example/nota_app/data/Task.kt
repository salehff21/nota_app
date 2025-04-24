package com.example.nota_app.data
data class Task(
    val id: Int = 0,
    val title: String,
    val priority: String,
    val date: String,
    val time: String,
    val isCompleted: Boolean = false,
    val status: Boolean = false
)
