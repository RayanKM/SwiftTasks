package com.example.swifttasks

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.swifttasks.databinding.ActivityMainBinding
import com.example.swifttasks.databinding.FragmentHomeBinding
import com.example.swifttasks.databinding.FragmentNewProjectBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.math.log

class Home : Fragment(R.layout.fragment_home) {

    // An interface that defines a callback method for button clicks.
    interface OnButtonClickListener {
        fun onButtonClicked()
    }


    // A private variable that holds a reference to the OnButtonClickListener interface.
    private lateinit var listener: OnButtonClickListener

    // This method is called when the fragment is attached to an activity.
    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Check if the activity implements the OnButtonClickListener interface.
        if (context is OnButtonClickListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnButtonClickListener")
        }
    }

    private lateinit var binding: FragmentHomeBinding
    private lateinit var binding2: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var pr : Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding2 = ActivityMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseApp.initializeApp(requireContext())
        firebaseAuth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        val myRef2 = database.getReference("Users").child(firebaseAuth.uid.toString()).child("username")
        myRef2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.getValue(String::class.java)
                binding.hallo.text = "Hello\n$username"
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
        val projectList = mutableListOf<ProjectModel>()
        val myRef = database.getReference("Users").child(firebaseAuth.uid.toString()).child("Projects")
        val taskKeyMap = mutableMapOf<Int, String>()
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var position = 0
                var ts = 0
                var count = 0
                if (snapshot.exists()){
                    projectList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val user = dataSnapshot.getValue(ProjectModel::class.java)
                        user?.let { projectList.add(it) }
                        val Key = dataSnapshot.key
                        taskKeyMap[position] = Key.toString()
                        position++
                        // Get tasks node
                        val tasksNode = dataSnapshot.child("tasks")
                        for (taskSnapshot in tasksNode.children) {
                            // Get task status and log it
                            val status = taskSnapshot.child("status").getValue(Boolean::class.java)
                            if (status == true){
                                ts += 1
                            }
                            count += 1
                        }
                    }
                    val pross = (ts.toDouble()/count.toDouble())*100
                    binding.progressPr.text = "${pross.toInt()}%"
                    binding.progressBar.progress = pross.toInt()
                    if(isAdded){
                        binding.mainRecyclerview.apply {
                            layoutManager = GridLayoutManager(requireActivity().applicationContext, 2)
                            adapter = HomeAdapter(requireContext(), projectList).apply {
                                setOnItemClickListener(object : HomeAdapter.onItemClickListener {
                                    override fun onItemClick(position: Int) {
                                        val taskKey = taskKeyMap[position]
                                        val fragment = ProjectDetails()
                                        val bundle = Bundle()
                                        bundle.putString("my_key", taskKey)
                                        fragment.arguments = bundle
                                        val transaction = requireActivity().supportFragmentManager.beginTransaction()
                                        transaction.replace(R.id.mn, fragment) // replace with MyFragment instead of ProjectDetails
                                        transaction.addToBackStack(null)
                                        transaction.commit()
                                        listener.onButtonClicked()
                                    }
                                })
                            }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
        binding.psj.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.mn, Projects()) // replace with MyFragment instead of ProjectDetails
            transaction.addToBackStack(null)
            transaction.commit()
            listener.onButtonClicked()
        }
    }
}