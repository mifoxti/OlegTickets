package com.example.tickets

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val isDarkTheme = sharedPreferences.getBoolean("dark_theme", false)
        if (isDarkTheme) {
            setTheme(R.style.Theme_Tickets_Dark)
        } else {
            setTheme(R.style.Theme_Tickets)
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_search -> {
                    loadFragment(SearchFragment())
                    true
                }
                R.id.navigation_orders -> {
                    loadFragment(OrdersFragment())
                    true
                }
                R.id.navigation_passengers -> {
                    loadFragment(PassengersFragment())
                    true
                }
                R.id.navigation_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            loadFragment(SearchFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

}