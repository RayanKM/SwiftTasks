package com.example.swifttasks

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.example.swifttasks.databinding.AddtaskdialogBinding
import com.example.swifttasks.databinding.FragmentNewProjectBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class NewProject : Fragment(R.layout.fragment_new_project) {
    private lateinit var binding: FragmentNewProjectBinding
    private var checked: String? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewProjectBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseApp.initializeApp(requireContext())
        firebaseAuth = FirebaseAuth.getInstance()
        val tasks = mutableListOf<SubTaskModel>() // Use a list of SubTaskModel instead of Any
        val adapter = ArrayAdapter(requireContext(), R.layout.addtasklist, tasks.map { it.task }) // Map tasks to their respective tasks
        binding.listtasks.adapter = adapter
        val checkBoxes = listOf(binding.checkbook, binding.checkmail, binding.checkwork, binding.checkurgent)

        for (checkBox in checkBoxes) {
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    for (otherCheckBox in checkBoxes) {
                        if (otherCheckBox != checkBox) {
                            otherCheckBox.isChecked = false
                        }
                    }
                    // This is the currently checked checkbox
                    checked = checkBox.text.toString()
                    Toast.makeText(requireContext(), checked, Toast.LENGTH_SHORT).show()

                }
            }
        }
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
                    adapter.add(task)
                    tasks.add(tsk)
                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
                    //fix the view it shows both
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

        binding.addp.setOnClickListener {
            if (checked != null){
                Log.d("tsdqs^fqez", checked.toString())
                database = FirebaseDatabase.getInstance().getReference("Users")
                val id = firebaseAuth.uid.toString()
                val Projects = ProjectModel(checked,binding.PD.text.toString(),tasks)
                database.child(id).child("Projects").push().setValue(Projects).addOnSuccessListener {
                    Toast.makeText(requireContext(), "DONE", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Log.d("tsdqs^fqez", "failed")
                }
            }
        }

        binding.cancel.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.mn, Projects())
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}