package com.alejandroelv.myfitnesstrack.ui.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        fetchUserData()

        binding.buttonSave.setOnClickListener{
            saveUserData()
        }
    }

    private fun checkUserInput() : Boolean{
        val inputWeight = binding.etWeight.text?.toString()?.toDouble()
        val inputBodyFat = binding.etBodyFat.text?.toString()?.toDouble()
        val inputMuscleMass = binding.etMuscleMass.text?.toString()?.toDouble()

        if (inputWeight != null) {
            if(inputWeight <= 0.0){
                Toast.makeText(this@RegisterWeightActivity, R.string.invalid_weight, Toast.LENGTH_SHORT).show()
                Log.e("Check error", "El peso se considera menor que 0")
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
        Log.e("Check error", "Voy a devolver true")
        return true
    }

    private fun fetchUserData(){
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user!!.uid

        val db = FirebaseFirestore.getInstance()
        val userDocumentRef = db.collection("users").document(uid)

        userDocumentRef
            .get()
            .addOnSuccessListener { documentSnapshot ->
                userData.weight = (documentSnapshot.get("weight") as Long).toInt()
                userData.height = (documentSnapshot.get("height") as Long).toInt()
                userData.age = (documentSnapshot.get("age") as Long).toInt()
                userData.gender = (documentSnapshot.get("gender") as Long).toInt()
                userData.goal = (documentSnapshot.get("goal") as Long).toInt()
                userData.goalByWeek = (documentSnapshot.get("goalByWeek") as Double)
            }
            .addOnFailureListener{ exception ->
                Log.e("TAG", "Error querying documents: ${exception.message}")
            }
    }


    private fun saveUserData(){
        if(!checkUserInput()){
            Log.e("Check error", "Justo antes del return")
            return
        }

        val auth = FirebaseAuth.getInstance()
        val userId: String = auth.currentUser!!.uid
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val userDocumentRef = db.collection("users").document(userId)

        val userMap = hashMapOf<String, Any>()
        userMap["id"] = userId
        userMap["weight"] = binding.etWeight.text.toString().toInt()
        userMap["gender"] = userData.gender
        userMap["age"] = userData.age
        userMap["height"] = userData.height
        userMap["goal"] = userData.goal
        userMap["goalByWeek"] = userData.goalByWeek
        userMap["goalCalories"] = (88.362 + (13.397 * userData.weight) + (4.799 * userData.height) - (5.677 * userData.age))

        val goalCalories: Double = userMap["goalCalories"] as Double
        val roundedGoalCalories = goalCalories.roundToInt()

        val sharedPref: SharedPreferences? = this.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor? = sharedPref?.edit()
        editor?.putInt("goalCalories", roundedGoalCalories)
        editor?.apply()

        userDocumentRef.set(userMap)
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