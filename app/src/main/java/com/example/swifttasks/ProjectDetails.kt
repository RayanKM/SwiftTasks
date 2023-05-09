package com.example.swifttasks

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swifttasks.databinding.AddtaskdialogBinding
import com.example.swifttasks.databinding.DialogtaskBinding
import com.example.swifttasks.databinding.FragmentProjectDetailsBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import kotlin.math.absoluteValue

class ProjectDetails : Fragment(R.layout.fragment_project_details) {
    private lateinit var binding: FragmentProjectDetailsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var pr : Int = 0



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProjectDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myValue = arguments?.getString("my_key")
        FirebaseApp.initializeApp(requireContext())
        firebaseAuth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        val projectList = mutableListOf<ProjectModel>()
        val myRef = database.getReference("Users").child(firebaseAuth.uid.toString()).child("Projects").child(myValue.toString())
        val taskKeyMap = mutableMapOf<Int, String>()
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                projectList.clear()
                val user = snapshot.getValue(ProjectModel::class.java)
                user?.let { projectList.add(it) }

                binding.pjd.text = user?.projectDescription
                if(isAdded){
                    binding.prc.apply {
                        layoutManager = LinearLayoutManager(this.context)
                        val tasks = user?.tasks?.filterNotNull()
                        tasks?.let {
                            var position = 0
                            for (taskSnapshot in snapshot.child("tasks").children) {
                                val taskKey = taskSnapshot.key
                                if (taskKey != null) {
                                    taskKeyMap[position] = taskKey
                                }
                                position++
                            }
                        }
                        adapter = TasksAdapter(requireContext(),
                            tasks as MutableList<SubTaskModel>?
                        ).apply {
                            setOnItemClickListener(object : TasksAdapter.onItemClickListener {
                                override fun onItemClick(position: Int) {
                                    val taskKey = taskKeyMap[position]
                                    val binding = DialogtaskBinding.inflate(layoutInflater)
                                    val view = binding.root
                                    val builder = AlertDialog.Builder(requireActivity())
                                    builder.setView(view)

                                    val dialog = builder.create()
                                    dialog.show()
                                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                                    dialog.setCancelable(false)

                                    binding.cq.setOnClickListener {
                                        val taskRef = FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().uid!!)
                                            .child("Projects")
                                            .child(myValue.toString())
                                            .child("tasks")
                                            .child(taskKey.toString())
                                            .child("status")
                                        taskRef.setValue(true).addOnCompleteListener {
                                            dialog.dismiss()
                                        }
                                    }
                                    binding.dlt.setOnClickListener {
                                        val taskRef = FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().uid!!)
                                            .child("Projects")
                                            .child(myValue.toString())
                                            .child("tasks")
                                            .child(taskKey.toString())
                                        taskRef.removeValue().addOnCompleteListener {
                                            dialog.dismiss()
                                        }
                                    }
                                    binding.cp.setOnClickListener {
                                        dialog.dismiss()
                                    }
                                }
                            })
                        }
                    }
                }

            }
            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        binding.button.setOnClickListener {
            val binding = AddtaskdialogBinding.inflate(layoutInflater)
            val view = binding.root
            val builder = AlertDialog.Builder(requireActivity())
            builder.setView(view)

            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCancelable(false)

            binding.snd.setOnClickListener {
                if (binding.taga.text.toString().isNotEmpty()){
                    val task = binding.taga.text.toString()
                    val tsk = SubTaskModel(task,false)
                    val myRef = database.getReference("Users").child(firebaseAuth.uid.toString()).child("Projects").child(myValue.toString()).child("tasks")
                    // Retrieve the current list of tasks from Firebase
                    myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val tasksList = snapshot.getValue()
                            val newTasksList = mutableListOf<SubTaskModel>()
                            if (tasksList != null) {
                                // If the tasks list already exists, add the new task to it
                                newTasksList.addAll(tasksList as Collection<SubTaskModel>)
                            }
                            // Add the new task to the list
                            newTasksList.add(tsk)
                            // Set the updated list back to Firebase
                            myRef.setValue(newTasksList).addOnCompleteListener {
                                dialog.dismiss()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle error
                        }
                    })
                }
                else {
                    MotionToast.createColorToast(requireActivity(),
                        "Empty task",
                        "Please add a task",
                        MotionToastStyle.WARNING,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular))
                }
            }
            binding.cn.setOnClickListener {
                dialog.dismiss()
            }
        }
    }
}