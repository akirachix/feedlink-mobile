package com.feedlink.feedlink.screens

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
import com.feedlink.feedlink.model.WasteClaim
import com.feedlink.feedlink.viewModel.WasteClaimUiState
import com.feedlink.feedlink.viewModel.WasteClaimViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WasteHistory(modifier: Modifier = Modifier) {
    val viewModel: WasteClaimViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredClaims = when (val currentState = uiState) {
        is WasteClaimUiState.Success -> {
            currentState.claims.filter { claim ->
                searchQuery.isEmpty() ||
                        claim.wasteId.toString().contains(searchQuery) ||
                        claim.claimStatus?.contains(searchQuery, ignoreCase = true) == true
            }
        }
        else -> emptyList()
    }

    val groupedClaims = filteredClaims.groupBy { claim ->
        claim.claimTime?.substring(0, 10) ?: "Unknown"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
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
                    text = "Collection History",
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
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search collections...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
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
                keyboardActions = KeyboardActions(
                    onSearch = {}
                )
            )
            Text(
                text = "Collections",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF4CAF50),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            when (val currentState = uiState) {
                is WasteClaimUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is WasteClaimUiState.Success -> {
                    if (filteredClaims.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(if (searchQuery.isEmpty()) "No collection history" else "No matching collections found")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            groupedClaims.forEach { (date, claims) ->
                                item {
                                    Text(
                                        text = formatMonthHeader(date),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }

                                items(claims) { claim ->
                                    HistoryClaimItem(claim = claim)
                                }

                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                    }
                }

                is WasteClaimUiState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("Failed to load history: ${currentState.message}")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.fetchAllWasteClaims() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryClaimItem(claim: WasteClaim, modifier: Modifier = Modifier) {
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
                    text = "Waste ID: ${claim.wasteId}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = claim.claimStatus ?: "Unknown",
                    fontSize = 14.sp,
                    color = when (claim.claimStatus) {
                        "collected" -> Color(0xFF4CAF50)
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
                    text = formatDateTime(claim.claimTime),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "PIN: ${claim.pin ?: "N/A"}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

private fun formatMonthHeader(dateString: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateString)
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthFormat.format(date)
    } catch (e: Exception) {
        dateString
    }
}

private fun formatDateTime(dateTimeString: String?): String {
    if (dateTimeString == null) return "Unknown"

    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(dateTimeString)
        val timeFormat = SimpleDateFormat("h:mm a, MMM d", Locale.getDefault())
        timeFormat.format(date)
    } catch (e: Exception) {
        "Unknown"
    }
}