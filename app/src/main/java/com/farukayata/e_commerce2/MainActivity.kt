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









/*
package com.farukayata.e_commerce2

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
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

        // Toolbar'ı desteklenen ActionBar olarak ayarla
        setSupportActionBar(binding.toolbar)

        // NavHostFragment ve NavController'i bağla
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        // BottomNavigationView'i NavController ile bağla
        setupBottomNavigationView()

        // AppBarConfiguration ayarla
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.categoryFragment,
                R.id.favoritesFragment,
                R.id.loginFragment
            )
        )
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        // BottomNavigation görünürlüğünü kontrol et
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // 'destination', geçilen(mevcutta gördüpümüz fragment) fragment'in ID'sini temsil eder.
            when (destination.id) {
                R.id.loginFragment, R.id.signUpFragment -> {
                    hideBottomNavigationView() // Login ve Signup ekranlarında Navbar'ı gizle
                }
                // Belirtilen fragment'lerde BottomNavigationView görünür olacak
                R.id.homeFragment, R.id.categoryFragment, R.id.favoritesFragment, R.id.loginFragment -> {
                    showBottomNavigationView()
                    // Diğer tüm fragment'lerde BottomNavigationView gizlenecek
                }
                //toolbarın gözükmemesi bir güzel gözükmedi şimdilik yorum da kalsın
//                R.id.loginFragment, R.id.signUpFragment -> {
//                    hideBottomNavigationView()
//                    hideToolbar() // Login ve Signup ekranlarında Toolbar'ı gizle
//                }

                else -> {
                    //destination.id başka bir fragment'in ID'sine eşitse
                hideBottomNavigationView()
                    //showToolbar() // Diğer fragmentlerde Toolbar görünebilir

                }
            }
        }
    }


    private fun setupBottomNavigationView() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.categoryFragment -> {
                    navController.navigate(R.id.categoryFragment)
                    true
                }
                R.id.favoritesFragment -> {
                    navController.navigate(R.id.favoritesFragment)
                    true
                }
                R.id.loginFragment -> {
                    navController.navigate(R.id.loginFragment)
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

    // geri tuşuna basıldığında bir önceki fragment'e dönme işlemi
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
//    private fun showToolbar() {
//        binding.toolbar.visibility = View.VISIBLE
//    }
//
//    private fun hideToolbar() {
//        binding.toolbar.visibility = View.GONE
//    }
}



 */





/*
package com.farukayata.e_commerce2

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
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

        // NavHostFragment ve NavController'i bağla
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        // BottomNavigationView'i NavController ile bağla
        setupBottomNavigationView()

        // AppBarConfiguration ayarla
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.categoryFragment,
                R.id.favoritesFragment,
                //R.id.profileHomeFragment
                R.id.loginFragment
            )
        )
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        // BottomNavigation görünürlüğünü kontrol et
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.categoryFragment, R.id.favoritesFragment, R.id.loginFragment -> {
                    showBottomNavigationView()
                }
                else -> {
                    hideBottomNavigationView()
                }
            }
        }
    }

    private fun setupBottomNavigationView() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.categoryFragment -> {
                    navController.navigate(R.id.categoryFragment)
                    true
                }
                R.id.favoritesFragment -> {
                    navController.navigate(R.id.favoritesFragment)
                    true
                }
                R.id.loginFragment -> {
                    navController.navigate(R.id.loginFragment)
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

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}



 */






/*
package com.farukayata.e_commerce2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.farukayata.e_commerce2.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        // NavController bağlantısı
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        // BottomNavigationView'i NavController ile bağlama
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
    }
}



 */








/*
package com.farukayata.e_commerce2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

 */