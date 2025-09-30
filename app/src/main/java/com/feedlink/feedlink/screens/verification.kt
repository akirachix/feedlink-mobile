package com.feedlink.feedlink.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.getViewModel
import com.feedlink.feedlink.R
import com.feedlink.feedlink.ui.theme.Green
import com.feedlink.feedlink.viewmodel.ForgotPasswordViewModel
import kotlinx.coroutines.delay

@Composable
fun VerificationCodeScreen(
    email: String,
    onVerificationSuccess: (String) -> Unit = {},
    onResendClick: () -> Unit = {}
) {
    val viewModel: ForgotPasswordViewModel = getViewModel()

    var pinDigits by remember { mutableStateOf(List(4) { "" }) }
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    var timeLeft by remember { mutableStateOf(180) }

    val focusRequesters = remember { List(4) { FocusRequester() } }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }

    val otp = pinDigits.joinToString("")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.curves),
            contentDescription = "Bottom Wave",
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 62.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(120.dp))

            Image(
                painter = painterResource(id = R.drawable.feedlink),
                contentDescription = "Logo",
                modifier = Modifier.size(74.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Enter Verification Code",
                color = Green,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                pinDigits.forEachIndexed { index, digit ->
                    VerificationCodeBox(
                        value = digit,
                        onValueChange = { newValue ->
                            if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                                val newDigits = pinDigits.toMutableList()
                                newDigits[index] = newValue
                                pinDigits = newDigits

                                if (newValue.isNotEmpty() && index < 3) {
                                    focusRequesters[index + 1].requestFocus()
                                }
                            } else if (newValue.isEmpty() && index > 0) {
                                focusRequesters[index - 1].requestFocus()
                            }
                        },
                        focusRequester = focusRequesters[index]
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val minutes = timeLeft / 60
            val seconds = timeLeft % 60
            val formattedTime = String.format("%02d:%02d", minutes, seconds)

            Text(
                text = formattedTime,
                color = Green,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Didn't receive code?",
                    color = Color.Black,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(
                    onClick = onResendClick,
                    enabled = timeLeft == 0,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Resend",
                        color = if (timeLeft == 0) Color(0xFFFF9800) else Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            errorMessage?.takeIf { it.isNotBlank() }?.let { errorMsg ->
                Spacer(Modifier.height(12.dp))
                Text(
                    text = errorMsg,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            Button(
                onClick = {
                    if (otp.length == 4) {
                        viewModel.verifyOtp(email, otp) {
                            onVerificationSuccess(otp)
                        }
                    }
                },
                enabled = otp.length == 4 && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF045611)
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Green,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "Continue",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun VerificationCodeBox(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .width(54.dp)
            .height(54.dp)
            .focusRequester(focusRequester),
        textStyle = TextStyle(
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        ),
        singleLine = true,
        shape = RoundedCornerShape(4.dp),
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = Green,
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Black
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        visualTransformation = VisualTransformation.None
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VerificationCodeScreenPreview() {
    VerificationCodeScreen(
        email = "test@example.com",
        onVerificationSuccess = {},
        onResendClick = {}
    )
}