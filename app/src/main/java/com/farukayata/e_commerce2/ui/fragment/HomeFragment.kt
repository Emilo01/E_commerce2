package com.farukayata.e_commerce2.ui.fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Kullanıcı E-Postasını Aldık ve Toolbar Başlığına Atadık
        val userEmail = FirebaseAuth.getInstance().currentUser?.email // Firebase'den e-posta aldık
        val username = userEmail?.substringBefore("@") ?: "HomePage"
        // "@" öncesini kadar olan kısımı aldık, null ise HomePage yazdırıyoruz

        (activity as AppCompatActivity).supportActionBar?.title = "Hi ${username}"
        //sonrası için xml kodlarında da burayı değiştire biliriz bence

        //Favorileri Yükle - gereksiz olabilir amma ürünler de kalp yanıp sömesini etkiliyor da olabilir
        favoritesViewModel.loadFavorites()

        //Kategori Chip Seçimini Kur
        setupCategorySelection()


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
            },
            onFavoriteClick = { product ->
                favoritesViewModel.addFavorite(product)
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
        //firestoredan Favoriler Güncellendiğinde tüm Ürün Listesini Güncelledik
        lifecycleScope.launchWhenStarted {
            favoritesViewModel.favorites.collect { favorites ->
                homeViewModel.updateFavorites(favorites)
                homeViewModel.updateMostInterestedFavorites(favorites)
            }
        }
        /*
        observe(viewLifecycleOwner) {...} yerine collect kullandık
        çünkü favoritesViewModel.favorites muhtemelen bir StateFlow.
         */


        homeViewModel.filteredProductList.observe(viewLifecycleOwner) { products ->
            Log.d("DEBUG", "Yüklenen ürün sayısı: ${products?.size ?: 0}")

            if (products.isNullOrEmpty()) {
                Log.e("ERROR", "Ürün listesi boş, Home sayfasında çökme yaşanabilir!")
                binding.emptyStateView.visibility = View.VISIBLE  //Boş durumu göster
                adapter.submitList(emptyList()) //RecyclerView'i boş listeyle güncelle
            } else {
                binding.emptyStateView.visibility = View.GONE  //Eğer ürünler varsa gizle
                adapter.submitList(products)
            }
        }


        homeViewModel.mostInterestedProducts.observe(viewLifecycleOwner) { products ->
            mostInterestedProducts.submitList(products)
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.shimmerLayout.startShimmer()
                binding.shimmerLayout.visibility = View.VISIBLE
                binding.contentLayout.visibility = View.GONE
                Log.d("DEBUG", "isLoading: $isLoading")

            } else {
                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.postDelayed({
                    binding.shimmerLayout.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE
                    binding.mostInterestedContainer.visibility = View.VISIBLE
                    binding.cardViewAllProducts.visibility = View.VISIBLE
                }, 500) //500ms gecikme ekledik
            }
        }

        // Arama Kutusunu Dinleme
        binding.searchView.isIconified = true // Arama çubuğu her zaman kapalı gelcek
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


        //setupCategorySelection()
    }

    //kategori Seçimi Chip için
//    private fun setupCategorySelection() {
//
//        binding.chipGroupCategories.setOnCheckedChangeListener { esra, checkedId ->
//                binding.chipGroupCategories.clearCheck()
//            when (checkedId) {
//                binding.chipElectronics.id -> navigateToCategory("electronics")
//                binding.chipClothing.id -> navigateToCategory("jewelery")
//                binding.chipHome.id -> navigateToCategory("men's clothing")
//                binding.chipToys.id -> navigateToCategory("women's clothing")
//            }
//        }
//    }

    private fun setupCategorySelection() {
        val categoryItems = listOf("electronics", "jewelery", "men's clothing", "women's clothing")

        // CustomChipGroup içindeki setChipItems fonksiyonunu çağırıyoruz
        binding.customChipGroup.setChipItems(categoryItems)

        // Seçilen kategoriye yönlendirme ekliyoruz
        binding.customChipGroup.onChipSelected = { selectedCategory ->
            navigateToCategory(selectedCategory)
        //onChipselected ile ayrı ayrı her chip için setonclicklistener eklemekten nkurtulduk
        //CustomChipGroup bileşeni sayesinde her sayfada tekrar tekrar aynı kodları yazmadan kullanabiliriz =>  Kod Tekrarını Önleme (Reusable Component)
        }
    }

    //kategoriye Gitme Fonksiyonu
    private fun navigateToCategory(category: String) {
        Log.d("CategoryDebug", "Gönderilen kategori: $category")
        val action = HomeFragmentDirections.actionHomeFragmentToCategorySpecialFragment(category)
        findNavController().navigate(action)
    }



}
