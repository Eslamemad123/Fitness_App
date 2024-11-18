package com.example.atry.Setting

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.atry.R
import com.example.atry.pages.Calculator
import com.example.atry.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfile(autoViewModel: AuthViewModel, context: Context, navController: NavController) {
    val userData by autoViewModel.userData.observeAsState(initial = AuthViewModel.UserData(0, 0f, 0, 0, "",""))

    var username by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Safely extract user data
    val age = userData.age ?: 0
    val gender = userData.gender ?: 0
    val weight = userData.weight ?: 0
    val slider = userData.height?.toFloat() ?: 0f

    LaunchedEffect(Unit) {
        autoViewModel.getUserData()  // Ensure this function correctly fetches data
    }

    // Use Box to stack the background image and the LazyColumn
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.photo_edit_profile),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                // Add the back button
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.TopEnd).padding(top = 30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .size(60.dp)
                    )
                }

                Text(
                    text = "Edit Profile",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp)
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.Transparent), // Ensure the background is transparent
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text(text = "User Name", color = Color.White) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                tint = Color.White,
                                contentDescription = "Email Icon"
                            )
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.White,
                            focusedTextColor = Color.White, // لون النص عند التركيز
                            unfocusedTextColor = Color.White // لون النص عند عدم التركيز
                        )
                    )

                    var showPassword by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text(text = "New Password", color = Color.White) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Key,
                                tint = Color.White,
                                contentDescription = "Password Icon"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.White,
                            focusedTextColor = Color.White, // لون النص عند التركيز
                            unfocusedTextColor = Color.White // لون النص عند عدم التركيز
                        ),
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (showPassword) "Hide password" else "Show password",
                                    tint = Color.White // لون الأيقونة باللون الأبيض
                                )
                            }
                        }
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text(text = "Confirm Password", color = Color.White) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Key,
                                tint = Color.White,
                                contentDescription = "Password Icon"
                            )
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.White,
                            focusedTextColor = Color.White, // لون النص عند التركيز
                            unfocusedTextColor = Color.White // لون النص عند عدم التركيز
                        ),
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (showPassword) "Hide password" else "Show password",
                                    tint = Color.White // لون الأيقونة باللون الأبيض
                                )
                            }
                        }
                    )

                    var showMessage by remember { mutableStateOf(false) }

                    Button(
                        onClick = {
                            if (username.isNotEmpty()) {
                                autoViewModel.updateName(username)
                                Toast.makeText(context, "Saved name", Toast.LENGTH_SHORT).show()
                            }

                            if (newPassword == confirmPassword && newPassword.isNotEmpty()) {
                                autoViewModel.updatePassword(context, confirmPassword)
                                Toast.makeText(context, "Saved new password", Toast.LENGTH_SHORT).show()
                            } else if (newPassword.isNotEmpty() && confirmPassword.isNotEmpty()) {
                                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(text = "Save Changes",
                        fontWeight = FontWeight.Bold // تعيين الخط ليكون بولد
                        )
                    }

                    Button(
                        onClick = {
                            showMessage = !showMessage
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                    ) {
                        Text(
                            text = "Show/hide BMI calc",
                            fontWeight = FontWeight.Bold // تعيين الخط ليكون بولد
                        )
                    }


                    if (showMessage) {
                        Calculator(navController, autoViewModel, age, gender, weight, slider, navigate = false)
                    }
                }
            }
        }
    }
}
