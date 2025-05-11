package com.evenmoney.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.evenmoney.ui.theme.EvenMoneyTheme

@Composable
fun LandingScreen(navController: NavController) {
    EvenMoneyTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Placeholder Icon",
                modifier = Modifier
                    .padding(top = 225.dp, bottom = 120.dp)
                    .size(100.dp)
            )
            Text("Even Money",
                fontSize = 35.sp)
            Text("Manage your shared expenses easily with Even Money.",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            )

            Button(onClick = {
                navController.navigate("login")
            },
                modifier = Modifier.padding(top = 10.dp).fillMaxWidth().padding(horizontal = 20.dp)) {
                Text("Get Started")
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun LandingScreenPreview() {
    LandingScreen(navController = rememberNavController())
}