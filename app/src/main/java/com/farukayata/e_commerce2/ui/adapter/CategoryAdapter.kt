package com.farukayata.e_commerce2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.farukayata.e_commerce2.databinding.CategoryItemBinding

class CategoryAdapter(
    private val onCategoryClick: (String) -> Unit
) : ListAdapter<String, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    inner class CategoryViewHolder(private val binding: CategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: String) {
            binding.textViewCategoryName.text = category
            binding.root.setOnClickListener {
                onCategoryClick(category) // Tıklanan kategori ile ilgili işlem
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









/*
package com.farukayata.e_commerce2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.farukayata.e_commerce2.databinding.CategoryItemBinding

class CategoryAdapter(
    private val onCategoryClick: (String) -> Unit = {}
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private val categoryList = mutableListOf<String>()

    fun submitList(categories: List<String>) {
        categoryList.clear()
        categoryList.addAll(categories)
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(val binding: CategoryItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.binding.textViewCategoryName.text = category
        holder.binding.root.setOnClickListener { onCategoryClick(category) }
    }

    override fun getItemCount(): Int = categoryList.size
}


 */