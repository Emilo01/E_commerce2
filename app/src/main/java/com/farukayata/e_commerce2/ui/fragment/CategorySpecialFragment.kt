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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.farukayata.e_commerce2.ui.viewmodel.FavoritesViewModel


@AndroidEntryPoint
class CategorySpecialFragment : Fragment() {

    private lateinit var binding: FragmentCategorySpecialBinding
    private val viewModel: CategorySpecialViewModel by viewModels()
    private val args: CategorySpecialFragmentArgs by navArgs() // Argümanları al
    private val favoritesViewModel: FavoritesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategorySpecialBinding.inflate(inflater, container, false)

//        val adapter = EcommorceAdapter(requireContext()) { product ->
//            // Ürün detayına yönlendirme
//            val action = CategorySpecialFragmentDirections.actionCategorySpecialFragmentToDetailFragment(product)
//            findNavController().navigate(action)
//        }

        //ecommerce adapterımıza lamda ekleyerek bağımsız hale getirdik.
        //ve ürünen tıklandığında geçiş mantığını artık ona ayit sayfannın fragmenttındna yöeticez
        val adapter = EcommorceAdapter(
            context = requireContext(),
            onProductClick = { product ->
                // Ürün detayına yönlendirme
                val action = CategorySpecialFragmentDirections.actionCategorySpecialFragmentToDetailFragment(product)
                findNavController().navigate(action)
            },
            onFavoriteClick = { product ->
                // Favorilere ekleme kısmını favoriteviewmodel kullanarak yaptık
                // isteğe bağlı categoryspecialviewmodel de addfavorite fonksiyonunu ekleye biliriz
                favoritesViewModel.addFavorite(product)
            }
        )

        binding.recyclerViewCategorySpecial.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        //üstteki gibi özelleştirerek kart yapısına mazgal görünüm verdik
        //binding.recyclerViewCategorySpecial.layoutManager = LinearLayoutManager(requireContext())
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
