package com.example.nota_app.data
data class Task(
    val id: Int =0,
    val title: String,
    val priority: String,
    val date: String,
    val time: String,
    var isCompleted: Boolean = false,
    var status: Boolean = false
)
