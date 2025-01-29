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
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        // BottomNavigationView'i NavController ile bağla
        setupBottomNavigationView()

        // AppBarConfiguration ayarla
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.categoryFragment,
                R.id.favoritesFragment,
                R.id.loginFragment,
                R.id.cartFragment,  // Sepet fragment'i eklendi
            )
        )
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        // BottomNavigation görünürlüğünü kontrol et
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.signUpFragment -> {
                    hideBottomNavigationView() // Login ve Signup ekranlarında Navbar'ı gizle
                }
                R.id.homeFragment, R.id.categoryFragment, R.id.favoritesFragment, R.id.cartFragment, R.id.loginFragment -> {
                    showBottomNavigationView() // Belirtilen fragment'lerde Navbar görünecek
                }
                else -> {
                    hideBottomNavigationView() // Diğer fragment'lerde Navbar gizlenecek
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
                R.id.cartFragment -> {
                    navController.navigate(R.id.cartFragment)
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

    // Geri tuşuna basıldığında bir önceki fragment'e dönme işlemi
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
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