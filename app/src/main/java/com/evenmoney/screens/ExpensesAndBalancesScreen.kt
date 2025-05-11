package com.evenmoney.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.evenmoney.models.Expense
import com.evenmoney.repositories.AuthRepository
import com.evenmoney.repositories.GroupRepository
import com.evenmoney.repositories.interfaces.IAuthRepository
import com.evenmoney.repositories.interfaces.IExpenseRepository
import com.evenmoney.repositories.interfaces.IGroupRepository
import com.evenmoney.repositories.mocks.FakeAuthRepository
import com.evenmoney.repositories.mocks.FakeExpenseRepository
import com.evenmoney.repositories.mocks.FakeGroupRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ExpensesAndBalancesScreen(
    navController: NavController,
    groupId: String?,
    expenseRepository: IExpenseRepository,
    groupRepository: IGroupRepository,
    authRepository: IAuthRepository
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Expenses", "Balances")


    Scaffold(
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(onClick = {
                    navController.navigate("add_expense/${groupId}")
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Expense")
                }
            }
        },
        content = {

            Column(modifier = Modifier.fillMaxSize().padding(top = 15.dp)) {
                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }
                when (selectedTab) {
                    0 -> ExpensesTab(navController, groupId.toString(), expenseRepository)
                    1 -> BalancesTab(groupId.toString(), expenseRepository, groupRepository, authRepository)
                }
            }
        }
    )
}

@Composable
fun ExpensesTab(
    navController: NavController,
    groupId: String,
    expenseRepository: IExpenseRepository
) {
    var groupExpenses by remember { mutableStateOf<List<Expense>>(emptyList()) }

    LaunchedEffect(Unit) {
        expenseRepository.readExpenses(groupId) { success, expenses ->
            if (success) {
                groupExpenses = expenses
            }
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        groupExpenses.forEach { expense ->
            item(key = expense.title) {
                ExpenseCard(
                    expense = expense,
                    onClick = {
                        navController.navigate("expense_details/${groupId}/${expense.title}")
                    })
            }
        }
    }
}

@Composable
fun ExpenseCard(expense: Expense, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = expense.title, fontSize = 18.sp)
                Text(text = "Paid by: ${expense.paidBy}", fontSize = 14.sp)
            }
            Text(text = "$${expense.amount}", fontSize = 18.sp)
        }
    }
}

@Composable
fun BalancesTab(
    groupId: String, expenseRepository: IExpenseRepository,
    groupRepository: IGroupRepository,
    authRepository: IAuthRepository
) {
    var groupExpenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var groupMembers by remember { mutableStateOf<List<String>>(emptyList()) }
    var groupBalances by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    var currentUserName by remember { mutableStateOf<String>("")}

    LaunchedEffect(Unit) {
        expenseRepository.readExpenses(groupId) { success, expenses ->
            if (success) {
                groupExpenses = expenses
                groupBalances = calculateBalances(groupExpenses)
            }
        }
        groupRepository.readGroupMembers(groupId) { success, members ->
            if (success) {
                groupMembers = members
            }
        }
        var userId = FirebaseAuth.getInstance().currentUser?.uid
        authRepository.readUserName(userId.toString()) { success, username ->
            if(success){
                currentUserName = username
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        var balancesText: String = if((groupBalances[currentUserName]?.toDouble() ?: 0.0) > 0.0){
            "You (${currentUserName}) get ${groupBalances[currentUserName]?.toDouble()}€"
        } else {
            "You (${currentUserName}) owe ${groupBalances[currentUserName]?.toDouble()}€ "
        }
        Text(text = balancesText,
            fontSize = 25.sp,
            modifier = Modifier.padding(top = 30.dp,start = 15.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            groupBalances.forEach { balanceEntry ->
                item() {
                    BalanceCard(balanceEntry.key, balanceEntry.value)
                }
            }
        }
    }
}

public fun CoroutineScope.calculateBalances(expenses: List<Expense>): Map<String, Double> {
    var balances = mutableListOf<Map<String, Double>>()
    var overallBalances = mutableMapOf<String, Double>()

    for (expense in expenses) {
        var map = mutableMapOf<String, Double>()
        for (entry in expense.distributions) {
            var amount: Double = if (entry.key == expense.paidBy) {
                expense.amount
            } else {
                0.0
            }
            amount -= entry.value
            map[entry.key] = amount
        }
        balances.add(map)
    }

    for (balance in balances) {
        for (entry in balance) {
            if (!overallBalances.containsKey(entry.key)) {
                overallBalances[entry.key] = entry.value
            } else {
                overallBalances[entry.key] = overallBalances[entry.key]!!.toDouble() + entry.value
            }
        }
    }

    return overallBalances
}

@Composable
fun BalanceCard(name: String, balance: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontSize = 18.sp)
            }
            Text(text = "$balance €", fontSize = 18.sp)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ExpensesAndBalancesScreenPreview() {
    ExpensesAndBalancesScreen(
        navController = rememberNavController(), "", FakeExpenseRepository(),
        FakeGroupRepository(), FakeAuthRepository()
    )
}
