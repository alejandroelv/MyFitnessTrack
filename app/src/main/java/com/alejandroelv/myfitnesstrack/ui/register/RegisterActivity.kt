package com.alejandroelv.myfitnesstrack.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alejandroelv.myfitnesstrack.R
import com.alejandroelv.myfitnesstrack.ui.register.fragments.RegisterUserGoal
import com.alejandroelv.myfitnesstrack.ui.register.fragments.RegisterWeight
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var currentFragment = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, RegisterWeight())
            .commit()
    }
}