package com.example.asuapp001.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.asuapp001.R
import com.example.asuapp001.databinding.FragmentAdBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.database

class AdFragment : Fragment() {

    private var _binding: FragmentAdBinding? = null
    private val binding get() = _binding!!
    private val adAdapter = AdAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewAds.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adAdapter
        }

        loadAdsFromFirebase()
    }

    private fun loadAdsFromFirebase() {
        val database = Firebase.database
        val adsRef = database.getReference("ads")

        adsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adsList = mutableListOf<Ad>()

                for (adSnapshot in snapshot.children) {
                    val title = adSnapshot.child("title").value as? String ?: "Без названия"
                    val description = adSnapshot.child("description").value as? String ?: "Нет описания"
                    val imageUrl = adSnapshot.child("imageUrl").value as? String ?: ""

                    val imageRes = when (imageUrl.lowercase()) {
                        "l2025" -> R.drawable.l2025
                        "santa_hat" -> R.drawable.santa_hat
                        else -> R.drawable.logo
                    }

                    adsList.add(Ad(title, description, imageRes))
                }

                adAdapter.submitList(adsList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Логировать: error.toException()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class Ad(
    val title: String,
    val description: String,
    val imageRes: Int
)

class AdAdapter : androidx.recyclerview.widget.ListAdapter<Ad, AdAdapter.AdViewHolder>(AdDiffCallback()) {

    class AdViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        private val textTitle: android.widget.TextView = itemView.findViewById(R.id.textAdTitle)
        private val textDesc: android.widget.TextView = itemView.findViewById(R.id.textAdDescription)
        private val image: android.widget.ImageView = itemView.findViewById(R.id.imageAd)

        fun bind(item: Ad) {
            textTitle.text = item.title
            textDesc.text = item.description
            image.setImageResource(item.imageRes)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ad_card, parent, false)
        return AdViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class AdDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<Ad>() {
    override fun areItemsTheSame(oldItem: Ad, newItem: Ad): Boolean = oldItem.title == newItem.title
    override fun areContentsTheSame(oldItem: Ad, newItem: Ad): Boolean = oldItem == newItem
}