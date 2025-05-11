package com.evenmoney.models

data class Expense(
    val title: String = "",
    val paidBy: String = "",
    val amount: Double = 0.0,
    val distributions: Map<String, Double> = emptyMap(),
    val notes: String = ""
)