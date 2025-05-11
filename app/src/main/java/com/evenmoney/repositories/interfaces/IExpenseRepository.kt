package com.evenmoney.repositories.interfaces

import com.evenmoney.models.Expense

interface IExpenseRepository {
    fun addExpense(
        groupId: String,
        title: String,
        amount: Double,
        payer: String,
        notes: String,
        distributions: Map<String, Double>,
        callback: (Boolean, String?) -> Unit
    )

    fun readExpenses(groupId: String, callback: (Boolean, List<Expense>) -> Unit)

    fun readExpense(groupId: String, title: String, callback: (Boolean, Expense) -> Unit)

    fun updateExpense(groupId: String, expense: Expense, callback: (Boolean, String?) -> Unit)
}