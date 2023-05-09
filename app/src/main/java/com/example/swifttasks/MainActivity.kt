package com.example.swifttasks

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.CharSequenceTransformation
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.example.swifttasks.databinding.ActivityMainBinding
import com.example.swifttasks.databinding.ActivityRegisterBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(),Home.OnButtonClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var calendar: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        turnNotf()
        setAlarm()

        binding.profile.clipToOutline = true

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.mn, Home())
            commit()
        }
        binding.Navbt.setItemSelected(R.id.Home)
        binding.Navbt.setOnItemSelectedListener {
            when (it) {
                R.id.Task -> {
                    binding.top.visibility = View.VISIBLE
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.mn, Projects())
                        commit()
                        binding.title.text = "Projects"
                    }
                }
                R.id.Home -> {
                    binding.top.visibility = View.VISIBLE
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.mn, Home())
                        commit()
                        binding.title.text = "Home"
                    }
                }
            }
        }
        binding.profile.setOnClickListener {
            binding.top.visibility = View.GONE
            if (binding.Navbt.getSelectedItemId() == R.id.Home) {
                binding.Navbt.setItemSelected(R.id.Home, false)
            } else {
                binding.Navbt.setItemSelected(R.id.Task, false)
            }
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.mn, Profile())
                commit()
            }
        }
    }
    override fun onButtonClicked() {
        binding.Navbt.setItemSelected(R.id.Task)
    }
    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
            fragmentManager.addOnBackStackChangedListener {
                val currentFragment = fragmentManager.findFragmentById(R.id.mn)
                if (currentFragment is Home){
                    binding.Navbt.setItemSelected(R.id.Home)
                }else{
                    binding.Navbt.setItemSelected(R.id.Task)
                }
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun setAlarm(){
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val toastMessage = "The current time is: $currentTime"
        Toast.makeText(applicationContext, toastMessage, Toast.LENGTH_SHORT).show()

        calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = 9
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this,NotificationReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0)

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,pendingIntent
        )
        Toast.makeText(this,"DONE ALARM",Toast.LENGTH_SHORT).show()
    }

    private fun turnNotf(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name : CharSequence = "nodsfsdftf"
            val description = "do task"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("notf",name,importance)
            channel.description = description
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}