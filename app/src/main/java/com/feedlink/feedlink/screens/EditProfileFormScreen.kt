package com.feedlink.feedlink.screens

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.feedlink.feedlink.viewmodel.ProfileViewModel
import org.koin.androidx.compose.getViewModel
import java.io.File
import java.io.FileOutputStream

fun saveBitmapToTempFile(context: Context, bitmap: Bitmap, filename: String = "upload_${System.currentTimeMillis()}.jpg"): File? {
    return try {
        val file = File(context.cacheDir, filename)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        file
    } catch (e: Exception) {
        Log.e("FileUtil", "Error saving bitmap", e)
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileFormScreen(
    userIdToEdit: Int,
    onProfileUpdated: () -> Unit,
    onNavigateBack: () -> Unit,
    profileViewModel: ProfileViewModel = getViewModel()
) {
    val context = LocalContext.current

    val userProfile by profileViewModel.userProfile.observeAsState()
    val isLoadingInitialData by profileViewModel.isLoading.observeAsState(false)
    val isUpdatingProfile by profileViewModel.isUpdating.observeAsState(false)
    val error by profileViewModel.error.observeAsState()
    val updateSuccess by profileViewModel.profileUpdateSuccess.observeAsState(false)

    var uiFirstName by remember { mutableStateOf("") }
    var uiLastName by remember { mutableStateOf("") }
    var uiEmail by remember { mutableStateOf("") }


    var localImageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(userProfile) {
        userProfile?.let { profile ->
            uiFirstName = profile.firstName ?: ""
            uiLastName = profile.lastName ?: ""
            uiEmail = profile.email ?: ""
        }
    }


    val imageToDisplay = when {
        localImageUri != null -> localImageUri.toString()
        !userProfile?.profilePicture.isNullOrBlank() -> {
            val url = userProfile!!.profilePicture!!.trim()
            if (url.startsWith("http")) {
                "$url?cb=${System.currentTimeMillis()}"
            } else {
                url
            }
        }
        else -> "https://via.placeholder.com/150"
    }

    var showImageSourceDialog by remember { mutableStateOf(false) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            localImageUri = uri
            profileViewModel.onImageSelected(context, it)
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val file = saveBitmapToTempFile(context, it)
            if (file != null) {
                val uri = Uri.fromFile(file)
                localImageUri = uri
                profileViewModel.onImageSelected(context, uri)
            }
        }
    }
    LaunchedEffect(userIdToEdit) {
        val currentProfileId = userProfile?.id?.toIntOrNull()
        if (currentProfileId != userIdToEdit) {
            profileViewModel.fetchUserProfile(userIdToEdit)
        }
    }

    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

            localImageUri = null

            profileViewModel.resetUpdateSuccessFlag()
            onProfileUpdated()
        }
    }

    LaunchedEffect(error) {
        error?.let { errorMsg ->
            val userMessage = when {
                errorMsg.contains("400", ignoreCase = true) || errorMsg.contains("email.*exist", ignoreCase = true) -> {
                    "This email is already in use. Please choose another."
                }
                errorMsg.contains("invalid", ignoreCase = true) || errorMsg.contains("format", ignoreCase = true) || uiEmail.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(uiEmail).matches() -> {
                    "Please enter a valid email address."
                }
                errorMsg.contains("401", ignoreCase = true) || errorMsg.contains("unauthorized", ignoreCase = true) -> {
                    "Session expired. Please log in again."
                }
                else -> {
                    "Failed to update profile. Please try again."
                }
            }
            Toast.makeText(context, userMessage, Toast.LENGTH_LONG).show()
            profileViewModel.clearError()
        }
    }

    val darkGreenColor = Color(0xFF234B06)
    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = darkGreenColor,
        unfocusedBorderColor = darkGreenColor.copy(alpha = 0.7f),
        cursorColor = darkGreenColor,
        focusedLabelColor = darkGreenColor,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", color = MaterialTheme.colorScheme.onSurface) },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = darkGreenColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        if (isLoadingInitialData && userProfile?.id?.toIntOrNull() != userIdToEdit) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = darkGreenColor)
            }
        } else if (userProfile == null && !isLoadingInitialData) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Failed to load profile.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.size(150.dp), contentAlignment = Alignment.BottomEnd) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageToDisplay),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(2.dp, darkGreenColor, CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop,
                    )
                    IconButton(
                        onClick = { showImageSourceDialog = true },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(darkGreenColor.copy(alpha = 0.8f))
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Filled.CameraAlt, "Change Image", tint = Color.White)
                    }
                }
                CustomOutlinedTextField(
                    value = uiFirstName,
                    onValueChange = { uiFirstName = it },
                    label = "First Name",
                    externalLabelColor = darkGreenColor,
                    textFieldColors = customTextFieldColors
                )
                CustomOutlinedTextField(
                    value = uiLastName,
                    onValueChange = { uiLastName = it },
                    label = "Last Name",
                    externalLabelColor = darkGreenColor,
                    textFieldColors = customTextFieldColors
                )
                CustomOutlinedTextField(
                    value = uiEmail,
                    onValueChange = { uiEmail = it },
                    label = "Email",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    externalLabelColor = darkGreenColor,
                    textFieldColors = customTextFieldColors
                )

                userProfile?.address?.let { address ->
                    if (address.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.LocationOn, null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(address, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (uiEmail.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(uiEmail).matches()) {
                            Toast.makeText(context, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        profileViewModel.saveProfileWithImage(
                            userId = userIdToEdit,
                            firstName = uiFirstName,
                            lastName = uiLastName,
                            email = uiEmail
                        )
                    },
                    enabled = !isUpdatingProfile,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = darkGreenColor,
                        contentColor = Color.White
                    )
                ) {
                    if (isUpdatingProfile) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Save Changes", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Change Profile Picture") },
            confirmButton = {
                TextButton(onClick = {
                    pickImageLauncher.launch("image/*")
                    showImageSourceDialog = false
                }) { Text("Gallery") }
            },
            dismissButton = {
                TextButton(onClick = {
                    takePictureLauncher.launch(null)
                    showImageSourceDialog = false
                }) { Text("Camera") }
            }
        )
    }
}

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    maxLines: Int = 1,
    externalLabelColor: Color,
    textFieldColors: TextFieldColors
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = externalLabelColor,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { if (placeholder.isNotBlank()) Text(placeholder) },
            keyboardOptions = keyboardOptions,
            maxLines = maxLines,
            colors = textFieldColors,
            textStyle = TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
        )
    }
}