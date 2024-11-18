package com.example.atry.viewmodel

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class AuthViewModel : ViewModel() {

    private val _userData = MutableLiveData<UserData>()
    val userData: LiveData<UserData> get() = _userData
    private var username by mutableStateOf("")

    fun updateUsername(newUsername: String) {
        username = newUsername
    }

    private val _signUpSuccess = MutableLiveData<Boolean>()
    val signUpSuccess: LiveData<Boolean> get() = _signUpSuccess

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    val firebaseUser: LiveData<FirebaseUser?> get() = _firebaseUser

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _firebaseUser.postValue(firebaseAuth.currentUser)
    }

    val _toastMessage = MutableLiveData<String?>()

    init {
        auth.addAuthStateListener(authStateListener)
        checkUserLoggedInStatus()
    }

    fun logIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("AuthViewModel", "Login successful")
            } else {
                Log.d("AuthViewModel", "Login failed: ${task.exception?.message}")
                _toastMessage.postValue("Login failed: ${task.exception?.message}")
            }
        }
    }

    fun signUp(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.let {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()

                    it.updateProfile(profileUpdates).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            _toastMessage.postValue("Sign up successful!")
                            _signUpSuccess.postValue(true)
                        } else {
                            _toastMessage.postValue("Failed to update profile: ${updateTask.exception?.message}")
                            _signUpSuccess.postValue(false) // Handle failure
                        }
                    }
                }
            } else {
                _toastMessage.postValue("Sign up failed: ${task.exception?.message}")
                _signUpSuccess.postValue(false) // Handle failure
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _firebaseUser.postValue(null) // Set to null to indicate the user is signed out
    }

    fun sendPasswordResetEmail(email: String) {
        if (email.isBlank()) {
            _toastMessage.postValue("Email is required")
        } else {
            auth.sendPasswordResetEmail(email).addOnSuccessListener {
                _toastMessage.postValue("Password reset email sent")
            }.addOnFailureListener {
                _toastMessage.postValue(it.message ?: "Error sending password reset email")
            }
        }
    }

    fun checkUserLoggedInStatus() {
        _firebaseUser.postValue(auth.currentUser) // If the user is already logged in, firebaseUser will have a value
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }

    fun getUserData() {
        val user = auth.currentUser
        user?.let {
            firestore.collection("users").document(user.email.toString())
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val weight = documentSnapshot.getLong("weight")?.toInt()
                        val height = documentSnapshot.getDouble("height")?.toFloat()
                        val age = documentSnapshot.getLong("age")?.toInt()
                        val gender = documentSnapshot.getLong("gender")?.toInt()
                        val bmi = documentSnapshot.getString("bmi")
                        val profileImageUrl = documentSnapshot.getString("profileImageUrl")

                        _userData.postValue(UserData(weight, height, age, gender, bmi, profileImageUrl))
                        _toastMessage.postValue("User data retrieved successfully")
                    } else {
                        _toastMessage.postValue("No user data found")
                    }
                }
                .addOnFailureListener { e ->
                    _toastMessage.postValue("Error retrieving user data: ${e.message}")
                }
        } ?: run {
            _toastMessage.postValue("User not logged in")
        }
    }

    fun uploadProfileImage(uri: Uri, context: Context) {
        val user = auth.currentUser
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("profile_images/${user?.email}.jpg")

        imageRef.putFile(uri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    saveProfileImageUrlToFirestore(user?.email.toString(), downloadUrl.toString())
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to upload image: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    fun saveProfileImageUrlToFirestore(email: String, imageUrl: String) {
        val userRef = firestore.collection("users").document(email)
        userRef.update("profileImageUrl", imageUrl)
            .addOnSuccessListener {
                _toastMessage.postValue("Profile image updated")
                getUserData()
            }
            .addOnFailureListener { exception ->
                _toastMessage.postValue("Failed to update profile image: ${exception.message}")
            }
    }

    fun clearToastMessage() {
        _toastMessage.postValue(null) // Clears the current toast message
    }

    fun updateName(name:String){
        val user = Firebase.auth.currentUser

        val profileUpdates = userProfileChangeRequest {
            displayName = name
            //photoUri = Uri.parse("https://example.com/jane-q-user/profile.jpg")
        }

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User profile updated.")
                }
            }
    }

    fun updatePassword(context: Context, newPassword: String) {
        val user = auth.currentUser
        if (user == null) {
            Log.e("UpdatePassword", "User not logged in")
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("UpdatePassword", "Attempting to update password for user: ${user.email}")

        user.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("UpdatePassword", "Password updated successfully")
                    Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("UpdatePassword", " Login again before retrying this request.")
                    Toast.makeText(context, "Error updating password", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun saveUserData(weight: Int, height: Float, age: Int, gender: Int, bmi: String) {
        val user = auth.currentUser
        user?.let {
            val userData = mapOf(
                "weight" to weight,
                "height" to height,
                "age" to age,
                "gender" to gender,
                "bmi" to bmi,
                "isBMICalculated" to true
            )

            firestore.collection("users").document(user.email.toString())
                .set(userData)
                .addOnSuccessListener {
                    _toastMessage.postValue("User data saved successfully")
                }
                .addOnFailureListener { e ->
                    _toastMessage.postValue("Error saving user data: ${e.message}")
                }
        }
    }

    data class UserData(
        val weight: Int?,
        val height: Float?,
        val age: Int?,
        val gender: Int?,
        val bmi: String?,
        val profileImageUrl: String?
    )

    companion object
}
