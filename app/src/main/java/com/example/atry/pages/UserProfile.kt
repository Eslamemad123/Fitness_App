package com.example.atry.pages

import Back_Handler
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.atry.R
import com.example.atry.navigate.BottomNavigationBar
import com.example.atry.viewmodel.AuthViewModel


@Composable
fun UserProfile(navController: NavController) {
    val authViewModel: AuthViewModel = viewModel()
    val notification = rememberSaveable { mutableStateOf("") }
    val userData by authViewModel.userData.observeAsState()
    val firebaseUser by authViewModel.firebaseUser.observeAsState()
    val username = firebaseUser?.displayName ?: "Guest"

    LaunchedEffect(Unit) {
        authViewModel.getUserData()
    }

    if (notification.value.isNotEmpty()) {
        Toast.makeText(LocalContext.current, notification.value, Toast.LENGTH_LONG).show()
        notification.value = ""
    }

    var weight by rememberSaveable { mutableStateOf(userData?.weight?.toString() ?: "") }
    var height by rememberSaveable { mutableStateOf(userData?.height?.toString() ?: "") }
    var age by rememberSaveable { mutableStateOf(userData?.age?.toString() ?: "") }
    var gender by rememberSaveable { mutableStateOf(userData?.gender?.toString() ?: "") }
    var bmi by rememberSaveable { mutableStateOf(userData?.bmi ?: "") }

    LaunchedEffect(userData) {
        userData?.let {
            weight = it.weight?.toString() ?: ""
            height = it.height?.toString() ?: ""
            age = it.age?.toString() ?: ""
            gender = it.gender?.toString() ?: ""
            bmi = it.bmi ?: ""
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            ProfileImage(
                userData = userData ?: AuthViewModel.UserData(
                    null, null, null, null, null, null
                ),
                authViewModel = authViewModel
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.body1,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                label = { Text("Height") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.body1,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.body1,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = gender,
                onValueChange = { gender = it },
                label = { Text("Gender") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.body1,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = bmi,
                onValueChange = { bmi = it },
                label = { Text("BMI") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.body1,
                singleLine = true,
                enabled = false, // Make this field non-editable
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    disabledTextColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    authViewModel.saveUserData(
                        weight.toIntOrNull() ?: 0,
                        height.toFloatOrNull() ?: 0f,
                        age.toIntOrNull() ?: 0,
                        gender.toIntOrNull() ?: 0,
                        bmi
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Update Profile", style = MaterialTheme.typography.button)
            }
        }

        BottomNavigationBar(navController = navController)

        Back_Handler()
    }
}


@Composable
fun ProfileImage(userData: AuthViewModel.UserData, authViewModel: AuthViewModel) {
    val imageUri = rememberSaveable { mutableStateOf(userData.profileImageUrl ?: "") }
    val context = LocalContext.current
    val painter: Painter = rememberAsyncImagePainter(
        if (imageUri.value.isEmpty())
            R.drawable.ic_user
        else
            imageUri.value
    )
    val firebaseUser by authViewModel.firebaseUser.observeAsState()
    val username = firebaseUser?.displayName ?: "Guest"

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri.value = it.toString()
            authViewModel.uploadProfileImage(it, context)
        }
    }

    Column(
        modifier = Modifier
            .padding(0.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = CircleShape,
            modifier = Modifier
                .padding(8.dp)
                .size(100.dp)
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .wrapContentSize()
                    .clickable { launcher.launch("image/*") },
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = if (imageUri.value.isNotEmpty()) "$username" else "Change profile picture", fontSize = 24.sp)
    }
}

