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
    private lateinit var binding: ActivityLoginBinding
    private lateinit var database : DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.textView.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
        binding.textView2.setOnClickListener {
            val binding = DialogView6Binding.inflate(layoutInflater)
            val view = binding.root
            val builder = AlertDialog.Builder(this)
            builder.setView(view)
            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCancelable(false)
            binding.snd.setOnClickListener {
                if (binding.taga.text.toString().isNotEmpty()){
                    val tg = binding.taga.text.toString()
                    dialog.dismiss()
                    firebaseAuth.sendPasswordResetEmail(tg).addOnSuccessListener {
                        MotionToast.createColorToast(this,
                            "Done!",
                            "We will send a password reset to your email soon",
                            MotionToastStyle.SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular))
                    }.addOnFailureListener {
                        MotionToast.createColorToast(this,
                            "Error!",
                            "The account may not exist",
                            MotionToastStyle.WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular))
                    }
                }else {
                    MotionToast.createColorToast(this,
                        "Please fill all the details",
                        "Thank you",
                        MotionToastStyle.WARNING,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular))
                }
            }
            binding.cn.setOnClickListener {
                dialog.dismiss()
            }
        }
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        database = FirebaseDatabase.getInstance().getReference("Users")
                        database.child(FirebaseAuth.getInstance().uid.toString()).child("country").get().addOnSuccessListener {
                            val intent = Intent(this, MainActivity::class.java)
                            val sharedPreferences = getSharedPreferences("SV", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.apply {
                                putString("STRING_KEY", firebaseAuth.uid.toString())
                            }.apply()
                            startActivity(intent)                        }
                    } else {
                        Toast.makeText(this, it.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onStart() {
        super.onStart()
        val sharedPreferences = getSharedPreferences("SV", MODE_PRIVATE)
        val KID = sharedPreferences.getString("STRING_KEY", "0")
        Log.d("tsdqs^fqez", KID.toString())
        Log.d("tsdqs^fqez", firebaseAuth.uid.toString())
        if (firebaseAuth.uid.toString() == KID){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}