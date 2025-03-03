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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentFavoritesBinding
import com.farukayata.e_commerce2.ui.adapter.EcommorceAdapter
import com.farukayata.e_commerce2.ui.adapter.FavoritesAdapter
import com.farukayata.e_commerce2.ui.viewmodel.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
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

        // FavoritesAdapter oluşturma
        //FavoriteAdapter yanında (emptyList()) vardı kaldırdık
//        val adapter = FavoritesAdapter { productId ->
//            viewModel.removeFavorite(productId)
//        }


        // FavoritesAdapter oluşturma
        val adapter = FavoritesAdapter(
            onRemoveClick = { productId ->
                // Favorilerden kaldırma işlemi
                viewModel.removeFavorite(productId)
            },
            onProductClick = { product ->
                // Ürün detayına yönlendirme
                val action = FavoritesFragmentDirections.actionFavoritesFragmentToDetailFragment(product)
                findNavController().navigate(action)
            }
        )

        //Ecommerce adapterı da kullana bilirdik fakat burda artık favoriteadapterı kullanmadığımız için
        //remove gibi özellikleri kullanamayız yada ecommerceadapotere da bu özellikleri eklememiz gerekir
        //bu yüzden üstteki gibi favorite adapterıa lambda verip favorite fragmennttı yönlendirdik
//        val adapter = EcommorceAdapter(
//            context = requireContext(),
//            onProductClick = { product ->
//                // Ürün detayına yönlendirme
//                val action = FavoritesFragmentDirections.actionFavoritesFragmentToDetailFragment(product)
//                findNavController().navigate(action)
//            },
//            onFavoriteClick = { product ->
//                // Favorilere ekleme işlemini burada yapabilirsiniz
//
//            }
//        )

        // RecyclerView ayarları
        // yan yana olan görünüm tekli oluyor
        //binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(requireContext())
        //artık yan yana ikili olcak
        binding.recyclerViewFavorites.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        binding.recyclerViewFavorites.adapter = adapter

        // Favori ürünleri gözlemleme
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

        // Ürünleri keşfet butonu tıklanınca anasayfaya yönlendirme
        binding.buttonExploreProducts.setOnClickListener {
            findNavController().navigate(R.id.action_favoritesFragment_to_homeFragment)

        }
            // Favori ürünleri yükleme
        viewModel.loadFavorites()

        return binding.root
    }
}


