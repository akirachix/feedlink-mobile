package com.feedlink.feedlink.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.feedlink.feedlink.R
import com.feedlink.feedlink.model.Listing
import com.feedlink.feedlink.viewmodel.ProductDetailViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    listingId: Int,
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    viewModel: ProductDetailViewModel = koinViewModel()
) {
    LaunchedEffect(listingId) {
        viewModel.fetchProductDetail(listingId)
    }

    val product by viewModel.product
    val isLoading by viewModel.isLoading
    val error by viewModel.error
    val quantity by viewModel.quantity

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Product Details",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E4E1E)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF2E4E1E)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF2E4E1E)
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selected = "home",
                onHomeClick = onNavigateToHome,
                onCartClick = onNavigateToCart,
                onOrdersClick = onNavigateToOrders,
                onNotificationsClick = onNavigateToNotifications
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF2E4E1E)
                    )
                }

                error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: $error",
                            color = Color.Red,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E4E1E)
                            )
                        ) {
                            Text("Go Back", color = Color.White)
                        }
                    }
                }

                product != null -> {
                    ProductDetailContent(
                        product = product!!,
                        quantity = quantity,
                        onQuantityChange = { viewModel.updateQuantity(it) },
                        onAddToCart = {
                            viewModel.addToCart()
                            onNavigateToCart()
                        }
                    )
                }

                else -> {
                    Text(
                        text = "Product not found",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun ProductDetailContent(
    product: Listing,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    onAddToCart: () -> Unit
) {
    var isAddingToCart by remember { mutableStateOf(false) }

    val resolvedImageUrl = when {
        !product.image.isNullOrBlank() -> product.image.trim()
        else -> null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(resolvedImageUrl ?: R.drawable.africanladyprofile)
                .crossfade(true)
                .error(R.drawable.africanladyprofile)
                .placeholder(R.drawable.africanladyprofile)
                .build(),
            contentDescription = "Product image",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Price: ${product.discountedPrice} Ksh",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF2E4E1E)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Expiry Date: ${product.expiryDate?.substringBefore('T') ?: "N/A"}",
            fontSize = 14.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Description: ${product.description ?: "No description available"}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E4E1E)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Quantity",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF2E4E1E)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onQuantityChange(quantity - 1) },
                enabled = quantity > 1,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFFFA500), CircleShape)
            ) {
                Icon(
                    Icons.Default.Remove,
                    contentDescription = "Decrease quantity",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(48.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = quantity.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E4E1E)
                )
            }

            IconButton(
                onClick = { onQuantityChange(quantity + 1) },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFFFA500), CircleShape)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Increase quantity",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (isAddingToCart) return@Button
                isAddingToCart = true
                onAddToCart()
            },
            enabled = !isAddingToCart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF234B06),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isAddingToCart) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = "Add to Cart",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}