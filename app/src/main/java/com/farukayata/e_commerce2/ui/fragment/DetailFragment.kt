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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private val args: DetailFragmentArgs by navArgs()
    private val cartViewModel: CartViewModel by viewModels()
    private val detailViewModel: DetailViewModel by viewModels()
    private val favoritesViewModel: FavoritesViewModel by viewModels()

    private var quantity = 1
    private var unitPrice: Double = 0.0
    private var isExpanded = false //readmi kısımı için

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)

        val product = args.product
        binding.product = product
        unitPrice = product.price ?: 0.0 // Değer null ise sıfır atandı

        Glide.with(this).load(product.image).into(binding.imageViewProduct)

        (activity as? MainActivity)?.supportActionBar?.title = product.title

        // Önerilen Ürünleri Yükle
        detailViewModel.loadRecommendedProducts(product.category ?: "", product.id ?: 0)

        favoritesViewModel.loadFavorites()
        //geri çıkarke ikinci detail sayfasıda ilk detail sayfasıan isfavorite false oluyor ??????????

        //Favori değişiklikleri
//        lifecycleScope.launchWhenStarted {
//            favoritesViewModel.favorites.collect { favorites ->
//                detailViewModel.updateFavorites1(favorites) // Favorileri güncelle
//            }
//        }

//        lifecycleScope.launchWhenStarted {
//            favoritesViewModel.favorites.collect { favorites ->
//                detailViewModel.updateFavorites1(favorites) // Favorileri güncelle
//                detailViewModel.recommendedProducts.value?.let { updatedList ->
//                    binding.recyclerViewRecommended.adapter?.notifyDataSetChanged() // UI'yi zorla güncelle
//                }
//            }
//        }





        // RecyclerView Ayarları
        binding.recyclerViewRecommended.apply {
            //ikinci detail fragmettan çıkarke ilk detail sayfasıan burda da false geliyor isfavorite
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }

        // Adapter Tanımla
        val adapter = RecommendedProductsAdapter(
            requireContext(),
            onProductClick = { selectedProduct ->
                val action = DetailFragmentDirections.actionDetailFragmentToDetailFragment(selectedProduct)
                findNavController().navigate(action)
            },
            onFavoriteClick = { product ->
                favoritesViewModel.addFavorite(product) // Favorilere ekle
            },
            onRemoveFavoriteClick = { product ->
                favoritesViewModel.removeFavorite(product.id.toString()) // Favorilerden çıkar
            }
        )

        binding.recyclerViewRecommended.adapter = adapter
        //xml deki şu kısım yerine üsttekini yaptık "android:adapter="@{recommendedAdapter}""

        // Önerilen Ürünleri Güncelle
        detailViewModel.recommendedProducts.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
        }

        //Read More / Read Less
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

        // Adet ve fiyat güncelleme
        updateTotalPrice()

        // Adet arttırma
        binding.buttonIncreaseQuantity.setOnClickListener {
            quantity++
            updateTotalPrice()
        }

        // Adet azaltma
        binding.buttonDecreaseQuantity.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateTotalPrice()
            }
        }

        // Kullanıcı adet seçimi popup
        binding.textViewProductQuantity.setOnClickListener {
            showQuantityPopup()
        }

        // Sepete ekleme butonu işlevi
        binding.buttonAddToCart.setOnClickListener {
            product?.let {
                val cartItem = CartItem(
                    id = it.id.toString(),
                    title = it.title,
                    price = it.price,
                    description = it.description,
                    image = it.image,
                    category = it.category,
                    count = quantity // Sepete eklenen adet sayısı
                )
                cartViewModel.addToCart(cartItem) // Ürünü sepete ekle

                Toast.makeText(requireContext(), "${quantity} of '${it.title}' product added to cart!", Toast.LENGTH_SHORT).show()
            }
        }

        Log.e("TAGGGGGGGGG", "onCreateView: DetailFragment")

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.e("TAGGGGGGGGG", "onResume: DetailFragment")

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            favoritesViewModel.favorites.collect { favorites ->
                detailViewModel.updateFavorites1(favorites)
                //ilk girerken detail sayfasına isfavorite burda ttrue oluyor ilk !!!!!!!!!!

                // Sadece belirli öğeleri güncelle
                binding.recyclerViewRecommended.adapter?.let { adapter ->
                    //burda true geliyor geri detail sayfasıa çıkarke !!!!!!!!!!!!!!!!??????????
                    adapter.notifyItemRangeChanged(0, adapter.itemCount)
                }
            }
        }
    }

    // Kullanıcı Adet Seçimi Yapınca Açılan Popup Menü
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

    // Toplam fiyatı güncelleme
    private fun updateTotalPrice() {
        val totalPrice = (unitPrice * quantity).coerceAtLeast(0.0)
        binding.textViewProductQuantity.text = quantity.toString()
        binding.textViewTotalPrice.text = String.format("Total Price : %.2f TL", totalPrice)
    }
}



/*
package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.MainActivity
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentDetailBinding
import com.farukayata.e_commerce2.model.CartItem
import com.farukayata.e_commerce2.ui.adapter.EcommorceAdapter
import com.farukayata.e_commerce2.ui.viewmodel.CartViewModel
import com.farukayata.e_commerce2.ui.viewmodel.DetailViewModel
import androidx.navigation.fragment.findNavController
import com.farukayata.e_commerce2.ui.adapter.RecommendedProductsAdapter
import com.farukayata.e_commerce2.ui.viewmodel.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private val args: DetailFragmentArgs by navArgs()
    private val cartViewModel: CartViewModel by viewModels()
    private val detailViewModel: DetailViewModel by viewModels()
    private val favoritesViewModel: FavoritesViewModel by viewModels()

    private var quantity = 1
    private var unitPrice: Double = 0.0
    private var isExpanded = false // Açıklamanın açık/kapalı durumunu takip edicez


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)

        val product = args.product
        binding.product = product
        unitPrice = product.price ?: 0.0 //değer null ise sıfır atandı

        Glide.with(this).load(product.image).into(binding.imageViewProduct)

        //toolbar başlığını ayarla
        (activity as? MainActivity)?.supportActionBar?.title = product.title

        //Read More / Read Less İşlevi
        binding.buttonReadMore.setOnClickListener {
            if (isExpanded) {
                // Açıklama kapatılıyor
                binding.textViewProductDescription.maxLines = 3
                binding.textViewProductDescription.ellipsize = TextUtils.TruncateAt.END
                binding.buttonReadMore.text = "Read More"
            } else {
                // Açıklama tamamen açılıyor
                binding.textViewProductDescription.maxLines = Integer.MAX_VALUE
                binding.textViewProductDescription.ellipsize = null
                binding.buttonReadMore.text = "Read Less"
            }
            isExpanded = !isExpanded // Durumu tersine çevir
        }

        //adet ve fiyat güncelleme
        updateTotalPrice()

        //adet arttırma
        binding.buttonIncreaseQuantity.setOnClickListener {
            quantity++
            updateTotalPrice()
        }

        //adet azaltma
        binding.buttonDecreaseQuantity.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateTotalPrice()
            }
        }

        //kullanıcı `textViewProductQuantity` üzerine tıkladığında Popup Açılacak
        binding.textViewProductQuantity.setOnClickListener {
            showQuantityPopup()
        }

        //sepete Ekle butonuna tıklama işlevi
        binding.buttonAddToCart.setOnClickListener {
            product?.let {
                val cartItem = CartItem(
                    id = it.id.toString(),
                    title = it.title,
                    price = it.price,
                    description = it.description,
                    image = it.image,
                    category = it.category,
                    count = quantity //sepete eklenen adet sayısı
                )
                cartViewModel.addToCart(cartItem) //ürünü sepete ekle

                Toast.makeText(requireContext(), "${quantity} of '${it.title}' product added to cart!", Toast.LENGTH_SHORT).show()
            }
        }

        //önerilen ürünleri yüklerken mevcut ürünün ID sini de gönder
        detailViewModel.loadRecommendedProducts(product.category ?: "", product.id ?:0)

        //recyclerView için yatay kaydırabilirlik ekledik ve adapter oluşturduk
        binding.recyclerViewRecommended.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true) //performans iyileştirme
        }

        //önerilen ürünler için adapter oluşturduk
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

        //viewModeldeki önerilen ürünleri RecyclerViewe bağladık
        detailViewModel.recommendedProducts.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products) //detay sayfasındaki ürün artık önerilenler listesinde olmayacak
        }

        return binding.root
    }

    // Kullanıcı Adet Seçimi Yapınca Açılan Spinner Tarzı Menü =>> (PopupMenu)
    private fun showQuantityPopup() {
        val popupMenu = PopupMenu(requireContext(), binding.textViewProductQuantity)

        //1-100 arasındaki sayıları menüye ekleyelim
        for (i in 1..100) {
            popupMenu.menu.add(i.toString())
        }

        //kullanıcı bir adet seçtiğinde
        popupMenu.setOnMenuItemClickListener { item ->
            quantity = item.title.toString().toInt()
            updateTotalPrice()
            true
        }

        popupMenu.show()
    }

    //toplam fiyat güncelleme
    private fun updateTotalPrice() {
        val totalPrice = (unitPrice * quantity).coerceAtLeast(0.0) //fiyat negatif olamaz
        binding.textViewProductQuantity.text = quantity.toString()
        binding.textViewTotalPrice.text = String.format("Total Price : %.2f TL", totalPrice)
    }
}

 */