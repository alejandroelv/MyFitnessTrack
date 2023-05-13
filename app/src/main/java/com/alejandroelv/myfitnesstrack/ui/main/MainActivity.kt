package com.alejandroelv.myfitnesstrack.ui.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.alejandroelv.myfitnesstrack.R
import com.alejandroelv.myfitnesstrack.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //TODO 1: Implementar el bottomNavigation
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
                    return@setOnItemSelectedListener true
                }
                R.id.nav_diary -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, DiaryFragment()).commit()
                    return@setOnItemSelectedListener true
                }
                R.id.nav_profile -> {
                    // Add the transaction for the ProfileFragment here if needed
                    return@setOnItemSelectedListener true
                }
                else -> return@setOnItemSelectedListener false
            }
        }

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
    }
}
