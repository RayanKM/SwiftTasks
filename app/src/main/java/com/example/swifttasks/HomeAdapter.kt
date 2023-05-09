package com.example.swifttasks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HomeAdapter (private val context: Context, private val users: MutableList<ProjectModel>) :
    RecyclerView.Adapter<UserViewHolder>() {

    private lateinit var mListenerA: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListenerA = listener
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.tasklist, parent, false),mListenerA
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        val user = users[position]
        holder.userName.text = user.projectType
        holder.userImage.clipToOutline = true
        val trueStatusCount = user.tasks?.count { it?.status == true } ?: 0
        val tasks = user.tasks?.filterNotNull()?.size ?: 0
        holder.dnn.text = "$trueStatusCount/$tasks"
        var rs = (trueStatusCount.toDouble()/tasks.toDouble())*100
        holder.pg.progress = rs.toInt()
        if (user.projectType == "Books" ){
            holder.userImage.setImageResource(R.drawable.book)
        }
        else if (user.projectType == "Emails" ){
            holder.userImage.setImageResource(R.drawable.email)
        }
        else if (user.projectType == "Urgent" ){
            holder.userImage.setImageResource(R.drawable.urgent)
        }
        else if (user.projectType == "Work" ){
            holder.userImage.setImageResource(R.drawable.wrok)
        }
    }
}

class UserViewHolder(itemView: View, listener: HomeAdapter.onItemClickListener) : RecyclerView.ViewHolder(itemView) {

    val userName: TextView = itemView.findViewById(R.id.title)
    val userImage: ImageView = itemView.findViewById(R.id.img)
    val dnn: TextView = itemView.findViewById(R.id.dnn)
    val pg : ProgressBar = itemView.findViewById(R.id.progressBar)
    init {
        itemView.setOnClickListener {
            listener.onItemClick(adapterPosition)
        }
    }
}
