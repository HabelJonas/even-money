package com.evenmoney

import com.evenmoney.screens.ExpensesAndBalancesScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.evenmoney.repositories.AuthRepository
import com.evenmoney.repositories.ExpenseRepository
import com.evenmoney.repositories.GroupRepository
import com.evenmoney.screens.AddExpenseScreen
import com.evenmoney.screens.CreateGroupScreen
import com.evenmoney.screens.ExpenseDetailsScreen
import com.evenmoney.screens.GroupOverviewScreen
import com.evenmoney.screens.JoinGroupScreen
import com.evenmoney.screens.LandingScreen
import com.evenmoney.screens.LoginScreen
import com.evenmoney.screens.RegisterScreen
import com.evenmoney.ui.theme.EvenMoneyTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        setContent {
            EvenMoneyTheme {
                val navController = rememberNavController()
                val authRepository = AuthRepository()
                val groupRepository = GroupRepository()
                val expenseRepository = ExpenseRepository()
                NavHost(navController = navController, startDestination = "landing") {
                    composable("landing") {
                        LandingScreen(navController)
                    }
                    composable("login") {
                        LoginScreen(navController, authRepository)
                    }
                    composable("register") {
                        RegisterScreen(navController, authRepository)
                    }
                    composable("group_overview") {
                        GroupOverviewScreen(navController, groupRepository)
                    }
                    composable("join_group") {
                        JoinGroupScreen(navController, groupRepository)
                    }
                    composable("create_group") {
                        CreateGroupScreen(navController, groupRepository)
                    }
                    composable("expenses_and_balances/{groupId}") { backStackEntry ->
                        val groupId = backStackEntry.arguments?.getString("groupId")
                        ExpensesAndBalancesScreen(navController, groupId, expenseRepository, groupRepository, authRepository)
                    }
                    composable("add_expense/{groupId}") { backStackEntry ->
                        val groupId = backStackEntry.arguments?.getString("groupId")
                        AddExpenseScreen(
                            navController,
                            groupId.toString(),
                            groupRepository,
                            expenseRepository
                        )
                    }
                    composable("expense_details/{groupId}/{title}") { backStackEntry ->
                        val title = backStackEntry.arguments?.getString("title")
                        val groupId = backStackEntry.arguments?.getString("groupId")
                        ExpenseDetailsScreen(
                            navController,
                            groupId.toString(),
                            title.toString(),
                            expenseRepository,
                            groupRepository
                        )

                    }
                }
            }
        }
    }
}

