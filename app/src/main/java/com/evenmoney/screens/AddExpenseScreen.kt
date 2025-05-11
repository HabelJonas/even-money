package com.evenmoney.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.evenmoney.repositories.interfaces.IExpenseRepository
import com.evenmoney.repositories.interfaces.IGroupRepository
import com.evenmoney.repositories.mocks.FakeExpenseRepository
import com.evenmoney.repositories.mocks.FakeGroupRepository

@Composable
fun AddExpenseScreen(
    navController: NavController,
    groupId: String,
    groupRepository: IGroupRepository,
    expenseRepository: IExpenseRepository
) {
    var expenseTitle by remember { mutableStateOf("") }
    var euroAmount by remember { mutableStateOf("") }
    var selectedPayer by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    var members by remember { mutableStateOf<List<String>>(emptyList()) }
    var memberDistributions by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    var memberSelections by remember { mutableStateOf<Map<String,Boolean>>(emptyMap()) }

    LaunchedEffect(Unit) {
        groupRepository.readGroupMembers(groupId) { success, list ->
            if (success) {
                members = list
                for (member in members) {
                    memberDistributions =
                        memberDistributions.toMutableMap().apply { this[member] = 0.0 }
                    memberSelections =
                        memberSelections.toMutableMap().apply { this[member] = true }
                }
            }
        }
    }

    //  val memberDistributions = remember {
    //      mutableStateMapOf<String, Double>().apply {
    //          members.forEach { this[it] = 0.0 } // Initialize all members as selected
    //      }
    //  }
//
//    val memberSelections = remember {
////        mutableStateMapOf<String, Boolean>().apply {
////            members.forEach { this[it] = true }
////        }
////    }

    LaunchedEffect(euroAmount, selectedPayer, memberSelections.toMap()) {
        val amount = euroAmount.toDoubleOrNull() ?: 0.0
        val selectedMembers = memberSelections.count { it.value }

        members.forEach { member ->
            memberDistributions = memberDistributions.toMutableMap().apply {
                when {
                    !memberSelections[member]!! -> this[member] = 0.0
                    selectedMembers > 0 -> this[member] = amount / selectedMembers
                    else -> this[member] = 0.0
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Add Expense")
            // Expense Title
            OutlinedTextField(
                value = expenseTitle,
                onValueChange = { expenseTitle = it },
                label = { Text("Expense Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Amount in Euro and Cent
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = euroAmount,
                    onValueChange = {
                        euroAmount = it.take(8).filter { it.isDigit() || it == '.' }
                            .replace(Regex("^(\\d*\\.?\\d{0,2}).*"), "$1")
                    },
                    label = { Text("Euro") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("€")
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Payer Dropdown
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isDropdownExpanded = !isDropdownExpanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedPayer.isEmpty()) "Select Payer" else selectedPayer,
                    modifier = Modifier.weight(1f)
                )
                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Dropdown")
                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    members.forEach { member ->
                        DropdownMenuItem(
                            text = { Text(text = member) },
                            onClick = {
                                selectedPayer = member
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Distribution Checkboxes
            Text(text = "Distribution")

            members.forEach { member ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = memberSelections[member] == true,
                        onCheckedChange = { isChecked ->
                            memberSelections = memberSelections.toMutableMap().apply { this[member]= isChecked }
                        }
                    )
                    Text(text = member)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${"%.2f".format(memberDistributions[member])} €", // Display distribution amount
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    expenseRepository.addExpense(groupId,expenseTitle,euroAmount.toDoubleOrNull() ?: 0.0,selectedPayer,notes,memberDistributions){ success, error ->
                        if(success){
                            navController.navigate("expenses_and_balances/$groupId")
                        }

                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            ) {
                Text("Add Expense")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddExpenseScreenPreview() {
    AddExpenseScreen(navController = rememberNavController(), "", FakeGroupRepository(),
        FakeExpenseRepository())
}