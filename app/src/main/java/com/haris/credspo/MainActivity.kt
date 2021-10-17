package com.haris.credspo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        bottomNav = findViewById(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)


        navController.addOnDestinationChangedListener { _, destination, _ ->
            println("${R.id.activity_history_fragment} \t ${destination.id}")
            when(destination.id) {
                R.id.splash_fragment,
                R.id.login_fragment,
                R.id.registration_fragment,
                R.id.verification_fragment -> bottomNav.visibility = View.GONE

                else -> bottomNav.visibility = View.VISIBLE
            }
        }
    }
}