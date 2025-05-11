package com.evenmoney.repositories.mocks

import com.evenmoney.repositories.interfaces.IExpenseRepository
import com.evenmoney.models.Expense

class FakeExpenseRepository : IExpenseRepository{
    override fun addExpense(
        groupId: String,
        title: String,
        amount: Double,
        payer: String,
        notes: String,
        distributions: Map<String, Double>,
        callback: (Boolean, String?) -> Unit
    ) {
        callback(true, null)
    }

    override fun readExpenses(
        groupId: String,
        callback: (Boolean, List<Expense>) -> Unit
    ) {
        callback(true, emptyList())
    }

    override fun readExpense(
        groupId: String,
        title: String,
        callback: (Boolean, Expense) -> Unit
    ) {
        callback(true, Expense())
    }

    override fun updateExpense(
        groupId: String,
        expense: Expense,
        callback: (Boolean, String?) -> Unit
    ) {
        callback(true,"")
    }
}