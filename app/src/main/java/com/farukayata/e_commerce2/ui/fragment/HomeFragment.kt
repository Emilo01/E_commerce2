package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentHomeBinding
import com.farukayata.e_commerce2.model.Product
import com.farukayata.e_commerce2.ui.adapter.EcommorceAdapter
import com.farukayata.e_commerce2.ui.viewmodel.FavoritesViewModel
import com.farukayata.e_commerce2.ui.viewmodel.HomePageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomePageViewModel by viewModels()
    private val favoritesViewModel: FavoritesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Activity'nin toolbar başlığını değiştir
        (activity as AppCompatActivity).supportActionBar?.title = "HomePage"
        //sonrası için xml kodlarında da burayı değiştire biliriz bence

        // Ürün listesini RecyclerView ile bağlama
//        homeViewModel.productList.observe(viewLifecycleOwner) { products ->
//            val adapter = EcommorceAdapter(requireContext(), products) { product ->
//                // Favorilere ekleme işlemi
//                val favorite = Product(
//                    id = product.id,
//                    title = product.title,
//                    price = product.price,
//                    image = product.image
//                )
//                favoritesViewModel.addFavorite(favorite)
//            }
//            binding.commerceAdapter = adapter
//        }
        homeViewModel.productList.observe(viewLifecycleOwner) { products ->
            val adapter = EcommorceAdapter(requireContext()) { product ->
                // Favorilere ekleme işlemi
                favoritesViewModel.addFavorite(product)
            }
            binding.commerceAdapter = adapter
            adapter.submitList(products) // Listeyi adaptöre bağla
        }

        // Favoriler sayfasına geçiş için butona tıklama
        binding.buttonFavorites.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_favoritesFragment)
        }

        return binding.root
    }
}





/*
package com.farukayata.e_commerce2.ui.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.farukayata.e_commerce2.databinding.FragmentHomeBinding
import com.farukayata.e_commerce2.ui.adapter.EcommorceAdapter
import com.farukayata.e_commerce2.ui.viewmodel.HomePageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    //ViewModel, Repository ye ejekte ettik

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomePageViewModel by viewModels()
    //API'den veri çekme, işleme ve fragment'a veri sağlama gibi görevler için

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        //Ürün listesini RecyclerView ile bağlayarak Home sayfasında görüntülettik
        viewModel.productList.observe(viewLifecycleOwner) { products ->
            val adapter = EcommorceAdapter(requireContext(), products)
            binding.commerceAdapter = adapter
        }


        return binding.root
    }
}



 */












/*
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

 */