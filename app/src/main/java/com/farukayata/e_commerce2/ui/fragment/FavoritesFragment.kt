package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.farukayata.e_commerce2.databinding.FragmentFavoritesBinding
import com.farukayata.e_commerce2.ui.adapter.FavoritesAdapter
import com.farukayata.e_commerce2.ui.viewmodel.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private val viewModel: FavoritesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        // FavoritesAdapter oluşturma
        //FavoriteAdapter yanında (emptyList()) vardı kaldırdık
        val adapter = FavoritesAdapter { productId ->
            viewModel.removeFavorite(productId)
        }

        // RecyclerView ayarları
        // yan yana olan görünüm tekli oluyor
        //binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(requireContext())
        //artık yan yana ikili olcak
        binding.recyclerViewFavorites.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        binding.recyclerViewFavorites.adapter = adapter

        // Favori ürünleri gözlemleme
        lifecycleScope.launchWhenStarted {
            viewModel.favorites.collect { favoriteList ->
                adapter.submitList(favoriteList)
            }
        }

        // Favori ürünleri yükleme
        viewModel.loadFavorites()

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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.farukayata.e_commerce2.databinding.FragmentFavoritesBinding
import com.farukayata.e_commerce2.ui.adapter.FavoritesAdapter
import com.farukayata.e_commerce2.ui.viewmodel.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private val viewModel: FavoritesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        // RecyclerView Ayarı
        val adapter = FavoritesAdapter(requireContext())
        binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFavorites.adapter = adapter

        // ViewModel'den gelen favori ürünleri lifecycleScope ile gözlemleme
        lifecycleScope.launchWhenStarted {
            viewModel.favoritesState.collect { favorites ->
                adapter.submitList(favorites)
            }
        }

        // Favori ürünleri yükleme
        viewModel.loadFavorites()

        return binding.root
    }
}


 */