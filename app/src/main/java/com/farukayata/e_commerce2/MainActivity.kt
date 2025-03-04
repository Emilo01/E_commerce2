package com.farukayata.e_commerce2

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import android.content.Context
import android.view.animation.AnimationUtils
import com.farukayata.e_commerce2.databinding.ActivityMainBinding
import com.farukayata.e_commerce2.ui.component.CustomToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // NavHostFragment ve NavController'i bağla
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        // Kullanıcı giriş yaptı mı kontrol et
        if (isUserLoggedIn()) {
            if (!onBoardingFinished()) {
                navController.navigate(R.id.viewPagerFragment)
            } else {
                navController.navigate(R.id.splashScreenFragment)
            }
        }

        // Custom Toolbar ayarla
        setupCustomToolbar()

        // BottomNavigationView'i NavController ile bağla
        setupBottomNavigationView()

        // AppBarConfiguration ayarlar
        //şu anlık gereksiz-toolbarı revize ederken es geçtik
        //kullanılmıyor çünkü şu satru yoruma aldık
        //"// NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)"
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.categoryFragment,
                R.id.favoritesFragment,
                R.id.profileFragment,
                R.id.cartFragment,
            )
        )
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        // BottomNavigation görünürlüğünü kontrol et
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.splashScreenFragment, R.id.viewPagerFragment -> {
                    hideBottomNavigationView() // Login ve Signup ekranlarında Navbar'ı gizle
                    hideToolbar() //Bu ekranlarda toolbarı da gizle
                }
                R.id.signUpFragment -> {
                    hideBottomNavigationView() // Navbar'ı yine gizle
                    showToolbar() // Ancak signUpFragment'te toolbar göster
                    binding.customToolbar.hideCartIcon() //sepet ikonunu gizle
                    binding.customToolbar.hideCartCard()
                }
                R.id.homeFragment, R.id.categoryFragment, R.id.favoritesFragment, R.id.cartFragment, R.id.profileFragment -> {
                    showBottomNavigationView() // Ana sayfa ve ana bölümlerde Navbar görünür
                    showToolbar()
                    binding.customToolbar.showCartIcon() //sepet ikonunu göster
                    binding.customToolbar.showCartCard()
                }
                else -> {
                    hideBottomNavigationView() // Diğer fragmentlerde Navbar gizlenecek
                    showToolbar()
                }
            }

            // Toolbar başlığını değiştirdik
            updateToolbarTitle(destination.id)

            // NavBardaki ilgili sekmenin seçili olduğundan emin olduk
            binding.bottomNavigationView.menu.findItem(destination.id)?.isChecked = true
        }
    }

    //custom Toolbarı Ayarla
    private fun setupCustomToolbar() {
        binding.customToolbar.onBackClick = {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.customToolbar.onCartClick = {
            navController.navigate(R.id.cartFragment)
        }
    }

    //toolbar Başlığını Güncelledik
    private fun updateToolbarTitle(destinationId: Int) {
        val title = when (destinationId) {
            R.id.homeFragment -> "Home"
            R.id.categoryFragment -> "Categories"
            R.id.favoritesFragment -> "Favorites"
            R.id.cartFragment -> "My Cart"
            R.id.profileFragment -> "Profile"
            else -> "E-Commerce"
        }
        binding.customToolbar.setTitle(title)
    }

    //bottom Navigation Viewi Ayarları
    private fun setupBottomNavigationView() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        bottomNavigationView.setOnItemSelectedListener { item ->
            val currentDestination = navController.currentDestination?.id

            val selectedItemView = bottomNavigationView.findViewById<View>(item.itemId)
            selectedItemView?.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.item_animation)
            )

            bottomNavigationView.menu.findItem(item.itemId).icon?.setTint(
                resources.getColor(R.color.teal_secondary, theme)
            )

            when (item.itemId) {
                R.id.homeFragment -> {
                    if (currentDestination != R.id.homeFragment) {
                        navController.navigate(R.id.homeFragment)
                    }
                    true
                }
                R.id.categoryFragment -> {
                    if (currentDestination != R.id.categoryFragment) {
                        navController.navigate(R.id.categoryFragment)
                    }
                    true
                }
                R.id.favoritesFragment -> {
                    if (currentDestination != R.id.favoritesFragment) {
                        navController.navigate(R.id.favoritesFragment)
                    }
                    true
                }
                R.id.cartFragment -> {
                    if (currentDestination != R.id.cartFragment) {
                        navController.navigate(R.id.cartFragment)
                    }
                    true
                }
                R.id.profileFragment -> {
                    if (currentDestination != R.id.profileFragment) {
                        navController.navigate(R.id.profileFragment)
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun showBottomNavigationView() {
        binding.bottomNavigationView.visibility = View.VISIBLE
    }

    private fun hideBottomNavigationView() {
        binding.bottomNavigationView.visibility = View.GONE
    }

//    override fun onSupportNavigateUp(): Boolean {
//        return navController.navigateUp() || super.onSupportNavigateUp()
//    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }


    private fun isUserLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("userSession", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun onBoardingFinished(): Boolean {
        val sharedPreferences = getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("Finished", false)
    }

    // Toolbar'ı gizleyen fonksiyon
    private fun hideToolbar() {
        binding.customToolbar.visibility = View.GONE
    }

    // Toolbar'ı gösteren fonksiyon
    private fun showToolbar() {
        binding.customToolbar.visibility = View.VISIBLE
    }

}








/*
package com.farukayata.e_commerce2

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import android.content.Context
import android.view.animation.AnimationUtils
import com.farukayata.e_commerce2.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar'ı desteklenen ActionBar olarak ayarladık
        setSupportActionBar(binding.toolbar)

        // NavHostFragment ve NavController'i bağla
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        // Kullanıcı giriş yaptı mı kontrol et
        if (isUserLoggedIn()) {
            // Kullanıcı giriş yaptıysa onboarding kontrol et
            if (!onBoardingFinished()) {
                navController.navigate(R.id.viewPagerFragment)
            } else {
                navController.navigate(R.id.splashScreenFragment)
            }
        }

        // BottomNavigationView'i NavController ile bağla
        setupBottomNavigationView()

        // AppBarConfiguration ayarla
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.categoryFragment,
                R.id.favoritesFragment,
                //R.id.loginFragment,
                R.id.profileFragment,
                R.id.cartFragment,
            )
        )
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        // BottomNavigation görünürlüğünü kontrol et
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.signUpFragment, R.id.splashScreenFragment, R.id.viewPagerFragment -> {
                    hideBottomNavigationView() // Login ve Signup ekranlarında Navbar'ı gizle
                }
                R.id.homeFragment, R.id.categoryFragment, R.id.favoritesFragment, R.id.cartFragment, R.id.profileFragment -> {
                    //logini geçici olarak koymuştuk profile çektik
                    showBottomNavigationView() // Belirtilen fragment'lerde Navbar görünecek
                }
                else -> {
                    hideBottomNavigationView() // Diğer fragment'lerde Navbar gizlenecek
                }
            }

            // Hangi fragment açıksa, nav bar'da seçili sekme onunla eşleşsin
            binding.bottomNavigationView.menu.findItem(destination.id)?.isChecked = true


        }
    }

    //navbarda gecikmeli sayfa geçişi oluyordu google önerisine göre alttaki gibi revize ettik
//    private fun setupBottomNavigationView() {
//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
//        bottomNavigationView.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.homeFragment -> {
//                    navController.navigate(R.id.homeFragment)
//                    true
//                }
//                R.id.categoryFragment -> {
//                    navController.navigate(R.id.categoryFragment)
//                    true
//                }
//                R.id.favoritesFragment -> {
//                    navController.navigate(R.id.favoritesFragment)
//                    true
//                }
//                R.id.cartFragment -> {
//                    navController.navigate(R.id.cartFragment)
//                    true
//                }
////                R.id.loginFragment -> {
////                    navController.navigate(R.id.loginFragment)
////                    true
////                }
//                //buradada login geçici olarak konmuştu profille revize ettik
//                R.id.profileFragment -> {
//                    navController.navigate(R.id.profileFragment)
//                    true
//                }
//                else -> false
//            }
//        }
//    }

    private fun setupBottomNavigationView() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Google'ın önerdiği gibi, doğrudan NavigationUI ile bağladık
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        bottomNavigationView.setOnItemSelectedListener { item ->
            val currentDestination = navController.currentDestination?.id

            val selectedItemView = bottomNavigationView.findViewById<View>(item.itemId)
            selectedItemView?.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.item_animation)
            )

            bottomNavigationView.menu.findItem(item.itemId).icon?.setTint(
                resources.getColor(R.color.teal_secondary, theme) // Yeni renk eklendi
            )

            when (item.itemId) {
                R.id.homeFragment -> {
                    if (currentDestination != R.id.homeFragment) {
                        navController.navigate(R.id.homeFragment)
                    }
                    true
                }
                R.id.categoryFragment -> {
                    if (currentDestination != R.id.categoryFragment) {
                        navController.navigate(R.id.categoryFragment)
                    }
                    true
                }
                R.id.favoritesFragment -> {
                    if (currentDestination != R.id.favoritesFragment) {
                        navController.navigate(R.id.favoritesFragment)
                    }
                    true
                }
                R.id.cartFragment -> {
                    if (currentDestination != R.id.cartFragment) {
                        navController.navigate(R.id.cartFragment)
                    }
                    true
                }
                R.id.profileFragment -> {
                    if (currentDestination != R.id.profileFragment) {
                        navController.navigate(R.id.profileFragment)
                    }
                    true
                }
                else -> false
            }
        }
    }


    private fun showBottomNavigationView() {
        binding.bottomNavigationView.visibility = View.VISIBLE
    }

    private fun hideBottomNavigationView() {
        binding.bottomNavigationView.visibility = View.GONE
    }

    // Geri tuşuna basıldığında bir önceki fragment'e dönme işlemi
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    // Kullanıcının giriş yapıp yapmadığını kontrol eden fonksiyon
    private fun isUserLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("userSession", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    // Kullanıcının onboarding'i tamamlayıp tamamlamadığını kontrol eden fonksiyon
    private fun onBoardingFinished(): Boolean {
        val sharedPreferences = getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("Finished", false)
    }

}



 */


