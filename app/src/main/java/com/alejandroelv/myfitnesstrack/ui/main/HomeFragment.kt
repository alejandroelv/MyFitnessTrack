package com.alejandroelv.myfitnesstrack.ui.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.alejandroelv.myfitnesstrack.R
import com.alejandroelv.myfitnesstrack.TimeUtils
import com.alejandroelv.myfitnesstrack.data.model.Day
import com.alejandroelv.myfitnesstrack.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    private lateinit var day: Day

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_home, container, false)
        binding = FragmentHomeBinding.bind(view)

        fetchDayData(TimeUtils().getTodayDate())
        fetchUserData()

        //TODO 1 (DONE): Implementar añadir y quitar el agua
        binding.buttonAddGlass.setOnClickListener{ addWaterGlass(1) }

        binding.buttonRemoveGlass.setOnClickListener{ addWaterGlass(-1) }

        //TODO 2 (DONE): Implementar la sección de food
        binding.buttonAddFood.setOnClickListener{
            val navController = findNavController()
            navController.navigate(R.id.action_homeFragment_to_diaryFragment)
        }

        //TODO 3: Implementar la sección de sleep
        binding.sleepSection.setOnClickListener{

        }

        //TODO 4: Implementar la sección de body composition

        //TODO 5: Implementar la sección de ejercicio

        return view
    }

    private fun fetchDayData(date: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user!!.uid

        val db = FirebaseFirestore.getInstance()
        val userDocumentRef = db.collection("users").document(uid)
        val dayDocumentRef = userDocumentRef.collection("days")

        dayDocumentRef
            .whereEqualTo("date", date)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Handle successful query results
                if (querySnapshot.isEmpty) {
                    // Document doesn't exist for the given date
                    day = Day(date)
                    sendInfoToDiary()
                } else {
                    // Document exists, access the first matching document
                    val documentSnapshot = querySnapshot.documents[0]
                    day = documentSnapshot.toObject(Day::class.java)!!
                    day.id = documentSnapshot.id
                }

                showDayData()
            }
            .addOnFailureListener { exception ->
                Log.e("TAG", "Error querying documents: ${exception.message}")
            }
    }

    private fun fetchUserData(){
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user!!.uid

        val db = FirebaseFirestore.getInstance()
        val userDocumentRef = db.collection("users").document(uid)

        userDocumentRef
            .get()
            .addOnSuccessListener { documentSnapshot ->
                binding.tvUserWeight.text = "${documentSnapshot.getDouble("weight")} kgs"
            }
            .addOnFailureListener{ exception ->
                Log.e("TAG", "Error querying documents: ${exception.message}")
            }
    }

    private fun showDayData(){
        if(day.waterGlasses > 0){
            binding.buttonRemoveGlass.isEnabled = true
        }
        binding.tvTodayCalories.text = day.getMealCalories().toInt().toString()
        binding.tvGoalCalories.text = "/${day.goalCalories.toInt()} kcal"
        binding.tvWaterGlasses.text = day.waterGlasses.toString()
        binding.tvWalkedSteps.text = day.walkedSteps.toString()

        if(day.walkedSteps == 0){
            binding.progressBarSteps.progress = 0
        }else{
            binding.progressBarSteps.progress = 6000 / day.walkedSteps
        }

        binding.tvHoursSlept.text = "${day.hoursOfSleep}h ${day.minutesOfSleep}m"

        if(TimeUtils().toMinutes(day.hoursOfSleep, day.minutesOfSleep) == 0){
            binding.progressBarSleep.progress = 0
        }else{
            binding.progressBarSleep.progress = (8 * 60) / TimeUtils().toMinutes(day.hoursOfSleep, day.minutesOfSleep)
        }
    }

    private fun sendInfoToDiary() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            val db = FirebaseFirestore.getInstance()
            val userDocumentRef = db.collection("users").document(uid)

            val dayDocumentRef = if(day.id == null){
                day.goalCalories = (context?.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                    ?.getInt("goalCalories", 0)?.toLong() ?: 0L).toDouble()

                userDocumentRef.collection("days").document()
            }else{
                userDocumentRef.collection("days").document(day.id!!)
            }

            dayDocumentRef?.set(day)
                ?.addOnSuccessListener {
                    day.id = dayDocumentRef.id
                    showDayData()
                    Log.e("Saved", "Saved succesfully")
                }
                ?.addOnFailureListener { exception ->
                    Log.e("No save", exception.message.toString())
                }
        }
    }

    private fun addWaterGlass(numberOfGlasses: Int){
        if(this.day.waterGlasses == 0 && numberOfGlasses == -1){ return }

        this.day.waterGlasses += numberOfGlasses

        binding.tvWaterGlasses.text = this.day.waterGlasses.toString()

        if(this.day.waterGlasses == 0){
            binding.buttonRemoveGlass.isEnabled = false
        }else if(!binding.buttonRemoveGlass.isEnabled){
            binding.buttonRemoveGlass.isEnabled = true
        }

        sendInfoToDiary()
    }
}