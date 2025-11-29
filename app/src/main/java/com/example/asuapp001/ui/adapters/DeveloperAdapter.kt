package com.example.asuapp001.ui.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.asuapp001.R // <- Импорт R
import com.example.asuapp001.data.Developer // <- Импорт Developer

class DeveloperAdapter(private val developers: List<Developer>) :
    RecyclerView.Adapter<DeveloperAdapter.DeveloperViewHolder>() {

    class DeveloperViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatarImageView: ImageView = itemView.findViewById(R.id.avatarImageView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val roleTextView: TextView = itemView.findViewById(R.id.roleTextView)
        val groupTextView: TextView = itemView.findViewById(R.id.groupTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val githubIcon: ImageView = itemView.findViewById(R.id.githubIcon)
        val githubText: TextView = itemView.findViewById(R.id.githubText)
        val emailIcon: ImageView = itemView.findViewById(R.id.emailIcon)
        val emailText: TextView = itemView.findViewById(R.id.emailText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeveloperViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_developer, parent, false)
        return DeveloperViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeveloperViewHolder, position: Int) {
        val developer = developers[position]

        holder.avatarImageView.setImageResource(developer.avatarResId)
        holder.nameTextView.text = developer.name
        holder.roleTextView.text = developer.role
        holder.groupTextView.text = "Группа: ${developer.group}"
        holder.descriptionTextView.text = developer.descriptionText

        // Обработчики кликов
        holder.githubText.setOnClickListener {
            openUrl(holder.itemView.context, developer.githubUrl)
        }
        holder.emailText.setOnClickListener {
            sendEmail(holder.itemView.context, developer.email)
        }
    }

    override fun getItemCount() = developers.size

    private fun openUrl(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    private fun sendEmail(context: Context, email: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
        }
        context.startActivity(intent)
    }
}