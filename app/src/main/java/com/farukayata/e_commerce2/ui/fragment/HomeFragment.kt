package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.data.entity.Commerce_Products
import com.farukayata.e_commerce2.databinding.FragmentHomeBinding
import com.farukayata.e_commerce2.ui.adapter.EcommorceAdapter


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.toolbarHomePage.title = "Commerce_Products"//E-Commerce App

        binding.CommerceRecycleView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        //2 stün olucağıı ve ikişerli olucağını belirttik

        val commerceProductListesi = ArrayList<Commerce_Products>()
        val f1 = Commerce_Products(1, "batman", "batman", 100.0)
        val f2 = Commerce_Products(1, "ironman", "ironman", 100.0)
        val f3 = Commerce_Products(1, "kaptanamerica", "kaptanamerica", 100.0)
        val f4 = Commerce_Products(1, "superman", "superman", 100.0)
        val f5 = Commerce_Products(1, "thor", "thor", 100.0)
        val f6 = Commerce_Products(1, "wolverine", "wolverine", 100.0)
        commerceProductListesi.add(f1)
        commerceProductListesi.add(f2)
        commerceProductListesi.add(f3)
        commerceProductListesi.add(f4)
        commerceProductListesi.add(f5)
        commerceProductListesi.add(f6)

        //liste adapter a gönderildi

        val EcommorceAdapter = EcommorceAdapter(requireContext(), commerceProductListesi)
        binding.CommerceRecycleView.adapter = EcommorceAdapter
        //listemizi recyclerview a aktardık

        return binding.root
    }
}