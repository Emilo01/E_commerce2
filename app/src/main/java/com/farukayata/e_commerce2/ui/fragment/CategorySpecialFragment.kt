package com.farukayata.e_commerce2.ui.fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.farukayata.e_commerce2.databinding.FragmentCategorySpecialBinding
import com.farukayata.e_commerce2.ui.adapter.EcommorceAdapter
import com.farukayata.e_commerce2.ui.viewmodel.CategorySpecialViewModel
import com.farukayata.e_commerce2.ui.viewmodel.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class CategorySpecialFragment : Fragment() {

    private var _binding: FragmentCategorySpecialBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategorySpecialViewModel by viewModels()
    private val favoritesViewModel: FavoritesViewModel by viewModels()
    private val args: CategorySpecialFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategorySpecialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Toolbar geri butonunu aktif et
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val categoryName = args.categoryName
        Log.d("CategoryDebug", "Gelen kategori adı: $categoryName")

        if (categoryName.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Kategori bilgisi alınamadı!", Toast.LENGTH_SHORT).show()
            return
        }

        val adapter = EcommorceAdapter(
            context = requireContext(),
            onProductClick = { product ->
                val action = CategorySpecialFragmentDirections.actionCategorySpecialFragmentToDetailFragment(product)
                findNavController().navigate(action)
            },
            onFavoriteClick = { product ->
                favoritesViewModel.addFavorite(product)
            },
            onRemoveFavoriteClick = { product ->
                favoritesViewModel.removeFavorite(product.id.toString())
            }
        )

        binding.recyclerViewCategorySpecial.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerViewCategorySpecial.adapter = adapter

        favoritesViewModel.loadFavorites()

        //favori değişikliklerini dinledik ve kategori ürünlerini güncelledik
        lifecycleScope.launchWhenStarted {
            favoritesViewModel.favorites.collect { favorites ->
                viewModel.loadProductsByCategory(categoryName, favorites)
            }
        }

        //ürünleri güncelledik
        viewModel.products.observe(viewLifecycleOwner) { products ->
            val filteredProducts = products.filter { it.category.equals(categoryName, ignoreCase = true) }
            if (filteredProducts.isEmpty()) {
                Toast.makeText(requireContext(), "Bu kategoriye ait ürün bulunamadı!", Toast.LENGTH_SHORT).show()
            }
            adapter.submitList(filteredProducts)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Category Special OnResume")

        //favori değişikliklerini dinleyerek ürünleri güncelledik
        lifecycleScope.launchWhenStarted {
            favoritesViewModel.favorites.collect { favorites ->
                viewModel.updateFavorites(favorites)
                binding.recyclerViewCategorySpecial.adapter?.notifyItemRangeChanged(0, binding.recyclerViewCategorySpecial.adapter!!.itemCount)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("CategorySpecialFragment", "onDestroyView çağrıldı. Binding sıfırlandı.")
        _binding = null
    }
}




/*
package com.farukayata.e_commerce2.ui.fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentCategorySpecialBinding
import com.farukayata.e_commerce2.ui.adapter.EcommorceAdapter
import com.farukayata.e_commerce2.ui.viewmodel.CategorySpecialViewModel
import com.farukayata.e_commerce2.ui.viewmodel.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategorySpecialFragment : Fragment() {

    private lateinit var binding: FragmentCategorySpecialBinding
    private val viewModel: CategorySpecialViewModel by viewModels()
    private val args: CategorySpecialFragmentArgs by navArgs()
    private val favoritesViewModel: FavoritesViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume: Category Special OnResume" )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategorySpecialBinding.inflate(inflater, container, false)

        val categoryName = args.categoryName
        Log.d("CategoryDebug", "Gelen kategori adı: $categoryName")

        if (categoryName.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Kategori bilgisi alınamadı!", Toast.LENGTH_SHORT).show()
            //findNavController().navigateUp()
            return binding.root
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar geri butonunu aktif et
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Fiziksel geri tuşuna basıldığında HomeFragment’a dön
       /* requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.homeFragment)
            //findNavController().navigateUp()
        }*/

        // Adapter tanımlama
        val adapter = EcommorceAdapter(
            context = requireContext(),
            onProductClick = { product ->
                val action = CategorySpecialFragmentDirections.actionCategorySpecialFragmentToDetailFragment(product)
                findNavController().navigate(action)
            },
            onFavoriteClick = { product ->
                favoritesViewModel.addFavorite(product)
            },
            onRemoveFavoriteClick = { product ->
                favoritesViewModel.removeFavorite(product.id.toString()) // Ürünü favorilerden çıkar
            }
        )

        binding.recyclerViewCategorySpecial.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerViewCategorySpecial.adapter = adapter

        lifecycleScope.launchWhenStarted {
            favoritesViewModel.favorites.collect { favorites ->
                viewModel.loadProductsByCategory(args.categoryName, favorites)
            }
        }

        viewModel.products.observe(viewLifecycleOwner) { products ->
            val filteredProducts = products.filter { it.category.equals(args.categoryName, ignoreCase = true) }
            if (filteredProducts.isEmpty()) {
                Toast.makeText(requireContext(), "Bu kategoriye ait ürün bulunamadı!", Toast.LENGTH_SHORT).show()
            }
            adapter.submitList(filteredProducts)
        }

        viewModel.loadProductsByCategory(args.categoryName)
    }
}

 */