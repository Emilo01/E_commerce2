package com.farukayata.e_commerce2.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.farukayata.e_commerce2.R
import com.farukayata.e_commerce2.databinding.FragmentHomeBinding
import com.farukayata.e_commerce2.model.Product
import com.farukayata.e_commerce2.ui.adapter.EcommorceAdapter
import com.farukayata.e_commerce2.ui.adapter.PopularProductsAdapter
import com.farukayata.e_commerce2.ui.viewmodel.FavoritesViewModel
import com.farukayata.e_commerce2.ui.viewmodel.HomePageViewModel
import com.google.firebase.auth.FirebaseAuth
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

        //Kullanıcı E-Postasını Aldık ve Toolbar Başlığına Atadık
        val userEmail = FirebaseAuth.getInstance().currentUser?.email // Firebase'den e-posta aldık
        val username = userEmail?.substringBefore("@") ?: "HomePage"
        // "@" öncesini kadar olan kısımı aldık, null ise HomePage yazdırıyoruz

        (activity as AppCompatActivity).supportActionBar?.title = "Hi ${username}"
        //sonrası için xml kodlarında da burayı değiştire biliriz bence

        //Adapter Tanımlama
        val adapter = EcommorceAdapter(
            context = requireContext(),
            onProductClick = { product ->
                val action = HomeFragmentDirections.detailGecis(product)
                findNavController().navigate(action)
            },
            onFavoriteClick = { product ->
                favoritesViewModel.addFavorite(product)
            }
        )
        binding.commerceAdapter = adapter


        val mostInterestedProducts = PopularProductsAdapter(
            context = requireContext(),
            onProductClick = { product ->
                val action = HomeFragmentDirections.detailGecis(product)
                findNavController().navigate(action)
            }
        )

        binding.recyclerViewMostInterested.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        binding.mostPopularAdapter = mostInterestedProducts

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
        homeViewModel.filteredProductList.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
        }

        homeViewModel.mostInterestedProducts.observe(viewLifecycleOwner) { products ->
            mostInterestedProducts.submitList(products)
        }

        // Arama Kutusunu Dinleme (Search Functionality)
        binding.searchView.isIconified = false // Arama çubuğu her zaman açık
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                homeViewModel.filterProducts(query.orEmpty()) // Boş olursa hata almamak için
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                homeViewModel.filterProducts(newText.orEmpty()) // Boş olursa hata almamak için
                return true
            }
        })



//        binding.searchEditText.addTextChangedListener { text ->
//            homeViewModel.filterProducts(text.toString()) // ViewModel'e yönlendir
//        }
//            val adapter = EcommorceAdapter(requireContext()) { product ->
//                // Favorilere ekleme işlemi
//                favoritesViewModel.addFavorite(product)
//            }

        //ecommerce adapterımıza lamda ekleyerek bağımsız hale getirdik.
        //ve ürünen tıklandığında geçiş mantığını artık ona ayit sayfannın fragmenttındna yöeticez
//
//            val adapter = EcommorceAdapter(
//                context = requireContext(),
//                onProductClick = { product ->
//                    // Ürün detayına geçiş
//                    val action = HomeFragmentDirections.detailGecis(product)
//                    findNavController().navigate(action)
//                },
//                onFavoriteClick = { product ->
//                    // Favorilere ekleme işlemi
//                    favoritesViewModel.addFavorite(product)
//                }
//            )
//            binding.commerceAdapter = adapter
//            adapter.submitList(products) // Listeyi adaptöre bağla
//        }

        // Favoriler sayfasına geçiş için butona tıklama
//        binding.buttonFavorites.setOnClickListener {
//            findNavController().navigate(R.id.action_homeFragment_to_favoritesFragment)
//        }

        return binding.root
    }
}
