package com.example.swifttasks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.example.swifttasks.databinding.ActivityLoginBinding
import com.example.swifttasks.databinding.DialogView6Binding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding // declare a lateinit var of type ActivityLoginBinding named binding
    private lateinit var database: DatabaseReference // declare a lateinit var of type DatabaseReference named database
    private lateinit var firebaseAuth: FirebaseAuth // declare a lateinit var of type FirebaseAuth named firebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) { // override the onCreate() function
        super.onCreate(savedInstanceState) // call the super class's onCreate() function
        binding =
            ActivityLoginBinding.inflate(layoutInflater) // inflate the view using the ActivityLoginBinding class and assign it to the binding variable
        setContentView(binding.root) // set the content view of the activity to the root view of the inflated layout
        FirebaseApp.initializeApp(this) // initialize the FirebaseApp with the current context
        firebaseAuth =
            FirebaseAuth.getInstance() // initialize the firebaseAuth variable with the instance of the FirebaseAuth class
        binding.textView.setOnClickListener { // set an onClickListener for the textView in the layout
            val intent = Intent(
                this,
                Register::class.java
            ) // create an Intent to navigate to the Register activity
            startActivity(intent) // start the activity using the created Intent
        }
        binding.textView2.setOnClickListener { // set an onClickListener for the textView2 in the layout
            val binding =
                DialogView6Binding.inflate(layoutInflater) // inflate the view using the DialogView6Binding class and assign it to a variable named binding
            val view =
                binding.root // get the root view of the inflated layout and assign it to a variable named view
            val builder =
                AlertDialog.Builder(this) // create an instance of the AlertDialog.Builder class with the current context as argument and assign it to a variable named builder
            builder.setView(view) // set the view of the AlertDialog.Builder to the root view of the inflated layout
            val dialog =
                builder.create() // create an AlertDialog from the builder and assign it to a variable named dialog
            dialog.show() // show the created dialog
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // set the background color of the dialog's window to transparent
            dialog.setCancelable(false) // disable the cancelable property of the dialog

            binding.snd.setOnClickListener { // set an onClickListener for the snd button in the layout
                if (binding.taga.text.toString()
                        .isNotEmpty()
                ) { // check if the taga EditText is not empty
                    val tg =
                        binding.taga.text.toString() // get the text from the taga EditText and assign it to a variable named tg
                    dialog.dismiss() // dismiss the dialog
                    firebaseAuth.sendPasswordResetEmail(tg)
                        .addOnSuccessListener { // send a password reset email to the user's email
                            MotionToast.createColorToast(
                                this,
                                "Done!",
                                "We will send a password reset to your email soon",
                                MotionToastStyle.SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(
                                    this,
                                    www.sanju.motiontoast.R.font.helvetica_regular
                                )
                            )
                        }.addOnFailureListener { // if sending the email fails, show an error toast
                        MotionToast.createColorToast(
                            this,
                            "Error!",
                            "The account may not exist",
                            MotionToastStyle.WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(
                                this,
                                www.sanju.motiontoast.R.font.helvetica_regular
                            )
                        )
                    }
                } else { // if the taga EditText is empty, show a warning toast
                    MotionToast.createColorToast(
                        this,
                        "Please fill all the details",
                        "Thank you",
                        MotionToastStyle.WARNING,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(
                            this,
                            www.sanju.motiontoast.R.font.helvetica_regular
                        )
                    )
                }
            }

            binding.cn.setOnClickListener { // set an onClickListener for the cn button in the layout to dismiss the dialog
                dialog.dismiss()
            }
        }


/*

Set an OnClickListener on the button view using the binding object.
*/
        binding.button.setOnClickListener {
// Get the email and password entered by the user from the EditText views
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

// Check if email and password are not empty
            if (email.isNotEmpty() && pass.isNotEmpty()) {
// Sign in the user using the entered email and password
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
// If the sign-in is successful
                    if (it.isSuccessful) {
// Get a reference to the "Users" node in the Firebase Realtime Database
                        database = FirebaseDatabase.getInstance().getReference("Users")
// Get the "country" value for the current user from the Realtime Database
                        database.child(FirebaseAuth.getInstance().uid.toString()).child("country")
                            .get().addOnSuccessListener {
// If getting the value is successful, start the MainActivity and save the user ID in SharedPreferences
                            val intent = Intent(this, MainActivity::class.java)
                            val sharedPreferences = getSharedPreferences("SV", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.apply {
                                putString("STRING_KEY", firebaseAuth.uid.toString())
                            }.apply()
                            startActivity(intent)
                        }
                    } else {
// If sign-in is not successful, show an error message using a Toast
                        Toast.makeText(this, it.exception?.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
// If email or password is empty, show an error message using a Toast
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val sharedPreferences = getSharedPreferences("SV", MODE_PRIVATE)
        val KID =
            sharedPreferences.getString("STRING_KEY", "0") // Get the UID from shared preferences
        if (firebaseAuth.uid.toString() == KID) { // Check if the current user's UID matches the one stored in shared preferences
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) // If it matches, start the MainActivity
        }
    }
}