package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.data.entity.Commerce_Products
import com.farukayata.e_commerce2.databinding.FragmentHomeBinding
import com.farukayata.e_commerce2.ui.adapter.EcommorceAdapter
import com.farukayata.e_commerce2.ui.viewmodel.HomePageViewModel


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomePageViewModel // aşada oncreta fun da oluşturcaz

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home, container, false)
        //yukarıyı revize edip fragmenthomebindingden dataminning yaptık

        //binding.toolbarHomePage.title = "Commerce_Products"//E-Commerce App
        //üstündeki gibi değilde dataminnig kurunca aşağıdaki gibi yaptık
        binding.homePageToolbarTitle = "Commerce_Products"//E-Commerce App

        //bu kısımı xml kısmında ekledik buraya gerek kalmadı
        //binding.CommerceRecycleView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        //2 stün olucağıı ve ikişerli olucağını belirttik

//        val commerceProductListesi = ArrayList<Commerce_Products>()
//        val f1 = Commerce_Products(1, "batman", "batman", 100.0)
//        val f2 = Commerce_Products(1, "ironman", "ironman", 50.0)
//        val f3 = Commerce_Products(1, "kaptanamerica", "kaptanamerica", 75.0)
//        val f4 = Commerce_Products(1, "superman", "superman", 600.0)
//        val f5 = Commerce_Products(1, "thor", "thor", 110.0)
//        val f6 = Commerce_Products(1, "wolverine", "wolverine", 999.0)
//        commerceProductListesi.add(f1)
//        commerceProductListesi.add(f2)
//        commerceProductListesi.add(f3)
//        commerceProductListesi.add(f4)
//        commerceProductListesi.add(f5)
//        commerceProductListesi.add(f6)
        //liste adapter a gönderildi
        //yukarıdakı kısım datasource de yapıldı

        viewModel.commerceProductListesi.observe(viewLifecycleOwner) {
            val EcommorceAdapter = EcommorceAdapter(requireContext(), it)

            binding.commorceAdapter = EcommorceAdapter
            //adapterı da datayı kurdukta sonra aşağıdaki gibi değilde yukarıdaki gibi yaptık
            //binding.CommerceRecycleView.adapter = EcommorceAdapter
            //listemizi recyclerview a aktardık
        }



        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tempViewModel: HomePageViewModel by viewModels() //geçici
        viewModel = tempViewModel
    }

}