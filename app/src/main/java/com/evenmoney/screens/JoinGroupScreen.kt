package com.evenmoney.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.evenmoney.repositories.interfaces.IGroupRepository
import com.evenmoney.repositories.mocks.FakeGroupRepository
import java.nio.file.WatchEvent

@Composable
fun JoinGroupScreen(navController: NavController, groupRepository: IGroupRepository) {
    var invitationCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            , verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "Join Group",
            modifier = Modifier.padding(top = 10.dp) )

        OutlinedTextField(
            value = invitationCode,
            onValueChange = { invitationCode = it },
            label = { Text("Invitation Code") },
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

                groupRepository.joinGroup(invitationCode) { success, error ->
                    isLoading = false
                    if (success) {
                        navController.navigate("group_overview") {
                            popUpTo("landing") { inclusive = true }
                        }
                    } else {
                        errorMessage = error ?: "Joining group failed."
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
                Text("Join Group")
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun JoinGroupScreenPreview()
{
    JoinGroupScreen(navController = rememberNavController(), groupRepository = FakeGroupRepository())
}
