package com.farukayata.e_commerce2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.CategoryItemBinding

class CategoryAdapter(
    private val onCategoryClick: (String) -> Unit
) : ListAdapter<String, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    // Kategori isimleri ile drawable kaynaklarını eşleştiriyoruz
    private val categoryImageMap = mapOf(
        "electronics" to R.drawable.electronics,
        "jewelery" to R.drawable.jewelry,
        "men's clothing" to R.drawable.mens_clothing,
        "women's clothing" to R.drawable.womans_clothing
    )

    inner class CategoryViewHolder(private val binding: CategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: String) {
            binding.textViewCategoryName.text = category

            // Eğer kategoriye ait bir resim varsa gösterdik
            categoryImageMap[category]?.let { imageResId ->
                binding.imageViewCategory.setImageResource(imageResId)
            }

            // Varsayılan olarak beyaz renk
            binding.cardViewCategory.setCardBackgroundColor(
                ContextCompat.getColor(binding.root.context, R.color.card_default)
            )

            binding.root.setOnClickListener {
                // Kategoriye tıklandığında rengi değiştirdik
                binding.cardViewCategory.setCardBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.card_selected)
                )

                // 300ms sonra tekrar eski haline getir (parlama efekti gibi)
                //bu çok işe yaramıyor gibi geliyor
                binding.cardViewCategory.postDelayed({
                    binding.cardViewCategory.setCardBackgroundColor(
                        ContextCompat.getColor(binding.root.context, R.color.card_default)
                    )
                }, 300)

                // Tıklanan kategoriyi işle
                onCategoryClick(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = CategoryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
    }
}
