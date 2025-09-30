package com.feedlink.feedlink.screens

import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.getViewModel
import com.feedlink.feedlink.R
import com.feedlink.feedlink.model.SignUpRequest
import com.feedlink.feedlink.ui.theme.Green
import com.feedlink.feedlink.ui.theme.Orange
import com.feedlink.feedlink.viewmodel.SignupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    userRole: String,
    onSignUpSuccess: () -> Unit = {},
    onSignInClick: () -> Unit = {},
) {
    val viewModel: SignupViewModel = getViewModel()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val signUpSuccessResponse by viewModel.SignupSuccess

    var isFirstNameError by remember { mutableStateOf(false) }
    var isLastNameError by remember { mutableStateOf(false) }
    var isEmailError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }
    var isConfirmPasswordError by remember { mutableStateOf(false) }

    LaunchedEffect(userRole) {
        Log.d("SignUpScreen", "User role received for this screen: $userRole")
    }

    LaunchedEffect(signUpSuccessResponse) {
        if (signUpSuccessResponse != null) {
            Log.d("SignUpScreen", "Sign up successful (response received), navigating...")
            onSignUpSuccess()
        }
    }

    fun validateFields(): Boolean {
        isFirstNameError = firstName.isBlank()
        isLastNameError = lastName.isBlank()
        isEmailError = email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        isPasswordError = password.isNotBlank() && password.length < 6
        isConfirmPasswordError = confirmPassword.isNotBlank() && password != confirmPassword

        return firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                password.isNotBlank() && password.length >= 6 &&
                confirmPassword.isNotBlank() && password == confirmPassword
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
                .fillMaxSize()
                .padding(top = 150.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(30.dp))

                Text(
                    text = "Sign Up",
                    color = Orange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Text(
                    "First Name:",
                    color = Green,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                OutlinedTextField(
                    value = firstName,
                    onValueChange = {
                        firstName = it; if (it.isNotBlank()) isFirstNameError = false
                    },
                    placeholder = {
                        Text(
                            "Enter your first name",
                            fontStyle = FontStyle.Italic,
                            color = Green.copy(alpha = 0.7f)
                        )
                    },
                    singleLine = true,
                    isError = isFirstNameError,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF9800),
                        unfocusedBorderColor = if (isFirstNameError) MaterialTheme.colorScheme.error else Color(
                            0xFFFF9800
                        ).copy(alpha = 0.7f),
                        cursorColor = Color(0xFF197116)
                    )
                )
                if (isFirstNameError) {
                    Text(
                        "First name cannot be empty",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, top = 2.dp)
                    )
                }
                Spacer(Modifier.height(10.dp))

                Text(
                    "Last Name:",
                    color = Green,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it; if (it.isNotBlank()) isLastNameError = false },
                    placeholder = {
                        Text(
                            "Enter your last name",
                            fontStyle = FontStyle.Italic,
                            color = Green.copy(alpha = 0.7f)
                        )
                    },
                    singleLine = true,
                    isError = isLastNameError,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF9800),
                        unfocusedBorderColor = if (isLastNameError) MaterialTheme.colorScheme.error else Color(
                            0xFFFF9800
                        ).copy(alpha = 0.7f),
                        cursorColor = Color(0xFF197116)
                    )
                )
                if (isLastNameError) {
                    Text(
                        "Last name cannot be empty",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, top = 2.dp)
                    )
                }
                Spacer(Modifier.height(10.dp))

                Text(
                    "Email:",
                    color = Green,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it; if (it.isNotBlank()) isEmailError =
                        !Patterns.EMAIL_ADDRESS.matcher(it).matches() else isEmailError = false
                    },
                    placeholder = {
                        Text(
                            "user@example.com",
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF197116).copy(alpha = 0.7f)
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = isEmailError,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Orange,
                        unfocusedBorderColor = if (isEmailError) MaterialTheme.colorScheme.error else Color(
                            0xFFFF9800
                        ).copy(alpha = 0.7f),
                        cursorColor = Color(0xFF197116)
                    )
                )
                if (isEmailError) {
                    Text(
                        "Enter a valid email address",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, top = 2.dp)
                    )
                }
                Spacer(Modifier.height(10.dp))

                Text(
                    "Password:",
                    color = Green,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it; if (it.isNotBlank()) isPasswordError =
                        it.length < 6 else isPasswordError = false
                    },
                    placeholder = {
                        Text(
                            "Enter password",
                            fontStyle = FontStyle.Italic,
                            color = Green.copy(alpha = 0.7f)
                        )
                    },
                    singleLine = true,
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
                        unfocusedBorderColor = if (isPasswordError) MaterialTheme.colorScheme.error else Color(
                            0xFFFF9800
                        ).copy(alpha = 0.7f),
                        cursorColor = Color(0xFF197116)
                    )
                )
                if (isPasswordError) {
                    Text(
                        "Password must be at least 6 characters",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, top = 2.dp)
                    )
                }
                Spacer(Modifier.height(10.dp))

                Text(
                    "Confirm Password:",
                    color = Green,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it; if (it.isNotBlank()) isConfirmPasswordError =
                        password != it else isConfirmPasswordError = false
                    },
                    placeholder = {
                        Text(
                            "Re-enter password",
                            fontStyle = FontStyle.Italic,
                            color = Green.copy(alpha = 0.7f)
                        )
                    },
                    singleLine = true,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    isError = isConfirmPasswordError,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Orange,
                        unfocusedBorderColor = if (isConfirmPasswordError) MaterialTheme.colorScheme.error else Color(
                            0xFFFF9800
                        ).copy(alpha = 0.7f),
                        cursorColor = Color(0xFF197116)
                    )
                )
                if (isConfirmPasswordError) {
                    Text(
                        "Passwords do not match",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, top = 2.dp)
                    )
                }
                Spacer(Modifier.height(20.dp))

                if (errorMessage != null && errorMessage!!.isNotEmpty()) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Button(
                    onClick = {
                        Log.d("SignUpScreen", "Create Account button clicked.")
                        val formIsValid = validateFields()

                        if (formIsValid) {
                            Log.d(
                                "SignUpScreen",
                                "Fields are valid. Calling viewModel.signup with role: $userRole"
                            )
                            val request = SignUpRequest(
                                email = email.trim(),
                                password = password,
                                firstName = firstName.trim(),
                                lastName = lastName.trim(),
                                role = userRole
                            )
                            viewModel.signup(request)
                        } else {
                            Log.d("SignUpScreen", "Fields are invalid. Not calling signup.")
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
                            "Create account",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Already have an account? ", color = Color.Black, fontSize = 15.sp)
                    Text(
                        text = "Sign In",
                        color = Orange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.clickable { if (!isLoading) onSignInClick() }
                    )
                }
                Spacer(Modifier.height(30.dp))
            }
        }
    }
}