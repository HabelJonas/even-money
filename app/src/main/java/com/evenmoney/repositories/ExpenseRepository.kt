package com.evenmoney.repositories

import com.evenmoney.repositories.interfaces.IExpenseRepository
import com.evenmoney.models.Expense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class ExpenseRepository : IExpenseRepository {
    private val db = FirebaseFirestore.getInstance()

    override fun addExpense(
        groupId: String,
        title: String,
        amount: Double,
        payer: String,
        notes: String,
        distributions: Map<String, Double>,
        callback: (Boolean, String?) -> Unit
    ) {
        val createdBy = FirebaseAuth.getInstance().currentUser?.uid
        val createdAt = Date()

        val expenseData = hashMapOf(
            "title" to title,
            "amount" to amount,
            "payer" to payer,
            "notes" to notes,
            "distributions" to distributions,
            "createdBy" to createdBy,
            "createdAt" to createdAt
        )

        db.collection("groups").document(groupId)
            .update("expenses", FieldValue.arrayUnion(expenseData))
            .addOnSuccessListener {
                callback(true, null)
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }

    override fun readExpenses(
        groupId: String,
        callback: (Boolean, List<Expense>) -> Unit
    ) {
        db.collection("groups").document(groupId)
            .get()
            .addOnSuccessListener { docSnapshot ->
                if (docSnapshot != null) {
                    var expensesList = mutableListOf<Expense>()
                    val expenses = docSnapshot.get("expenses") as? List<Map<String, Any>>
                    expenses?.forEach { expenseMap ->
                        val title = expenseMap["title"] as? String ?: ""
                        val payer = expenseMap["payer"] as? String ?: ""
                        val amount = (expenseMap["amount"] as? Number)?.toDouble() ?: 0.0
                        val distributions = expenseMap["distributions"] as? Map<String,Double> ?: emptyMap()

                        val expense = Expense(
                            title = title,
                            paidBy = payer,
                            amount = amount,
                            distributions = distributions
                        )
                        expensesList.add(expense)
                    }
                    callback(true, expensesList)
                }

            }
            .addOnFailureListener { e ->
                callback(false, emptyList())
            }
    }

    override fun readExpense(
        groupId: String,
        title: String,
        callback: (Boolean, Expense) -> Unit
    ) {
        db.collection("groups").document(groupId)
            .get()
            .addOnSuccessListener { docSnapshot ->
                if (docSnapshot != null) {
                    val expenses = docSnapshot.get("expenses") as? List<Map<String, Any>>
                    expenses?.forEach { expenseMap ->
                        val expenseTitle = expenseMap["title"] as? String ?: ""
                        if (expenseTitle == title) {
                            val payer = expenseMap["payer"] as? String ?: ""
                            val expenseTitle = expenseMap["title"] as? String ?: ""
                            val amount = (expenseMap["amount"] as? Number)?.toDouble() ?: 0.0
                            val distributions =
                                expenseMap["distributions"] as? Map<String, Double> ?: emptyMap()
                            val notes = expenseMap["notes"] as? String ?: ""

                            callback(true, Expense(expenseTitle,payer,amount,distributions,notes))
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                callback(false, Expense())
            }
    }

    override fun updateExpense(
        groupId: String,
        expense: Expense,
        callback: (Boolean, String?) -> Unit
    ) {
        val groupRef = db.collection("groups").document(groupId)
        groupRef.get()
            .addOnSuccessListener { docSnapshot ->
                if(docSnapshot.exists()) {
                    val expenses = docSnapshot.get("expenses") as? ArrayList<Map<String, Any>>
                    if(expenses != null){
                        val updatedExpenses = expenses.map { readExpense ->
                            if(expense.title == readExpense["title"]){
                                readExpense.toMutableMap().apply {
                                    this["payer"] = expense.paidBy
                                    this["amount"] = expense.amount
                                    this["distributions"] = expense.distributions
                                    this["notes"] = expense.notes
                                }
                            } else {
                                readExpense
                            }
                        }
                        groupRef.update("expenses", updatedExpenses)
                            .addOnSuccessListener {
                                callback(true, "updated successfully")
                            }
                            .addOnFailureListener { e ->
                                callback(false, e.message)
                            }
                    }

                }
            }
            .addOnFailureListener {
                e -> callback(false, e.message)
            }
    }
}