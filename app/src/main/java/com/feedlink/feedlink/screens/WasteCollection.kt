package com.feedlink.feedlink.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.feedlink.feedlink.R
import com.feedlink.feedlink.model.WasteClaim
import com.feedlink.feedlink.viewModel.WasteClaimUiState
import com.feedlink.feedlink.viewModel.WasteClaimViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WasteCollection(
    onTimerClick: (Int) -> Unit,
    onContinueClaimingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: WasteClaimViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAllWasteClaims()
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
                    text = "Collection",
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
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
                    ) {
                        val sortedClaims = currentState.claims.sortedWith { claim1, claim2 ->
                            try {
                                val date1 = parseClaimTime(claim1.claimTime)
                                val date2 = parseClaimTime(claim2.claimTime)
                                date2.compareTo(date1)
                            } catch (e: Exception) {
                                0
                            }
                        }

                        items(sortedClaims) { claim ->
                            WasteClaimItem(
                                claim = claim,
                                onClockClick = {
                                    claim.wasteId?.let { onTimerClick(it) }
                                }
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Button(
                            onClick = onContinueClaimingClick,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Continue claiming",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                is WasteClaimUiState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("Failed to load claims: ${currentState.message}")
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
fun WasteClaimItem(
    claim: WasteClaim,
    modifier: Modifier = Modifier,
    onClockClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Waste ID: ${claim.wasteId ?: "Unknown"}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = "Status: ${claim.claimStatus}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Claimed: ${formatClaimTime(claim.claimTime)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatDuration(claim.claimTime),
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(end = 8.dp)
                )
                IconButton(
                    onClick = onClockClick,
                    enabled = claim.wasteId != null
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Timer",
                        tint = if (claim.wasteId != null) Color(0xFF4CAF50) else Color.Gray
                    )
                }
            }
        }
    }
}

private fun parseClaimTime(claimTime: String?): Date {
    if (claimTime == null) return Date(0)

    val formats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    )

    for (format in formats) {
        try {
            if (claimTime.endsWith("Z")) {
                format.timeZone = TimeZone.getTimeZone("UTC")
            } else {
                format.timeZone = TimeZone.getDefault()
            }

            val date = format.parse(claimTime)
            if (date != null) {
                Log.d("TimeParsing", "Successfully parsed '$claimTime' with format '${format.toPattern()}'")
                return date
            }
        } catch (e: Exception) {
            Log.d("TimeParsing", "Failed to parse '$claimTime' with format '${format.toPattern()}': ${e.message}")
        }
    }

    Log.e("TimeParsing", "Could not parse claim time: $claimTime")
    return Date(0)
}

private fun formatClaimTime(claimTime: String?): String {
    if (claimTime == null) return "Unknown time"

    return try {
        val claimDate = parseClaimTime(claimTime)
        val now = Date()

        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getDefault()

        dateFormat.format(claimDate)
    } catch (e: Exception) {
        claimTime
    }
}

private fun formatDuration(claimTime: String?): String {
    if (claimTime == null) return "00:00"

    return try {
        val claimDate = parseClaimTime(claimTime)

        val now = Date()

        Log.d("TimeFormatting", "Claim time: $claimTime")
        Log.d("TimeFormatting", "Parsed claim date: $claimDate")
        Log.d("TimeFormatting", "Current time: $now")

        val diffInMillis = now.time - claimDate.time

        if (diffInMillis < 0) {
            Log.d("TimeFormatting", "Claim time is in the future, showing 00:00")
            return "00:00"
        }

        val seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
        val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)

        val formattedTime = when {
            days >= 7 -> {
                val weeks = days / 7
                if (weeks >= 4) {
                    val months = weeks / 4
                    "${months}mo ago"
                } else {
                    "${weeks}w ago"
                }
            }
            days >= 1 -> "${days}d ago"
            hours >= 1 -> "${hours}h ago"
            minutes >= 1 -> "${minutes}m ago"
            else -> "Just now"
        }

        Log.d("TimeFormatting", "Formatted duration: $formattedTime (diffInMillis: $diffInMillis)")

        formattedTime
    } catch (e: Exception) {
        Log.e("TimeFormatting", "Error formatting duration: ${e.message}")
        "00:00"
    }
}