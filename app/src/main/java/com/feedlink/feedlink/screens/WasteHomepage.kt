package com.feedlink.feedlink.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.feedlink.feedlink.model.Listing
import com.feedlink.feedlink.utils.NotificationManager
import com.feedlink.feedlink.viewModel.ListingUiState
import com.feedlink.feedlink.viewModel.ListingViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WasteHomepage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val viewModel: ListingViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val showClaimSuccessDialog by viewModel.showClaimSuccessDialog.collectAsState()
    val claimedListings by viewModel.claimedListings.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val newListingDetected by viewModel.newListingDetected.collectAsState()

    val listState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    var selectedListing by remember { mutableStateOf<Listing?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchInedibleListings()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            NotificationManager.showNotification(
                context,
                "New Waste Available!",
                "New inedible waste items are available for claim."
            )
        }

        viewModel.resetNewListingFlag()
    }

    LaunchedEffect(newListingDetected) {
        if (newListingDetected) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                when (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                )) {
                    PackageManager.PERMISSION_GRANTED -> {
                        NotificationManager.showNotification(
                            context,
                            "New Waste Available!",
                            "New inedible waste items are available for claim."
                        )
                        viewModel.resetNewListingFlag()
                    }
                    else -> {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            } else {
                NotificationManager.showNotification(
                    context,
                    "New Waste Available!",
                    "New inedible waste items are available for claim."
                )
                viewModel.resetNewListingFlag()
            }
        }
    }

    val filteredListings = when (val currentState = uiState) {
        is ListingUiState.Success -> {
            currentState.listings.filter { listing ->
                searchQuery.isEmpty() ||
                        listing.category?.contains(searchQuery, ignoreCase = true) == true
            }
        }
        else -> emptyList()
    }

    if (showClaimSuccessDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissClaimSuccessDialog() },
            title = { Text("Claim Successful") },
            text = { Text("You have successfully claimed this waste item. Please check your email for the pickup PIN.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissClaimSuccessDialog() }
                ) {
                    Text("OK")
                }
            }
        )
    }

    selectedListing?.let { listing ->
        ListingDetailsPopup(
            listing = listing,
            onDismiss = { selectedListing = null },
            onClaimClick = {
                viewModel.claimListing(listing.listingId)
                selectedListing = null
            },
            isClaimed = claimedListings.contains(listing.listingId)
        )
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "Available Inedible Waste",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.refreshListings() },
                            enabled = !isRefreshing
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = if (isRefreshing) Color.Gray else Color(0xFF4CAF50)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black
                    )
                )
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search by category...") },
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
                        onSearch = { }
                    )
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (val currentState = uiState) {
                is ListingUiState.Loading -> {
                    if (filteredListings.isEmpty()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(filteredListings) { listing ->
                                ListingItem(
                                    listing = listing,
                                    isClaimed = claimedListings.contains(listing.listingId),
                                    onClaimClick = { viewModel.claimListing(listing.listingId) },
                                    onItemClick = { selectedListing = listing }
                                )
                            }
                        }

                        if (isRefreshing) {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.TopCenter)
                                    .padding(top = 8.dp),
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }

                is ListingUiState.Success -> {
                    if (filteredListings.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (searchQuery.isEmpty()) "No available inedible waste items" else "No matching inedible items found",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Check back later for new waste items",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { viewModel.refreshListings() }) {
                                    Text("Refresh")
                                }
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(filteredListings) { listing ->
                                ListingItem(
                                    listing = listing,
                                    isClaimed = claimedListings.contains(listing.listingId),
                                    onClaimClick = { viewModel.claimListing(listing.listingId) },
                                    onItemClick = { selectedListing = listing }
                                )
                            }
                        }

                        if (isRefreshing) {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.TopCenter)
                                    .padding(top = 8.dp),
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }

                is ListingUiState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("Failed to load items: ${currentState.message}")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.refreshListings() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListingItem(
    listing: Listing,
    isClaimed: Boolean,
    onClaimClick: () -> Unit,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clickable { onItemClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, Color(0xFFFFC107)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = listing.imageUrl ?: listing.placeholderImageUrl,
                contentDescription = "Waste image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Type: ${listing.productType?.replaceFirstChar { it.uppercase() } ?: "Unknown"}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF4CAF50),
                fontSize = 12.sp,
                maxLines = 1
            )

            Text(
                text = "Category: ${listing.category ?: "None"}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 12.sp,
                maxLines = 1
            )

            Text(
                text = "${listing.quantity ?: "0"} ${listing.unit ?: "units"}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onClaimClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isClaimed) Color.Gray else Color(0xFF4CAF50),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = !isClaimed
            ) {
                Text(
                    text = if (isClaimed) "Claimed" else "Claim",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ListingDetailsPopup(
    listing: Listing,
    onDismiss: () -> Unit,
    onClaimClick: () -> Unit,
    isClaimed: Boolean,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            modifier = modifier
                .width((screenWidth * 0.9f))
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
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
                        text = "Item Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF4CAF50)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                AsyncImage(
                    model = listing.imageUrl ?: listing.placeholderImageUrl,
                    contentDescription = "Waste image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(16.dp))

                DetailRow(label = "Product Type", value = listing.productType ?: "Unknown")
                DetailRow(label = "Category", value = listing.category?.replaceFirstChar { it.uppercase() } ?: "Unknown")
                DetailRow(label = "Description", value = listing.description ?: "No description")
                DetailRow(label = "Quantity", value = "${listing.quantity ?: "0"} ${listing.unit ?: "units"}")

                listing.originalPrice?.let {
                    DetailRow(label = "Original Price", value = it)
                }
                listing.discountedPrice?.let {
                    DetailRow(label = "Discounted Price", value = it)
                }

                listing.expiryDate?.let {
                    DetailRow(label = "Expiry Date", value = formatDateTime(it))
                }
                DetailRow(label = "Listed On", value = formatDateTime(listing.createdAt))

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onClaimClick()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isClaimed) Color.Gray else Color(0xFF4CAF50),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isClaimed
                ) {
                    Text(
                        text = if (isClaimed) "Already Claimed" else "Claim This Item",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

private fun formatDateTime(dateString: String?): String {
    if (dateString == null) return "Unknown"

    return try {
        val formats = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        )

        for (format in formats) {
            try {
                val date = format.parse(dateString)
                val now = Date()
                val diff = now.time - date.time

                val outputFormat = when {
                    diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
                    diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)} minutes ago"
                    diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)} hours ago"
                    diff < TimeUnit.DAYS.toMillis(7) -> "${TimeUnit.MILLISECONDS.toDays(diff)} days ago"
                    else -> {
                        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        dateFormat.format(date)
                    }
                }

                return outputFormat
            } catch (e: Exception) {
                Log.e("WasteHomepage", "Error formatting date: $dateString", e)
            }
        }

        dateString
    } catch (e: Exception) {
        Log.e("WasteHomepage", "Error formatting date: $dateString", e)
        dateString
    }
}

private fun formatDuration(durationString: String?): String {
    if (durationString == null) return "Unknown"

    return try {
        val hours = durationString.toIntOrNull()
        if (hours != null) {
            return when {
                hours < 24 -> "$hours hours"
                hours % 24 == 0 -> "${hours / 24} days"
                else -> "${hours / 24} days and ${hours % 24} hours"
            }
        }

        durationString
    } catch (e: Exception) {
        Log.e("WasteHomepage", "Error formatting duration: $durationString", e)
        durationString
    }
}