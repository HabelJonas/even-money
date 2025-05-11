package com.evenmoney.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.evenmoney.repositories.interfaces.IGroupRepository
import com.evenmoney.repositories.mocks.FakeGroupRepository
import com.evenmoney.ui.theme.EvenMoneyTheme


data class Group(
    var groupId: String = "",
    var groupName: String = "",
    var invitationCode: String = ""
)

@Composable
fun GroupOverviewScreen(navController: NavController, groupRepository: IGroupRepository) {
    var showMenu by remember { mutableStateOf(false) }
    val fabShape = CircleShape
    var groups by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var groupInfo by remember { mutableStateOf(listOf<Group>()) }

    LaunchedEffect(Unit) {
        groupRepository.readGroupsMap { success, map ->
            if (success) {
                for (entry in map) {
                    groupRepository.readGroupInvitationCode(entry.key) { success, invitationCode ->
                        if (success) {
                            groupInfo = groupInfo.toMutableList().apply {
                                add(Group(entry.key, entry.value, invitationCode))
                            }
                        }
                    }
                }
            }
            isLoading = false
        }
    }

    EvenMoneyTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Group Overview",
                    fontSize = 35.sp,
                    modifier = Modifier.padding(top = 80.dp)
                )

                Icon(
                    imageVector = Icons.Filled.Face,
                    contentDescription = "Placeholder Icon",
                    modifier = Modifier
                        .padding(top = 80.dp, bottom = 120.dp)
                        .size(100.dp)
                )

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        groupInfo.forEach { group ->
                            item(key = group.groupId) {
                                GroupCard(
                                    groupName = group.groupName,
                                    invitationCode = group.invitationCode,
                                    onClick = {
                                        navController.navigate("expenses_and_balances/${group.groupId}")
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                FloatingActionButton(
                    onClick = { showMenu = true },
                    shape = fabShape
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Gruppe hinzufügen")
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Gruppe beitreten") },
                        onClick = {
                            showMenu = false
                            navController.navigate("join_group")
                        },
                        leadingIcon = {
                            Icon(Icons.Filled.Add, contentDescription = null)
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Gruppe erstellen") },
                        onClick = {
                            showMenu = false
                            navController.navigate("create_group")
                        },
                        leadingIcon = {
                            Icon(Icons.Filled.Create, contentDescription = null)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GroupCard(groupName: String, invitationCode: String, onClick: () -> Unit) {
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
            Icon(
                imageVector = Icons.Filled.Face,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = groupName,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
            Text(
                text = invitationCode,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 20.dp)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun GroupOverviewScreenPreview() {
    GroupOverviewScreen(
        navController = rememberNavController(),
        groupRepository = FakeGroupRepository()
    )
}
