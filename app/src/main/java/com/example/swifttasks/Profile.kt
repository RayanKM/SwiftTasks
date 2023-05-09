package com.example.swifttasks

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.example.swifttasks.databinding.AddtaskdialogBinding
import com.example.swifttasks.databinding.ChangeusernameBinding
import com.example.swifttasks.databinding.FragmentNewProjectBinding
import com.example.swifttasks.databinding.FragmentProfileBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class Profile : Fragment(R.layout.fragment_profile) {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.profile.clipToOutline = true

        val sharedPreferences = this.activity?.getSharedPreferences("SV", Context.MODE_PRIVATE)
        FirebaseApp.initializeApp(requireContext())
        firebaseAuth = FirebaseAuth.getInstance()

        val database = FirebaseDatabase.getInstance()
        val myRef2 = database.getReference("Users").child(firebaseAuth.uid.toString()).child("email")
        myRef2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val email = snapshot.getValue(String::class.java)
                binding.eml.text = "$email"
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        binding.button.setOnClickListener {
            val binding = ChangeusernameBinding.inflate(layoutInflater)
            val view = binding.root
            val builder = AlertDialog.Builder(requireActivity())
            builder.setView(view)

            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCancelable(false)

            binding.snd.setOnClickListener {
                if (binding.taga.text.toString().isNotEmpty()) {
                    val username = binding.taga.text.toString()
                    val myRef = database.getReference("Users").child(firebaseAuth.uid.toString())
                        .child("username")
                    myRef.setValue(username).addOnCompleteListener {
                        dialog.dismiss()
                    }
                } else {
                    MotionToast.createColorToast(
                        requireActivity(),
                        "Empty task",
                        "Please add a task",
                        MotionToastStyle.WARNING,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(
                            requireContext(),
                            www.sanju.motiontoast.R.font.helvetica_regular
                        )
                    )
                }
            }
            binding.cn.setOnClickListener {
                dialog.dismiss()

            }
        }

        binding.lg.setOnClickListener {
            val editor = sharedPreferences?.edit()
            editor?.apply {
                putString("STRING_KEY", "0")
            }?.apply()
            val intent = Intent(this.activity, Login::class.java)
            startActivity(intent)
        }
    }
}