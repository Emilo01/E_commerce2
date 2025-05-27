package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentFavoritesBinding
import com.farukayata.e_commerce2.ui.adapter.FavoritesAdapter
import com.farukayata.e_commerce2.ui.viewmodel.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private val viewModel: FavoritesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        // artık onRemoveClick yerine onFavoriteClick kullanılıyoruz
        val adapter = FavoritesAdapter(
            onFavoriteClick = { product ->
                product.isFavorite = !product.isFavorite
                viewModel.toggleFavorite(product) // favori durumu güncellendi
            },
            onProductClick = { product ->
                val action = FavoritesFragmentDirections.actionFavoritesFragmentToDetailFragment(product)
                findNavController().navigate(action)
            }
        )

        //ikili ızgara görünümü
        binding.recyclerViewFavorites.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerViewFavorites.adapter = adapter

        //favori ürünleri izledik
        lifecycleScope.launchWhenStarted {
            viewModel.favorites.collectLatest { favoriteList ->
                Log.d("FavoritesFragment", "Favori Listesi: $favoriteList")
                if (favoriteList.isEmpty()) {
                    binding.recyclerViewFavorites.visibility = View.GONE
                    binding.emptyFavoritesLayout.visibility = View.VISIBLE
                } else {
                    binding.recyclerViewFavorites.visibility = View.VISIBLE
                    binding.emptyFavoritesLayout.visibility = View.GONE
                    adapter.submitList(favoriteList)
                }
            }
        }

        binding.buttonExploreProducts.setOnClickListener {
            findNavController().navigate(R.id.action_favoritesFragment_to_homeFragment)
        }

        viewModel.loadFavorites()

        return binding.root
    }
}