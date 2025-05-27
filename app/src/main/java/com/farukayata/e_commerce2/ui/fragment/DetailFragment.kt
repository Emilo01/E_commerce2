package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.MainActivity
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentDetailBinding
import com.farukayata.e_commerce2.model.CartItem
import com.farukayata.e_commerce2.ui.viewmodel.CartViewModel
import com.farukayata.e_commerce2.ui.viewmodel.DetailViewModel
import com.farukayata.e_commerce2.ui.viewmodel.FavoritesViewModel
import com.farukayata.e_commerce2.ui.adapter.RecommendedProductsAdapter
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val args: DetailFragmentArgs by navArgs()
    private val cartViewModel: CartViewModel by viewModels()
    private val detailViewModel: DetailViewModel by viewModels()
    private val favoritesViewModel: FavoritesViewModel by viewModels()

    private var quantity = 1
    private var unitPrice: Double = 0.0
    private var isExpanded = false // açıklama genişletme

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product
        binding.product = product
        unitPrice = product.price ?: 0.0

        Glide.with(this).load(product.image).into(binding.imageViewProduct)
        (activity as? MainActivity)?.supportActionBar?.title = product.title

        //favorileri Firestoredan yükledik
        favoritesViewModel.loadFavorites()

        lifecycleScope.launchWhenStarted {
            favoritesViewModel.favorites.collect { favorites ->
                //favorileri güncelleyerek önerilen ürünleri yükledik
                detailViewModel.loadRecommendedProducts(product.category ?: "", product.id ?: 0, favorites)
            }
        }

        binding.recyclerViewRecommended.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }

        val adapter = RecommendedProductsAdapter(
            requireContext(),
            onProductClick = { selectedProduct ->
                val action = DetailFragmentDirections.actionDetailFragmentToDetailFragment(selectedProduct)
                findNavController().navigate(action)
            },
            onFavoriteClick = { product ->
                favoritesViewModel.addFavorite(product)
            },
            onRemoveFavoriteClick = { product ->
                favoritesViewModel.removeFavorite(product.id.toString())
            }
        )

        binding.recyclerViewRecommended.adapter = adapter

        //önerilen ürünleri güncelle
        detailViewModel.recommendedProducts.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
        }

        //açıklama - +
        binding.buttonReadMore.setOnClickListener {
            if (isExpanded) {
                binding.textViewProductDescription.maxLines = 3
                binding.textViewProductDescription.ellipsize = TextUtils.TruncateAt.END
                binding.buttonReadMore.text = "Read More"
            } else {
                binding.textViewProductDescription.maxLines = Integer.MAX_VALUE
                binding.textViewProductDescription.ellipsize = null
                binding.buttonReadMore.text = "Read Less"
            }
            isExpanded = !isExpanded
        }

        //adet ve fiyat
        updateTotalPrice()

        binding.buttonIncreaseQuantity.setOnClickListener {
            quantity++
            updateTotalPrice()
        }

        binding.buttonDecreaseQuantity.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateTotalPrice()
            }
        }

        binding.textViewProductQuantity.setOnClickListener {
            showQuantityPopup()
        }

        //sepete ekleme butonu işlevi
        binding.buttonAddToCart.setOnClickListener {
            val cartItem = CartItem(
                id = product.id.toString(),
                title = product.title,
                price = product.price,
                description = product.description,
                image = product.image,
                category = product.category,
                count = quantity
            )
            cartViewModel.addToCart(cartItem)

            //Toast.makeText(requireContext(), "${quantity} adet '${product.title}' sepete eklendi!", Toast.LENGTH_SHORT).show()
            Snackbar.make(binding.root, "${quantity} adet '${product.title}' sepete eklendi!", Snackbar.LENGTH_SHORT).show()

            //firestoredan güncel sepet verisini tekrar çektik
            cartViewModel.loadCartItems()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("DetailFragment", "onResume çağrıldı. Favori ürünler güncelleniyor.")

        lifecycleScope.launchWhenStarted {
            favoritesViewModel.favorites.collect { favorites ->
                detailViewModel.updateFavorites(favorites)
                binding.recyclerViewRecommended.adapter?.notifyItemRangeChanged(0, binding.recyclerViewRecommended.adapter!!.itemCount)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("DetailFragment", "onDestroyView çağrıldı. Binding sıfırlandı.")
        _binding = null
    }

    //adet seçimi için popup
    private fun showQuantityPopup() {
        val popupMenu = PopupMenu(requireContext(), binding.textViewProductQuantity)
        for (i in 1..100) {
            popupMenu.menu.add(i.toString())
        }
        popupMenu.setOnMenuItemClickListener { item ->
            quantity = item.title.toString().toInt()
            updateTotalPrice()
            true
        }
        popupMenu.show()
    }

    // toplam fiyatı güncelleme
    private fun updateTotalPrice() {
        val totalPrice = (unitPrice * quantity).coerceAtLeast(0.0)
        binding.textViewProductQuantity.text = quantity.toString()
        binding.textViewTotalPrice.text = String.format("Total Price : %.2f TL", totalPrice)
    }
}