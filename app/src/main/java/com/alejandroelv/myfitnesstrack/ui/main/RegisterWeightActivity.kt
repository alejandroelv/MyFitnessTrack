package com.alejandroelv.myfitnesstrack.ui.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alejandroelv.myfitnesstrack.FirebaseUtils
import com.alejandroelv.myfitnesstrack.R
import com.alejandroelv.myfitnesstrack.data.model.User
import com.alejandroelv.myfitnesstrack.databinding.ActivityRegisterWeightBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.roundToInt

class RegisterWeightActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterWeightBinding
    private val userData: User = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterWeightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseUtils().getUserData(userData)

        binding.buttonSave.setOnClickListener{
            saveUserData()
        }
    }

    private fun checkUserInput() : Boolean{
        val inputWeight = binding.etWeight.text?.toString()?.toDoubleOrNull()
        val inputBodyFat = binding.etBodyFat.text?.toString()?.toDoubleOrNull()
        val inputMuscleMass = binding.etMuscleMass.text?.toString()?.toDoubleOrNull()

        if (inputWeight != null) {
            if(inputWeight <= 0.0){
                Toast.makeText(this@RegisterWeightActivity, R.string.invalid_weight, Toast.LENGTH_SHORT).show()
                return false
            }
        }

        if (inputBodyFat != null) {
            if((inputBodyFat < 0) || (inputBodyFat > 100)){
                Toast.makeText(this@RegisterWeightActivity, "The body fat introduced must be between 0 and 100", Toast.LENGTH_SHORT).show()
            }
        }

        if (inputMuscleMass != null) {
            if((inputMuscleMass < 0) || (inputMuscleMass > inputWeight!!)){
                Toast.makeText(this@RegisterWeightActivity, "The muscle mass introduced must be between 0 and the weight introduced", Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }

    private fun saveUserData(){
        if(!checkUserInput()){
            return
        }

        userData.weight = binding.etWeight.text.toString().toInt()

        FirebaseUtils().saveUserData(userData, this)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    val intent = Intent(this@RegisterWeightActivity, MainActivity::class.java)
                    startActivity(intent)
                }else{
                    Log.e("Error", "Failure on save")
                }
            }.addOnFailureListener{
                Log.e("Error", "Failure on save")
            }
    }
}