package com.example.swifttasks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProjectAdapter (private val context: Context, private val users: MutableList<ProjectModel>) :
    RecyclerView.Adapter<UserViewHolder2>() {

    private lateinit var mListenerA: onItemClickListener
    private lateinit var mListenerB: onItemLongClickListener


    interface onItemClickListener {
        fun onItemClick(position: Int)
    }
    interface onItemLongClickListener {
        fun onItemLongClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListenerA = listener
    }
    fun setOnItemLongClickListener(listener: onItemLongClickListener) {
        mListenerB = listener
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder2 {
        return UserViewHolder2(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.projectlist, parent, false),mListenerA,mListenerB

        )
    }

    override fun onBindViewHolder(holder: UserViewHolder2, position: Int) {

        val user = users[position]
        holder.userName.text = user.projectType
        holder.userImage.clipToOutline = true
        if (user.projectType == "Books" ){
            holder.userImage.setImageResource(R.drawable.book)
        }else if (user.projectType == "Emails" ){
            holder.userImage.setImageResource(R.drawable.email)
        }else if (user.projectType == "Urgent" ){
            holder.userImage.setImageResource(R.drawable.urgent)
        }else if (user.projectType == "Work" ){
            holder.userImage.setImageResource(R.drawable.wrok)
        }

    }
}

class UserViewHolder2(itemView: View, listener: ProjectAdapter.onItemClickListener, longClickListener: ProjectAdapter.onItemLongClickListener) : RecyclerView.ViewHolder(itemView) {

    val userName: TextView = itemView.findViewById(R.id.title)
    val userImage: ImageView = itemView.findViewById(R.id.img)

    init {
        itemView.setOnClickListener {
            listener.onItemClick(adapterPosition)
        }

        itemView.setOnLongClickListener {
            longClickListener.onItemLongClick(adapterPosition)
            true
        }
    }
}

