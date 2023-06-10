package com.alejandroelv.myfitnesstrack.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alejandroelv.myfitnesstrack.databinding.ActivitySleepRegisterBinding

class SleepRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySleepRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySleepRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}