package com.feedlink.feedlink.screens
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.feedlink.feedlink.R
import com.feedlink.feedlink.model.ListingItem
import com.feedlink.feedlink.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.feedlink.feedlink.viewmodel.CartViewModel
import com.feedlink.feedlink.viewmodel.CheckoutUiState

@Composable
fun CartScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToPayment: (orderId: Int, amount: Int) -> Unit,
    cartViewModel: CartViewModel = viewModel()
) {
    val cartItems = cartViewModel.cartItems
    val totalItems = cartViewModel.totalItems
    val totalPrice = cartViewModel.totalPrice

    val checkoutState by cartViewModel.checkoutState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(checkoutState) {
        when (val state = checkoutState) {
            is CheckoutUiState.NavigateToPayment -> {
                onNavigateToPayment(state.orderId, state.amount)
                cartViewModel.checkoutStateConsumed()
            }
            is CheckoutUiState.Error -> {
                snackbarHostState.showSnackbar(
                    message = state.message,
                    duration = SnackbarDuration.Short
                )
                cartViewModel.checkoutStateConsumed()
            }
            else -> {  }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                BottomNavigationBar(
                    selected = "cart",
                    onHomeClick = onNavigateToHome,
                    onCartClick = {},
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
                Image(
                    painter = painterResource(id = R.drawable.topcurve),
                    contentDescription = "Top Curve",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(228.dp)
                        .align(Alignment.TopCenter)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Your Cart",
                        color = Orange,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 30.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = Orange,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 220.dp)
                ) {
                    CartContent(
                        cartItems = cartItems,
                        totalItems = totalItems,
                        totalPrice = totalPrice,
                        onQuantityChange = cartViewModel::onQuantityChange,
                        onRemoveItem = cartViewModel::onRemoveItem,
                        onContinueShopping = onNavigateToHome,
                        onProceedToCheckout = { cartViewModel.initiateCheckout() },
                        isCheckoutLoading = checkoutState is CheckoutUiState.Loading
                    )
                }
            }
        }

        if (checkoutState is CheckoutUiState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Green
            )
        }
    }
}


@Composable
fun CartContent(
    cartItems: List<ListingItem>,
    totalItems: Int,
    totalPrice: Int,
    onQuantityChange: (Int, Int) -> Unit,
    onRemoveItem: (Int) -> Unit,
    onContinueShopping: () -> Unit,
    onProceedToCheckout: () -> Unit,
    isCheckoutLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp)
        ) {
            items(cartItems, key = { it.listingId }) { item ->
                CartItemRow(
                    item = item,
                    onQuantityChange = { newQuantity ->
                        onQuantityChange(item.listingId, newQuantity)
                    },
                    onRemove = { onRemoveItem(item.listingId) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Items", fontWeight = FontWeight.Normal, fontSize = 16.sp, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            Text("$totalItems", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("$totalPrice KSH", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
        }

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = onContinueShopping,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            border = BorderStroke(1.dp, Green)
        ) {
            Text("Continue Shopping", color = Green, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onProceedToCheckout,
            colors = ButtonDefaults.buttonColors(
                containerColor = Green,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = cartItems.isNotEmpty() && !isCheckoutLoading
        ) {
            if (isCheckoutLoading) {
                Text("Processing...", fontWeight = FontWeight.Bold)
            } else {
                Text("Proceed to checkout", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun CartItemRow(
    item: ListingItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = item.image ?: "",
                placeholder = painterResource(id = R.drawable.ic_launcher_background)
            ),
            modifier = Modifier
                .size(70.dp)
                .clip(MaterialTheme.shapes.medium),
            contentDescription = item.description,
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.description ?: "Unknown Item",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Green,
                    fontWeight = FontWeight.SemiBold
                )
            )
            val price = item.discountedPrice ?: item.originalPrice ?: 0f
            val priceInt = price.toInt()
            Text(
                text = "$priceInt KSH",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
            )
        }

        Spacer(Modifier.width(16.dp))

        Column(horizontalAlignment = Alignment.End) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove item",
                    tint = Green
                )
            }
            Spacer(Modifier.height(8.dp))
            QuantitySelector(
                quantity = item.quantity?.toIntOrNull() ?: 1,
                onQuantityChange = onQuantityChange
            )
        }
    }
}

@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(36.dp)
            .border(BorderStroke(1.dp, Green), CircleShape)
    ) {
        IconButton(
            onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
            modifier = Modifier.size(34.dp)
        ) {
            Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Green)
        }

        Text(
            text = quantity.toString(),
            modifier = Modifier.padding(horizontal = 8.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Green
        )

        IconButton(
            onClick = { onQuantityChange(quantity + 1) },
            modifier = Modifier.size(34.dp)
        ) {
            Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Green)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    FeedlinkTheme {
        CartScreen(
            onNavigateToHome = {},
            onNavigateToOrders = {},
            onNavigateToNotifications = {},
            onNavigateToPayment = { _, _ -> }
        )
    }
}

