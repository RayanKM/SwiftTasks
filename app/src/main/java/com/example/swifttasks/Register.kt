package com.example.swifttasks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.example.swifttasks.databinding.ActivityRegisterBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        firebaseAuth = FirebaseAuth.getInstance()

        // Get shared preferences
        val sharedPreferences = getSharedPreferences("SV", MODE_PRIVATE)

        // Handle click on Login TextView
        binding.textView.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        // Handle click on Register button
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()
            val username = binding.nameEt.text.toString()

            // Check if all fields are not empty
            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() && username.isNotEmpty()) {

                // Check if passwords match
                if (pass == confirmPass) {

                    // Create user with email and password using Firebase Auth
                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                // Get user ID from Firebase Auth and create UserDataModel object
                                database = FirebaseDatabase.getInstance().getReference("Users")
                                val id = firebaseAuth.uid.toString()
                                val user = UserDataModel(id, email, pass, username)

                                // Add user data to Firebase Realtime Database
                                database.child(id).setValue(user).addOnSuccessListener {

                                    // Save user ID to shared preferences
                                    val editor = sharedPreferences.edit()
                                    editor.apply {
                                        putString("STRING_KEY", id)
                                    }.apply()

                                    // Display success toast message using MotionToast library
                                    MotionToast.createColorToast(
                                        this,
                                        "SignUp Successful",
                                        "Please LogIn",
                                        MotionToastStyle.SUCCESS,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(
                                            this,
                                            www.sanju.motiontoast.R.font.helvetica_regular
                                        )
                                    )

                                    // Wait for 2 seconds and go to Register activity
                                    Handler().postDelayed({
                                        val intent = Intent(this, Register::class.java)
                                        startActivity(intent)
                                        finish()
                                    }, 2000)
                                }
                            } else {
                                // If creating user is not successful, display error message
                                Toast.makeText(
                                    this,
                                    task.exception?.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    // If passwords don't match, display error message
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                // If any field is empty, display error message
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}