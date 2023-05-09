package com.example.swifttasks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swifttasks.databinding.DialogconfirmBinding
import com.example.swifttasks.databinding.DialogtaskBinding
import com.example.swifttasks.databinding.FragmentNewProjectBinding
import com.example.swifttasks.databinding.FragmentProjectsBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class Projects : Fragment(R.layout.fragment_projects) {
    private lateinit var binding: FragmentProjectsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProjectsBinding.inflate(inflater, container, false)
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
        val projectList = mutableListOf<ProjectModel>()
        val myRef = database.getReference("Users").child(firebaseAuth.uid.toString()).child("Projects")
        val taskKeyMap = mutableMapOf<Int, String>()
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                projectList.clear()
                var position = 0
                for (dataSnapshot in snapshot.children) {
                    val user = dataSnapshot.getValue(ProjectModel::class.java)
                    user?.let { projectList.add(it) }
                    val Key = dataSnapshot.key
                    taskKeyMap[position] = Key.toString()
                    position++
                }
                if(isAdded){
                    binding.rcp.apply {
                        layoutManager = LinearLayoutManager(this.context)
                        adapter = ProjectAdapter(requireContext(), projectList).apply {
                                setOnItemClickListener(object : ProjectAdapter.onItemClickListener {
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
                                }
                            })
                            setOnItemLongClickListener(object : ProjectAdapter.onItemLongClickListener {
                                override fun onItemLongClick(position: Int) {
                                    val taskKey = taskKeyMap[position]
                                    val binding = DialogconfirmBinding.inflate(layoutInflater)
                                    val view = binding.root
                                    val builder = AlertDialog.Builder(requireActivity())
                                    builder.setView(view)

                                    val dialog = builder.create()
                                    dialog.show()
                                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                                    dialog.setCancelable(false)

                                    binding.snd.setOnClickListener {
                                        val taskRef = FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().uid!!)
                                            .child("Projects")
                                            .child(taskKey.toString())
                                        taskRef.removeValue().addOnCompleteListener {
                                            dialog.dismiss()
                                        }
                                    }
                                    binding.cn.setOnClickListener {
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
        binding.add.setOnClickListener {
            // Replace the current fragment with the Product fragment
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.mn, NewProject())
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}