package com.alejandroelv.myfitnesstrack.ui.register

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alejandroelv.myfitnesstrack.R
import com.alejandroelv.myfitnesstrack.ui.register.fragments.RegisterWeight

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, RegisterWeight())
            .commit()
    }
}