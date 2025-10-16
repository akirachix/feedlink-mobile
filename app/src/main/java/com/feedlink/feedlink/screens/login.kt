
package com.feedlink.feedlink.screens

import android.content.Context
import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.feedlink.feedlink.R
import com.feedlink.feedlink.ui.theme.Green
import com.feedlink.feedlink.ui.theme.Orange
import com.feedlink.feedlink.viewmodel.SigninViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun SignInScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToRecyclerHome: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPassword: () -> Unit,
) {
    val viewModel: SigninViewModel = getViewModel()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val signInResponse by viewModel.signInResponse

    var isEmailError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.userRole.value) {
        viewModel.userRole.value?.let { role ->
            val response = viewModel.signInResponse.value ?: return@LaunchedEffect

            context.getSharedPreferences("FEEDLINK_PREFS", Context.MODE_PRIVATE).edit {
                putString("ACCESS_TOKEN", response.token)
                putString("EMAIL", response.email)
                putString("USER_ROLE", role)
                putString("USER_ID", response.userId)
            }

            when (role) {
                "recycler" -> onNavigateToRecyclerHome()
                "buyer" -> onNavigateToHome()
                else -> onNavigateToHome()
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.top),
            contentDescription = "Top Green Curve",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 180.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(8.dp, RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(Color.White)
                    .padding(horizontal = 22.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "Sign In",
                    color = Orange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .padding(bottom = 18.dp)
                )

                Text(
                    text = "Email:",
                    color = Green,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = {
                        Text(
                            "Enter email",
                            fontStyle = FontStyle.Italic,
                            color = Gray
                        )
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = isEmailError,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Orange,
                        unfocusedBorderColor = Orange,
                        cursorColor = Green,
                        errorBorderColor = Color.Red
                    )
                )
                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Password:",
                    color = Green,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text(
                            "Enter password",
                            fontStyle = FontStyle.Italic,
                            color = Gray
                        )
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    isError = isPasswordError,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Orange,
                        unfocusedBorderColor = Orange,
                        cursorColor = Green,
                        errorBorderColor = Color.Red
                    )
                )

                errorMessage?.takeIf { it.isNotBlank() }?.let { errorMsg ->
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    "Forgot Password?",
                    color = Orange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onForgotPassword() }
                )

                Spacer(Modifier.height(18.dp))

                Button(
                    onClick = {
                        val isEmailValid =
                            email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
                        val isPasswordValid = password.isNotBlank()

                        isEmailError = !isEmailValid
                        isPasswordError = !isPasswordValid

                        if (isEmailValid && isPasswordValid) {
                            viewModel.signin(email, password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Green)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Green,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "Log In",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Do not have an account? ",
                        color = Color.Black,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Sign Up",
                        color = Orange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.clickable { onSignUpClick() }
                    )
                }
            }
        }
    }
}