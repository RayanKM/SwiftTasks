package com.example.swifttasks

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.example.swifttasks.databinding.ChangeusernameBinding
import com.example.swifttasks.databinding.FragmentProfileBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.text.SimpleDateFormat
import java.util.*

data class TimeResponse(
    val datetime: String
)
interface TimeApi {
    @GET("api/timezone/{timezone}")
    suspend fun getTime(@Path("timezone") timezone: String): TimeResponse
}
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

        val retrofit = Retrofit.Builder()
            .baseUrl("http://worldtimeapi.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //here is the api
        val api = retrofit.create(TimeApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.getTime("Europe/London")
                val receivedTime = response.datetime
                val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.getDefault())
                val parsedDate = inputDateFormat.parse(receivedTime)
                val outputDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val formattedTime = outputDateFormat.format(parsedDate)
                withContext(Dispatchers.Main) {
                    binding.tms.text = "Your current time is : $formattedTime"
                }


            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d("sdfqsdqdq","Error: ${e.message}" )
                }
            }
        }


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

