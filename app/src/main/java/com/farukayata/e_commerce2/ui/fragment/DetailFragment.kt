package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.farukayata.e_commerce2.MainActivity
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)

        val product = args.product
        binding.product = product

        Glide.with(this).load(product.image).into(binding.imageViewProduct)

        // Toolbar başlığını ayarla
        (activity as? MainActivity)?.supportActionBar?.title = product.title

        return binding.root
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