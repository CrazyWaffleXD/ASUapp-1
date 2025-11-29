package com.example.asuapp001.ui.ad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.asuapp001.R
import com.example.asuapp001.databinding.FragmentAdBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.database
import java.util.Locale
import android.content.res.ColorStateList
import android.graphics.Color


class AdFragment : Fragment() {

    private var _binding: FragmentAdBinding? = null
    private val binding get() = _binding!!
    private val adAdapter = AdAdapter()

    private val allAds = mutableListOf<Ad>()        // Хранит все объявления
    private var currentFilterTag: String? = null    // Текущий тег фильтрации


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

        // Фильтры
        binding.chipGroupFilters.setOnCheckedChangeListener { _: ChipGroup, checkedId: Int ->
            val tag = when (checkedId) {
                R.id.chipAll -> null
                R.id.chipNews -> "новости"
                R.id.chipEvents -> "события"
                R.id.chipAnnounce -> "объявления"
                else -> null
            }
            filterByTag(tag)
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

                    // Читаем теги как список строк
                    val tagsSnapshot = adSnapshot.child("tags")
                    val tags = mutableListOf<String>()
                    if (tagsSnapshot.exists() && tagsSnapshot.childrenCount > 0) {
                        for (tag in tagsSnapshot.children) {
                            tag.value?.toString()?.let { tags.add(it) }
                        }
                    }

                    adsList.add(Ad(title, description, imageRes, tags))
                }

                allAds.clear()
                allAds.addAll(adsList)
                filterByTag(currentFilterTag) // Применяем фильтр
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибки
            }
        })
    }

    // Фильтрация объявлений по тегу
    private fun filterByTag(tag: String?) {
        currentFilterTag = tag
        val filteredList = if (tag.isNullOrEmpty()) {
            allAds
        } else {
            allAds.filter { it.tags.contains(tag) }
        }
        adAdapter.submitList(filteredList.toList()) {
            binding.recyclerViewAds.scrollToPosition(0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


data class Ad(
    val title: String,
    val description: String,
    val imageRes: Int,
    val tags: List<String>
)

class AdAdapter : androidx.recyclerview.widget.ListAdapter<Ad, AdAdapter.AdViewHolder>(AdDiffCallback()) {

    class AdViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        private val textTitle: android.widget.TextView = itemView.findViewById(R.id.textAdTitle)
        private val textDesc: android.widget.TextView = itemView.findViewById(R.id.textAdDescription)
        private val image: android.widget.ImageView = itemView.findViewById(R.id.imageAd)
        private val chipGroup: ChipGroup = itemView.findViewById(R.id.chipGroupTags)

        fun bind(item: Ad) {
            textTitle.text = item.title
            textDesc.text = item.description
            image.setImageResource(item.imageRes)

            // Очищаем и добавляем теги
            chipGroup.removeAllViews()
            for (tag in item.tags) {
                val chip = Chip(chipGroup.context).apply {
                    text = tag
                    isClickable = false
                    isCheckable = false

                    chipBackgroundColor = ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.new_light_grey)
                    )

                    // ✅ Используем свой стиль
                    setTextAppearance(R.style.CustomChipTextAppearance)
                }
                chipGroup.addView(chip)
            }
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