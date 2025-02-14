package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private val args: DetailFragmentArgs by navArgs()
    private val cartViewModel: CartViewModel by viewModels()
    private val detailViewModel: DetailViewModel by viewModels()

    private var quantity = 1
    private var unitPrice : Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)

        val product = args.product
        binding.product = product
        unitPrice = product.price ?: 0.0 // değer null sa sıfır verdirdik

        Glide.with(this).load(product.image).into(binding.imageViewProduct)

        // Toolbar başlığını ayarla
        (activity as? MainActivity)?.supportActionBar?.title = product.title

        //adet ve fiyat güncelleme için
        updateTotalPrice()

        //adet arttırma
        binding.buttonIncreaseQuantity.setOnClickListener{
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

        //Kullanıcı `textViewProductQuantity` üzerine tıkladığında Popup Açılacak
        binding.textViewProductQuantity.setOnClickListener {
            showQuantityPopup()
        }

        // Sepete Ekle butonuna tıklama işlevi
        binding.buttonAddToCart.setOnClickListener {
            product?.let {
                val cartItem = CartItem(
                    id = it.id.toString(),
                    title = it.title,
                    price = it.price,
                    description = it.description,
                    image = it.image,
                    category = it.category,
                    count = quantity //sepete eklemeden nseçtiğimiz adet sayısı
                )
                cartViewModel.addToCart(cartItem) // Ürünü sepete ekle

                Toast.makeText(requireContext(), "${quantity} of '${it.title}' product added to cart!", Toast.LENGTH_SHORT).show()
            }
        }

        //önerilen ürünleri yüklüyoruz
        detailViewModel.loadRecommendedProducts(product.category ?:"")

        //recycler view için yatay kaydırabilirlik ekledik ve adapter yazmadık
//        binding.recyclerViewRecommended.layoutManager =
//            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        //sethasfixedsize sayesinde boyutu değişmeyen öğeler için gereksiz measure çağrıları engelleniyor ve performans artıyor
        binding.recyclerViewRecommended.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true) // Performans iyileştirme
        }


        //önerilen ürünler recycler viewi kullanmak için ecommerce adapterı kullandık
        val adapter = EcommorceAdapter(
            requireContext(),
            onProductClick = { selectedProduct ->
                val action = DetailFragmentDirections.actionDetailFragmentToDetailFragment(selectedProduct)
                findNavController().navigate(action)
            },
            onFavoriteClick = { product ->
                // Favorilere ekleme işlemi buraya eklenebilir
            }
        )

        binding.recyclerViewRecommended.adapter = adapter

        //view modeldeki ürünleri recycler view a atadık
        detailViewModel.recommendedProducts.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
        }


        return binding.root
    }

    //Kullanıcı Adet Seçimi Yapınca Açılan Spinner Tarzı Menü (PopupMenu)
    private fun showQuantityPopup() {
        val popupMenu = PopupMenu(requireContext(), binding.textViewProductQuantity)

        // 1-100 arasındaki sayıları menüye ekleyelim
        for (i in 1..100) {
            popupMenu.menu.add(i.toString())
        }

        // Kullanıcı bir adet seçtiğinde
        popupMenu.setOnMenuItemClickListener { item ->
            quantity = item.title.toString().toInt()
            updateTotalPrice()
            true
        }

        popupMenu.show()
    }

    // toplam fiyat güncelleme
    private fun updateTotalPrice() {
        val totalPrice = (unitPrice * quantity).coerceAtLeast(0.0) //fiyatı eksili gelemez artık min 0
        binding.textViewProductQuantity.text = quantity.toString()
        binding.textViewTotalPrice.text = String.format("Total Price : %.2f TL", totalPrice)
    }
}
















/*
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_detail, container, false)
        //xml kısmınnda commoreceNesnesi kullandıkta sonra bu sayfayıda yukarıda başlayarak revize ediyoruz

        //home page den gönderilen nesneyi detail fragmentta yakalıcaz
        val bundle: DetailFragmentArgs by navArgs()
        //val gelenProduct = bundle.product
        //eğer product değilde geleProduct ise aşadakileride düzeltcez
        val product=bundle.product

        binding.commoreceNesnesi = product
        //burda product u gönnderip resim haricinde çalışma sağladım


        //binding.toolbarDetailPage.title = product.title
        //bu kısımı xml kısmında commorce nesnesi olarak gönderiyoruz artık

        binding.imageViewProduct.setImageResource(
            resources.getIdentifier(product.image, "drawable", requireContext().packageName))
        //drawble kulladık çünkü burda api den değil localde drawblede olan resimleri kullancaz

        //binding.textViewProductPrice.text = "${product.price} TL"
        //bu kısımı xml kısmında commorce nesnesi olarak gönderiyoruz artık

        return binding.root
    }


}

 */