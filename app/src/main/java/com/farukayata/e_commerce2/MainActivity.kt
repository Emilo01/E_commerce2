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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.farukayata.e_commerce2.databinding.ActivityMainBinding
import com.farukayata.e_commerce2.ui.component.CustomToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
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
                    binding.customToolbar.showBackCard()
                    binding.customToolbar.showBackIcon()
                }
                R.id.homeFragment, R.id.categoryFragment, R.id.favoritesFragment, R.id.cartFragment -> {
                    showBottomNavigationView()
                    showToolbar()
                    binding.customToolbar.showCartIcon()
                    binding.customToolbar.showCartCard()
                    binding.customToolbar.hideBackCard()
                    binding.customToolbar.hideBackIcon()
                }

                R.id.orderConfirmationFragment -> {
                    binding.customToolbar.hideCartIcon()
                    binding.customToolbar.hideCartCard()
                    binding.customToolbar.hideBackCard()
                    binding.customToolbar.hideBackIcon()
                }

                R.id.paymentSelectionFragment -> {
                    showBottomNavigationView()
                    showToolbar()
                    binding.customToolbar.showCartCard()
                    binding.customToolbar.showBackCard()
                    binding.customToolbar.showBackIcon()
                }
                R.id.couponsFragment,R.id.profileDetailFragment,R.id.ordersFragment -> {
                    showToolbar()
                    hideBottomNavigationView()
                    binding.customToolbar.showCartCard()
                    binding.customToolbar.showBackCard()
                    binding.customToolbar.showBackIcon()
                }

                R.id.categorySpecialFragment,R.id.detailFragment,R.id.receiptFragment -> {
                    hideBottomNavigationView()
                    showToolbar()
                    //binding.customToolbar.showBackBack()
                    binding.customToolbar.showCartCard()
                    binding.customToolbar.showBackCard()
                    binding.customToolbar.showBackIcon()
                }

                R.id.profileFragment -> {
                    showBottomNavigationView()
                    showToolbar()
                    binding.customToolbar.hideCartIcon()
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
            R.id.homeFragment -> getString(R.string.title_home) //"Cepte Al"
            R.id.categoryFragment -> getString(R.string.title_category)
            R.id.favoritesFragment -> getString(R.string.title_favorites)
            R.id.cartFragment -> getString(R.string.title_cart)
            R.id.profileFragment -> getString(R.string.title_profile)
            R.id.detailFragment -> getString(R.string.title_detail)
            R.id.categorySpecialFragment -> getString(R.string.title_category_special)
            R.id.profileDetailFragment -> getString(R.string.title_profile_detail)
            R.id.ordersFragment -> getString(R.string.title_orders)
            R.id.couponsFragment -> getString(R.string.title_coupons)
            R.id.paymentSelectionFragment -> getString(R.string.title_payment_selection)
            R.id.cardPaymentFragment -> getString(R.string.title_card_payment)
            R.id.orderConfirmationFragment -> getString(R.string.title_order_confirmation)
            R.id.receiptFragment -> getString(R.string.title_receipt)
            else -> getString(R.string.title_default)
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