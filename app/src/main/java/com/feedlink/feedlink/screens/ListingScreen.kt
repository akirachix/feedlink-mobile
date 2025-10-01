
package com.feedlink.feedlink.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.feedlink.feedlink.R
import com.feedlink.feedlink.location.LocationManager
import com.feedlink.feedlink.location.RequestLocationPermission
import com.feedlink.feedlink.model.Listing
import com.feedlink.feedlink.viewmodel.ListingsViewModel
import org.koin.androidx.compose.koinViewModel


data class CategoryItem(val name: String, val iconRes: Int)

@Composable
fun BottomNavigationBar(
    selected: String = "home",
    onHomeClick: () -> Unit,
    onCartClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selected == "home",
            onClick = onHomeClick,
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (selected == "home") Color(0xFFFFA500) else Color(0xFF234B06)
                )
            },
            label = {
                Text(
                    "Home",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 10.sp,
                    color = if (selected == "home") Color(0xFFFFA500) else Color(0xFF234B06)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFFA500),
                selectedTextColor = Color(0xFFFFA500),
                unselectedIconColor = Color(0xFF234B06),
                unselectedTextColor = Color(0xFF234B06),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = selected == "cart",
            onClick = onCartClick,
            icon = {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = "Cart",
                    tint = if (selected == "cart") Color(0xFFFFA500) else Color(0xFF234B06)
                )
            },
            label = {
                Text(
                    "Cart",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 10.sp,
                    color = if (selected == "cart") Color(0xFFFFA500) else Color(0xFF234B06)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFFA500),
                selectedTextColor = Color(0xFFFFA500),
                unselectedIconColor = Color(0xFF234B06),
                unselectedTextColor = Color(0xFF234B06),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = selected == "orders",
            onClick = onOrdersClick,
            icon = {
                Icon(
                    Icons.Default.List,
                    contentDescription = "Orders",
                    tint = if (selected == "orders") Color(0xFFFFA500) else Color(0xFF234B06)
                )
            },
            label = {
                Text(
                    "Orders",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 10.sp,
                    color = if (selected == "orders") Color(0xFFFFA500) else Color(0xFF234B06)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFFA500),
                selectedTextColor = Color(0xFFFFA500),
                unselectedIconColor = Color(0xFF234B06),
                unselectedTextColor = Color(0xFF234B06),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = selected == "notifications",
            onClick = onNotificationsClick,
            icon = {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = if (selected == "notifications") Color(0xFFFFA500) else Color(0xFF234B06)
                )
            },
            label = {
                Text(
                    "Notifications",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 10.sp,
                    color = if (selected == "notifications") Color(0xFFFFA500) else Color(0xFF234B06)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFFA500),
                selectedTextColor = Color(0xFFFFA500),
                unselectedIconColor = Color(0xFF234B06),
                unselectedTextColor = Color(0xFF234B06),
                indicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
fun ListingCard(listing: Listing, onProductClick: (Int) -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .width(140.dp)
            .height(230.dp)
            .clickable { onProductClick(listing.listingId) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(listing.imageUrl ?: R.drawable.africanladyprofile)
                    .crossfade(true)
                    .error(R.drawable.africanladyprofile)
                    .placeholder(R.drawable.africanladyprofile)
                    .build(),
                contentDescription = listing.description ?: "Product image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (listing.description.isNullOrBlank()) "No Title" else listing.description!!,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF2E4E1E),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ex.Date: ${listing.expiryDate ?: "N/A"}",
                fontSize = 11.sp,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Price: ${listing.discountedPrice?.toInt()?.toString() ?: "N/A"} Ksh",
                fontSize = 12.sp,
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF2E4E1E)
            )
        }
    }
}

@Composable
fun CategoryRow(onCategorySelected: (String?) -> Unit) {
    val categories = listOf(
        CategoryItem("All", R.drawable.africanladyprofile),
        CategoryItem("Food", R.drawable.food),
        CategoryItem("Drinks", R.drawable.drinks),
        CategoryItem("Dairy", R.drawable.milk)
    )

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        categories.forEach { category ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { onCategorySelected(category.name) }
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2E4E1E).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = category.iconRes),
                        contentDescription = category.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category.name,
                    fontSize = 12.sp,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF2E4E1E)
                )
            }
        }
    }
}

@Composable
fun ForYouSection(listings: List<Listing>, onProductClick: (Int) -> Unit) {
    val forYouListings = listings.take(4)
    if (forYouListings.isNotEmpty()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            forYouListings.forEach { listing ->
                ListingCard(listing, onProductClick)
            }
        }
    } else {
        Text("No special offers for you at the moment.", modifier = Modifier.padding(vertical = 8.dp))
    }
}

@Composable
fun AllProductsGrid(listings: List<Listing>, onProductClick: (Int) -> Unit) {
    val itemsPerRow = 2
    val rows = listings.chunked(itemsPerRow)

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        rows.forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { listing ->
                    ListingCard(listing, onProductClick, modifier = Modifier.weight(1f))
                }
                if (rowItems.size < itemsPerRow) {
                    Spacer(modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingScreen(
    onNavigateToProductDetail: (Int) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    viewModel: ListingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val locationManager = remember { LocationManager(context) }
    val launchPermissionRequest = remember { mutableStateOf<(() -> Unit)?>(null) }
    var isLoadingPermission by remember { mutableStateOf(false) }

    var selectedCategory by remember { mutableStateOf<String?>(null) }

    RequestLocationPermission(
        onPermissionResult = { location ->
            isLoadingPermission = false
            if (location != null) {
                viewModel.fetchListings(location.first, location.second)
            } else {
                viewModel.fetchListings(null, null)
            }
        },
        onPermissionRequestReady = { function ->
            launchPermissionRequest.value = function
            isLoadingPermission = true
            function.invoke()
        }
    )

    val listingsResult by viewModel.listings
    val uiState by viewModel.uiState

    val allEdibleListings = listingsResult?.filter { it.productType == "edible" } ?: emptyList()

    val filteredListings = if (selectedCategory.isNullOrEmpty() || selectedCategory == "All") {
        allEdibleListings
    } else {
        when (selectedCategory) {
            "Food" -> allEdibleListings.filter { it.category in listOf("Vegetables", "Fruits", "Food") }
            "Drinks" -> allEdibleListings.filter { it.category == "Drinks" }
            "Dairy" -> allEdibleListings.filter { it.category == "Dairy" }
            else -> allEdibleListings.filter { it.category == selectedCategory }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Welcome!",
                            fontSize = 20.sp,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFF2E4E1E)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                                .clickable { onNavigateToProfile() },
                            contentScale = ContentScale.Crop
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF2E4E1E)
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selected = "home",
                onHomeClick = {},
                onCartClick = onNavigateToCart,
                onOrdersClick = onNavigateToOrders,
                onNotificationsClick = onNavigateToNotifications
            )
        },
        content = { paddingValues ->
            if (isLoadingPermission) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF2E4E1E))
                }
                return@Scaffold
            }

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF2E4E1E))
                    }
                }

                uiState.error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error: ${uiState.error}",
                            color = Color.Red,
                            fontSize = 16.sp
                        )
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Categories",
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 22.sp,
                            color = Color(0xFF2E4E1E),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CategoryRow(onCategorySelected = { category ->
                            selectedCategory = category
                        })

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "For you",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color(0xFF2E4E1E),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ForYouSection(
                            listings = filteredListings,
                            onProductClick = onNavigateToProductDetail
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "All Products",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color(0xFF2E4E1E),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        AllProductsGrid(
                            listings = filteredListings,
                            onProductClick = onNavigateToProductDetail
                        )

                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    )
}