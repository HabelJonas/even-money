package com.evenmoney.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItemDefaults.colors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.evenmoney.repositories.interfaces.IAuthRepository
import com.evenmoney.repositories.mocks.FakeAuthRepository
import com.evenmoney.ui.theme.EvenMoneyTheme
import com.evenmoney.R
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily

@Composable
fun LoginScreen(navController: NavController, authRepository: IAuthRepository) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    EvenMoneyTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.bright)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(vertical = 32.dp),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.bright_dark)) // logo_background
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(colorResource(id = R.color.dark)),
                        modifier = Modifier
                            .size(150.dp)
                            .border(
                                width = 2.dp,
                                color = colorResource(id = R.color.dark),
                                shape = RoundedCornerShape(16.dp),
                            ).padding(16.dp)

                    )
                    Text(
                        text = "Even Money",
                        fontSize = 50.sp,
                        style = TextStyle(fontFamily = FontFamily.Serif),
                        modifier = Modifier.padding(top = 16.dp),
                        color = colorResource(id = R.color.dark)
                    )
                    Text(
                        text = "Login",
                        fontSize = 32.sp,
                        modifier = Modifier.padding(top = 16.dp),
                        color = colorResource(id = R.color.text_color)
                    )
                    Text(
                        text = "Login to your account",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 6.dp, bottom = 18.dp),
                        color = colorResource(R.color.text_color)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Your email address", color = colorResource(R.color.text_color)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(16.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(color = colorResource(R.color.text_color))
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Your password", color = colorResource(R.color.text_color)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(16.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(color = colorResource(R.color.text_color))
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
                            authRepository.login(email, password) { success, error ->
                                isLoading = false
                                if (success) {
                                    navController.navigate("group_overview") {
                                        popUpTo("landing") { inclusive = true }
                                    }
                                } else {
                                    errorMessage = error ?: "Login failed"
                                }
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.dark),// logo_button
                            contentColor = colorResource(R.color.text_color)// logo_on_button
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = colorResource(R.color.text_color),
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text("Login", fontSize = 18.sp)
                        }
                    }
                    Text(
                        text = "Don't have an account? Register now!",
                        modifier = Modifier
                            .padding(top = 18.dp)
                            .clickable { navController.navigate("register") },
                        color = colorResource(R.color.text_color) // logo_primary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(navController = rememberNavController(), authRepository = FakeAuthRepository())
}

