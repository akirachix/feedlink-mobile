package com.feedlink.feedlink.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.feedlink.feedlink.R
import com.feedlink.feedlink.model.WasteClaim
import com.feedlink.feedlink.utils.DateUtils
import com.feedlink.feedlink.viewmodel.UpdateClaimStatusUiState
import com.feedlink.feedlink.viewmodel.WasteClaimUiState
import com.feedlink.feedlink.viewmodel.WasteClaimViewModel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WasteCollection(
    viewModel: WasteClaimViewModel = koinViewModel(),
    onTimerClick: (Int) -> Unit,
    onContinueClaimingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val updateStatusState by viewModel.updateStatusState.collectAsState()
    var selectedClaim by remember { mutableStateOf<WasteClaim?>(null) }
    var showDetailsDialog by remember { mutableStateOf(false) }


    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }


    LaunchedEffect(Unit) {
        viewModel.fetchAllWasteClaims()
    }


    LaunchedEffect(updateStatusState) {
        when (val state = updateStatusState) {
            is UpdateClaimStatusUiState.Success -> {
                successMessage = state.message
                showSuccessDialog = true
                showDetailsDialog = false
                viewModel.resetUpdateStatusState()
            }
            is UpdateClaimStatusUiState.Error -> {
                viewModel.resetUpdateStatusState()
            }
            else -> {}
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


        Scaffold(
            modifier = Modifier.padding(top = 200.dp),
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
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
                                    val date1 = DateUtils.parseClaimTime(claim1.claimTime)
                                    val date2 = DateUtils.parseClaimTime(claim2.claimTime)
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
                                    },
                                    onItemClick = {
                                        selectedClaim = claim
                                        showDetailsDialog = true
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
                                    containerColor = Color(0xFF234B06),
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


    if (showDetailsDialog && selectedClaim != null) {
        CollectionDetailsDialog(
            claim = selectedClaim!!,
            onDismiss = { showDetailsDialog = false },
            onUpdateStatus = { newStatus ->
                selectedClaim?.wasteId?.let { wasteId ->
                    viewModel.updateWasteClaimStatus(wasteId, newStatus)
                }
            },
            updateStatusState = updateStatusState
        )
    }


    if (showSuccessDialog) {
        SuccessDialog(
            message = successMessage,
            onDismiss = { showSuccessDialog = false }
        )
    }
}


@Composable
fun WasteClaimItem(
    claim: WasteClaim,
    modifier: Modifier = Modifier,
    onClockClick: () -> Unit = {},
    onItemClick: () -> Unit = {}
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onItemClick() },
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
                color = if (claim.claimStatus == "pending") Color(0xFFFF9800) else Color(0xFF4CAF50)
            )
            Text(
                text = "Claimed: ${DateUtils.formatClaimTime(claim.claimTime)}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = DateUtils.formatDuration(claim.claimTime),
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
                    tint = if (claim.wasteId != null) Color(0xFF234B06) else Color.Gray
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDetailsDialog(
    claim: WasteClaim,
    onDismiss: () -> Unit,
    onUpdateStatus: (String) -> Unit,
    updateStatusState: UpdateClaimStatusUiState
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Collection Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF234B06)
                )


                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = "Waste ID: ${claim.wasteId ?: "Unknown"}",
                    fontSize = 16.sp,
                    color = Color.Black
                )


                Spacer(modifier = Modifier.height(8.dp))


                Text(
                    text = "Current Status: ${claim.claimStatus}",
                    fontSize = 16.sp,
                    color = if (claim.claimStatus == "pending") Color(0xFFFF9800) else Color(0xFF4CAF50)
                )


                Spacer(modifier = Modifier.height(8.dp))


                Text(
                    text = "Claimed on: ${DateUtils.formatClaimTime(claim.claimTime)}",
                    fontSize = 16.sp,
                    color = Color.Black
                )


                Spacer(modifier = Modifier.height(24.dp))


                if (claim.claimStatus == "pending") {
                    Button(
                        onClick = {
                            onUpdateStatus("collected")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF234B06),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = updateStatusState !is UpdateClaimStatusUiState.Loading
                    ) {
                        if (updateStatusState is UpdateClaimStatusUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text(text = "Mark as Collected")
                        }
                    }
                } else {
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = false
                    ) {
                        Text(text = "Already Collected")
                    }
                }


                Spacer(modifier = Modifier.height(8.dp))


                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Close")
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessDialog(
    message: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Success!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color(0xFF234B06)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF234B06),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "OK")
                }
            }
        }
    }
}