package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.farukayata.e_commerce2.databinding.FragmentCategorySpecialBinding
import com.farukayata.e_commerce2.ui.adapter.EcommorceAdapter
import com.farukayata.e_commerce2.ui.viewmodel.CategorySpecialViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategorySpecialFragment : Fragment() {

    private lateinit var binding: FragmentCategorySpecialBinding
    private val viewModel: CategorySpecialViewModel by viewModels()
    private val args: CategorySpecialFragmentArgs by navArgs() // Argümanları al

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategorySpecialBinding.inflate(inflater, container, false)

        val adapter = EcommorceAdapter(requireContext()) { product ->
            // Ürün detayına yönlendirme
        }

        binding.recyclerViewCategorySpecial.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCategorySpecial.adapter = adapter

        viewModel.products.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
        }

        viewModel.loadProductsByCategory(args.categoryName) // Seçilen kategoriye göre ürünleri yükle

        return binding.root
    }
}







/*
package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.farukayata.e_commerce2.databinding.FragmentCategorySpecialBinding
import com.farukayata.e_commerce2.ui.adapter.EcommorceAdapter
import com.farukayata.e_commerce2.ui.viewmodel.CategorySpecialViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategorySpecialFragment : Fragment() {

    private lateinit var binding: FragmentCategorySpecialBinding
    private val viewModel: CategorySpecialViewModel by viewModels()
    private val args: CategorySpecialFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategorySpecialBinding.inflate(inflater, container, false)

        val adapter = EcommorceAdapter(requireContext(), emptyList()) { product ->
            // Detay sayfasına geçiş
        }
        binding.recyclerViewCategorySpecial.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCategorySpecial.adapter = adapter

        viewModel.categoryProducts.observe(viewLifecycleOwner) { products ->
            adapter.updateList(products) // EcommorceAdapter içinde bir updateList işlevi eklemelisiniz
        }

        viewModel.loadCategoryProducts(args.categoryName) // Kategoriyi ViewModel'e gönder
        return binding.root
    }
}

 */
