package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.farukayata.e_commerce2.databinding.FragmentCategoryBinding
import com.farukayata.e_commerce2.ui.adapter.CategoryAdapter
import com.farukayata.e_commerce2.ui.viewmodel.CategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.fragment.findNavController


@AndroidEntryPoint
class CategoryFragment : Fragment() {

    private lateinit var binding: FragmentCategoryBinding
    private val viewModel: CategoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)

        // adapter oluştur ve tıklama olayını bağla
        val adapter = CategoryAdapter { selectedCategory ->
            val action = CategoryFragmentDirections.actionCategoryFragmentToCategorySpecialFragment(selectedCategory)
            findNavController().navigate(action)
        }

        binding.recyclerViewCategory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCategory.adapter = adapter

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            adapter.submitList(categories)
        }

        viewModel.loadCategories()

        return binding.root
    }
}

