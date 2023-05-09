package com.example.swifttasks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TasksAdapter(private val context: Context, private val users: MutableList<SubTaskModel>?) :
    RecyclerView.Adapter<UserViewHolder3>() {

    private lateinit var mListenerA: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }


    fun setOnItemClickListener(listener: onItemClickListener){
        mListenerA = listener
    }


    override fun getItemCount(): Int {
        return users?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder3 {
        return UserViewHolder3(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.taskdetail, parent, false),mListenerA
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder3, position: Int) {

        val user = users?.get(position)
        if (user != null) {
            holder.userName.text = user.task.toString()
        }
        if (user?.status == true){
            holder.img.setImageResource(R.drawable.check)
        }else {
            holder.img.setImageResource(R.drawable.nyet)
        }
    }
}

class UserViewHolder3(itemView: View, listenerA: TasksAdapter.onItemClickListener) : RecyclerView.ViewHolder(itemView) {

    val userName: TextView = itemView.findViewById(R.id.title)
    val img: ImageView = itemView.findViewById(R.id.gc)

    init {
        itemView.setOnClickListener {
            listenerA.onItemClick(adapterPosition)
        }
    }
}
