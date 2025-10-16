package com.feedlink.feedlink.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.feedlink.feedlink.R
import com.feedlink.feedlink.viewmodel.OrderUiState
import com.feedlink.feedlink.viewmodel.OrderViewModel
import org.koin.androidx.compose.koinViewModel

val DarkGreen = Color(0xFF0A5825)

@Composable
fun OrderConfirmedScreen(
    navController: NavController,
    orderId: Int
) {
    val viewModel: OrderViewModel = koinViewModel()

    LaunchedEffect(key1 = orderId) {
        viewModel.fetchOrderDetails(orderId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGreen),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .aspectRatio(1f),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.8f)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 80.dp, start = 16.dp, end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Payment confirmed",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(16.dp))

                    when (val state = uiState) {
                        is OrderUiState.Loading -> {
                            CircularProgressIndicator(color = Color.White)
                        }
                        is OrderUiState.SuccessSingle -> {
                            val order = state.order
                            Text(
                                text = "Your pickup PIN is:",
                                color = Color.White,
                                fontSize = 18.sp
                            )
                            Text(
                                text = order.pin ?: "N/A",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        is OrderUiState.Error -> {
                            Text(
                                text = state.message,
                                color = Color.Red
                            )
                        }
                        else -> {
                            Text("Fetching details...", color = Color.White)
                        }
                    }


                    Spacer(Modifier.weight(1f))
                    OutlinedButton(
                        onClick = {
                            navController.navigate("home") { popUpTo(0) }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(horizontal = 24.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color.White),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text("Go to Home", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }

            Image(
                painter = painterResource(id = R.drawable.check),
                contentDescription = "Payment Confirmed Checkmark",
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.TopCenter),
                contentScale = ContentScale.Fit
            )
        }
    }
}
