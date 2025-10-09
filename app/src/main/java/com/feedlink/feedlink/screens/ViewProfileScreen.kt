package com.feedlink.feedlink.screens


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.feedlink.feedlink.auth.TokenManager
import com.feedlink.feedlink.model.UserProfile
import com.feedlink.feedlink.viewmodel.ProfileViewModel
import org.koin.androidx.compose.getViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewProfileScreen(
    onNavigateToEdit: (Int) -> Unit,
    onLogout: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = getViewModel()
) {
    val context = LocalContext.current


    val userProfile by viewModel.userProfile.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()


    var showLogoutDialog by remember { mutableStateOf(false) }


    LaunchedEffect(userProfile == null) {
        if (userProfile == null) {
            val userIdStr = TokenManager.getUserId()
            Log.d("Profile", "Saved user ID: $userIdStr")
            val userId = userIdStr?.toIntOrNull()
            Log.d("Profile", "Parsed user ID: $userId")


            if (userId != null) {
                viewModel.fetchUserProfile(userId)
            } else {
                Log.e("Profile", "User ID missing or invalid")
            }
        }
    }


    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, "Error: $it", Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF234B06)
                        )
                    }
                },
                title = { },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color(0xFF234B06)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                isLoading && userProfile == null ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))


                userProfile != null ->
                    ProfileContent(profile = userProfile!!, onNavigateToEdit = onNavigateToEdit)


                error != null ->
                    Column(
                        Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Failed to load profile.", color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = {
                            TokenManager.getUserId()?.toIntOrNull()?.let { id ->
                                viewModel.fetchUserProfile(id)
                            }
                        }) { Text("Retry") }
                    }


                else ->
                    Text("No profile data.", Modifier.align(Alignment.Center))
            }
        }
    }


    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("Logout", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                Spacer(modifier = Modifier.width(24.dp))
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = Color(0xFF234B06))
                }
            }
        )
    }
}


@Composable
fun ProfileContent(profile: UserProfile, onNavigateToEdit: (Int) -> Unit) {
    val context = LocalContext.current
    val darkGreenColor = Color(0xFF234B06)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            SubcomposeAsyncImage(
                model = profile.profilePicture?.trim(),
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            color = darkGreenColor,
                            strokeWidth = 2.dp
                        )
                    }
                },
                success = { successState ->
                    Log.d(
                        "ViewProfileScreen",
                        "Coil Success: Image loaded from URL: ${successState.result.request}"
                    )
                    SubcomposeAsyncImageContent()
                },
                error = {
                    Log.e(
                        "ViewProfileScreen",
                        "Coil Error: Failed to load image. URL: ${profile.profilePicture}",
                        it.result.throwable
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(2.dp, Color.Red.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Default Profile Picture",
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                contentDescription = "Profile Image",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(2.dp, darkGreenColor, CircleShape),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = {
                    profile.id?.toIntOrNull()?.let { id ->
                        onNavigateToEdit(id)
                    } ?: Toast.makeText(context, "Profile not loaded", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(darkGreenColor)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit Profile",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }


        Spacer(modifier = Modifier.height(24.dp))


        Text(
            text = "${profile.firstName ?: ""} ${profile.lastName ?: ""}".trim(),
            style = MaterialTheme.typography.headlineSmall,
            color = darkGreenColor,
            fontWeight = FontWeight.Bold,


            )


        Spacer(modifier = Modifier.height(8.dp))


        if (!profile.email.isNullOrBlank()) {
            Text(
                text = profile.email!!,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
        }




    }
    @Composable
    fun ProfileDetailRow(label: String, value: String?) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(0.3f)
            )
            Text(
                text = if (value.isNullOrBlank()) "N/A" else value,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(0.7f)
            )
        }
    }}



