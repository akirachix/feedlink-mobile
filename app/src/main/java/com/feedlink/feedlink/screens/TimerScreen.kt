package com.feedlink.feedlink.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.feedlink.feedlink.R
import com.feedlink.feedlink.repository.WasteClaimRepository
import com.feedlink.feedlink.viewmodel.TimerViewModel
import kotlinx.coroutines.delay
import org.koin.core.context.GlobalContext
import kotlin.math.min
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    onBackClick: () -> Unit,
    claimId: Int?
) {
    if (claimId == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Invalid claim ID", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBackClick) {
                    Text("Go Back", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        return
    }

    val viewModel: TimerViewModel = viewModel(
        factory = TimerViewModelFactory(claimId)
    )

    val wasteClaimState by viewModel.wasteClaim.collectAsState()
    val timerExpired by viewModel.timerExpired.collectAsState()
    val isOverdue by viewModel.isOverdue.collectAsState()
    val pickupDeadline by viewModel.pickupDeadline.collectAsState()
    val totalTime by viewModel.totalTimeInSeconds.collectAsState()
    var timeLeft by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(pickupDeadline) {
        if (pickupDeadline != null) {
            timeLeft = viewModel.getTimeLeftInSeconds()
            if (timeLeft <= 0) {
                isRunning = false
                viewModel.onTimerExpired()
            }
        }
    }

    LaunchedEffect(key1 = isRunning, key2 = timeLeft) {
        if (isRunning && timeLeft > 0) {
            while (isRunning && timeLeft > 0) {
                delay(1000)
                timeLeft--
            }

            if (timeLeft == 0) {
                isRunning = false
                viewModel.onTimerExpired()
            }
        }
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

            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to WasteCollection",
                    tint = Color(0xFFFF9800)
                )
            }

            Text(
                text = "Timer",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF9800)
                ),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 150.dp)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                wasteClaimState == null -> {
                    CircularProgressIndicator()
                    Text(
                        text = "Loading claim details...",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                wasteClaimState?.isSuccess == true -> {
                    val claim = wasteClaimState!!.getOrNull()!!

                    val wasteDetails = "Claim ID: ${claim.wasteId}\n" +
                            "Status: ${claim.claimStatus}\n" +
                            "Claimed at: ${formatClaimTime(claim.claimTime)}"

                    Text(
                        text = wasteDetails,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    pickupDeadline?.let { deadline ->
                        Text(
                            text = "Pickup deadline: ${formatDeadline(deadline)}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isOverdue) Color.Red else Color.Gray
                            ),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Canvas(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            val center = Offset(size.width / 2, size.height / 2)
                            val radius = min(size.width, size.height) / 2 - 16.dp.toPx()
                            val strokeWidth = 16.dp.toPx()

                            drawArc(
                                color = Color.LightGray,
                                startAngle = 270f,
                                sweepAngle = 360f,
                                useCenter = false,
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )

                            val progress = if (timerExpired || isOverdue) 0f else timeLeft.toFloat() / totalTime

                            drawArc(
                                color = if (timerExpired || isOverdue) Color.Gray else Color(0xFFFF9800),
                                startAngle = 270f,
                                sweepAngle = progress * 360f,
                                useCenter = false,
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }

                        Text(
                            text = if (isOverdue) "00:00" else if (timerExpired) "00:00" else formatTime(timeLeft),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = if (timerExpired || isOverdue) Color.Gray else Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    if (isOverdue) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Pickup overdue!",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else if (timerExpired) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Time expired! Waste is now available for claiming.",
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                else -> {
                    val exception = wasteClaimState?.exceptionOrNull()!!
                    Text(
                        text = "Error loading claim",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = exception.message ?: "Unknown error",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.fetchWasteClaim() }) {
                        Text("Retry", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", minutes, secs)
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

private fun formatDeadline(deadline: Date): String {
    val outputFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    outputFormat.timeZone = TimeZone.getDefault()
    return outputFormat.format(deadline)
}

class TimerViewModelFactory(
    private val claimId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            val repository = GlobalContext.get().get<WasteClaimRepository>()
            @Suppress("UNCHECKED_CAST")
            return TimerViewModel(repository, claimId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
private fun Float.toPx(): Float {
    return this * (LocalDensity.current.density)
}
