package com.evenmoney.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.evenmoney.repositories.mocks.FakeGroupRepository
import com.evenmoney.repositories.interfaces.IGroupRepository

@Composable
fun CreateGroupScreen(navController: NavController, groupRepository: IGroupRepository) {
    var groupName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create Group",
            fontSize = 35.sp,
            modifier = Modifier.padding(top = 80.dp)
        )

        OutlinedTextField(
            value = groupName,
            onValueChange = { groupName = it },
            label = { Text("Group Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = {
                isLoading = true
                errorMessage = null

                groupRepository.createGroup(groupName) { success, error ->
                    isLoading = false
                    if (success) {
                        navController.navigate("group_overview") {
                            popUpTo("landing") { inclusive = true }
                        }
                    } else {
                        errorMessage = error ?: "Group creation failed"
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Create Group")
            }
        }
        Text(
            text = "Already have a group invitation code? Join here.",
            modifier = Modifier
                .padding(top = 24.dp)
                .clickable { navController.navigate("join_group") },
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateGroupScreenPreview() {
    CreateGroupScreen(navController = rememberNavController(), groupRepository = FakeGroupRepository())
}
