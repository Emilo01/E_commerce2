package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentDetailBinding


class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater, container, false)

        //home page den gönderilen nesneyi detail fragmentta yakalıcaz
        val bundle: DetailFragmentArgs by navArgs()
        //val gelenProduct = bundle.product
        //eğer product değilde geleProduct ise aşadakileride düzeltcez
        val product=bundle.product

        binding.toolbarDetailPage.title = product.title

        binding.imageViewProduct.setImageResource(
            resources.getIdentifier(product.image, "drawable", requireContext().packageName))
        //drawble kulladık çünkü burda api den değil localde drawblede olan resimleri kullancaz

        binding.textViewProductPrice.text = "${product.price} TL"

        return binding.root
    }


}