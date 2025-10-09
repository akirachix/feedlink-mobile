package com.feedlink.feedlink.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.feedlink.feedlink.R
import com.feedlink.feedlink.network.Order
import com.feedlink.feedlink.viewmodel.OrderUiState
import com.feedlink.feedlink.viewmodel.OrderViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistory(
    onNavigateToHome: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    viewModel: OrderViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredOrders = when (val currentState = uiState) {
        is OrderUiState.Success -> {
            currentState.orders.filter { order ->
                searchQuery.isEmpty() ||
                        order.orderId.toString().contains(searchQuery) ||
                        order.orderStatus?.contains(searchQuery, ignoreCase = true) == true
            }
        }
        else -> emptyList()
    }

    val groupedOrders = filteredOrders.groupBy { order ->
        order.orderDate?.substring(0, 10) ?: "Unknown Date"
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selected = "orders", // or "cart" if that's your identifier — adjust as needed
                onHomeClick = onNavigateToHome,
                onCartClick = {}, // You might want to implement this
                onOrdersClick = onNavigateToOrders,
                onNotificationsClick = onNavigateToNotifications
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                // Top curved header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.topcurve),
                        contentDescription = "Curved Background",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Order History",
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = Color(0xFFFF9800)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 200.dp)
                        .padding(paddingValues)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder = { Text("Search orders by ID or status...") },
                        leadingIcon = { Icon(Icons.Default.Search, "Search") },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedIndicatorColor = Color(0xFF4CAF50),
                            focusedIndicatorColor = Color(0xFF4CAF50),
                            unfocusedLeadingIconColor = Color(0xFF4CAF50),
                            focusedLeadingIconColor = Color(0xFF4CAF50)
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {})
                    )

                    Text(
                        text = "Your Orders",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    when (val currentState = uiState) {
                        is OrderUiState.Loading -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }

                        is OrderUiState.Success -> {
                            if (filteredOrders.isEmpty()) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        if (searchQuery.isEmpty()) "You have no past orders"
                                        else "No matching orders found"
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    groupedOrders.forEach { (date, orders) ->
                                        item {
                                            Text(
                                                text = formatMonthHeader(date),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = Color.Gray,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )
                                        }
                                        items(orders) { order ->
                                            OrderHistoryItem(order = order)
                                        }
                                        item {
                                            Spacer(modifier = Modifier.height(16.dp))
                                        }
                                    }
                                }
                            }
                        }

                        is OrderUiState.Error -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text("Failed to load history: ${currentState.message}")
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { viewModel.fetchAllOrders() }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun OrderHistoryItem(order: Order, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order ID: ${order.orderId}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = order.orderStatus?.replaceFirstChar { it.uppercase() } ?: "Unknown",
                    fontSize = 14.sp,
                    color = when (order.orderStatus?.lowercase()) {
                        "picked", "delivered" -> Color(0xFF4CAF50)
                        "pending" -> Color(0xFFFFC107)
                        else -> Color.Gray
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDateTime(order.orderDate),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "PIN: ${order.pin ?: "N/A"}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

private fun formatMonthHeader(dateString: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = parser.parse(dateString) ?: return dateString
        val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        formatter.format(date)
    } catch (e: Exception) {
        Log.e("OrderHistory", "Error formatting month header: $dateString", e)
        dateString
    }
}

private fun formatDateTime(dateTimeString: String?): String {
    if (dateTimeString == null) return "Unknown date"

    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date = parser.parse(dateTimeString) ?: return "Unknown date"
        val formatter = SimpleDateFormat("h:mm a, MMM d", Locale.getDefault())
        formatter.format(date)
    } catch (e: Exception) {
        Log.e("OrderHistory", "Error formatting date time: $dateTimeString", e)
        "Unknown date"
    }
}