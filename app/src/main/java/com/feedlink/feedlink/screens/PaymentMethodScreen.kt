package com.feedlink.feedlink.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.feedlink.feedlink.R
import com.feedlink.feedlink.ui.theme.FeedlinkTheme
import com.feedlink.feedlink.ui.theme.Green
import com.feedlink.feedlink.viewmodel.PaymentUiState
import com.feedlink.feedlink.viewmodel.PaymentViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PaymentMethodScreen(
    navController: NavHostController,
    orderId: Int,
    totalAmount: Int,
    paymentViewModel: PaymentViewModel = koinViewModel()
) {
    var showPhoneNumberDialog by remember { mutableStateOf(false) }
    val paymentState by paymentViewModel.paymentState.collectAsState()


    LaunchedEffect(paymentState) {
        when (val currentState = paymentState) {
            is PaymentUiState.StkPushSuccess -> {

                paymentViewModel.startPaymentPolling(currentState.paymentId, currentState.orderId)
            }

            is PaymentUiState.OrderConfirmed -> {
                navController.navigate(Screen.OrderConfirmed.createRoute(orderId)) {
                    popUpTo(Screen.Home.route) {
                        inclusive = false
                    }
                }
            }

            is PaymentUiState.Error -> {

                println("Payment Flow Error: ${currentState.message}")
            }

            PaymentUiState.Idle,
            PaymentUiState.Loading,
            PaymentUiState.Polling -> {

            }
        }
    }


    PaymentMethodScreenContent(
        onMpesaClick = { showPhoneNumberDialog = true },
        onBackClick = {
            if (paymentState is PaymentUiState.Idle || paymentState is PaymentUiState.Error) {
                paymentViewModel.resetState()
                navController.popBackStack()
            }
        }
    )

    if (showPhoneNumberDialog) {
        PhoneNumberDialog(
            onDismiss = { showPhoneNumberDialog = false },
            onConfirm = { phoneNumber ->
                showPhoneNumberDialog = false
                paymentViewModel.initiateStkPush(phoneNumber, totalAmount, orderId)
            }
        )
    }

    when (val currentState = paymentState) {
        is PaymentUiState.Loading, is PaymentUiState.Polling -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable(enabled = false, onClick = {}),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(60.dp),
                        color = Color.White,
                        strokeWidth = 5.dp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = if (currentState is PaymentUiState.Loading)
                            "Sending request to M-Pesa..."
                        else
                            "Request sent.\nWaiting for payment confirmation...",
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        }
        is PaymentUiState.Error -> {
            ErrorDialog(
                errorMessage = currentState.message,
                onDismiss = { paymentViewModel.resetState() }
            )
        }
        else -> { }
    }
}

@Composable
fun PaymentMethodScreenContent(
    onMpesaClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.topcurve),
            contentDescription = "Top decorative curve",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        )
        Image(
            painter = painterResource(id = R.drawable.curves),
            contentDescription = "Bottom decorative curve",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFFF8000),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(190.dp))
            Text(
                text = "Pay With",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Green
            )
            Spacer(modifier = Modifier.height(60.dp))
            OutlinedButton(
                onClick = onMpesaClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, Green)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.mpesa),
                        contentDescription = "M-Pesa Logo",
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.width(50.dp))
                    Text(
                        text = "M-PESA",
                        color = Color.Black,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun PhoneNumberDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var phoneNumber by remember { mutableStateOf("254") }
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Enter phone number",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Green
                )
                Spacer(Modifier.height(20.dp))
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        if (it.startsWith("254") && it.length <= 12 && it.all { char -> char.isDigit() }) {
                            phoneNumber = it
                        }
                    },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Green,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        border = BorderStroke(1.dp, Green),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancel", color = Green, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(16.dp))
                    Button(
                        onClick = { onConfirm(phoneNumber) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Green),
                        shape = RoundedCornerShape(8.dp),
                        enabled = phoneNumber.length == 12
                    ) {
                        Text("Confirm", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorDialog(errorMessage: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Payment Error", fontWeight = FontWeight.Bold) },
        text = { Text(errorMessage) },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Green)
            ) {
                Text("OK")
            }
        }
    )
}

@Preview(showBackground = true, name = "Payment Method Screen")
@Composable
fun PaymentMethodScreenPreview() {
    FeedlinkTheme {
        PaymentMethodScreenContent(
            onMpesaClick = {},
            onBackClick = {}
        )
    }
}
