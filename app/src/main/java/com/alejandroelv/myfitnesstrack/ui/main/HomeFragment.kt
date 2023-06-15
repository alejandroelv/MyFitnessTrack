package com.alejandroelv.myfitnesstrack.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.alejandroelv.myfitnesstrack.FirebaseUtils
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

        binding.buttonAddGlass.setOnClickListener{ addWaterGlass(1) }

        binding.buttonRemoveGlass.setOnClickListener{ addWaterGlass(-1) }

        binding.buttonAddFood.setOnClickListener{
            val navController = findNavController()
            navController.navigate(R.id.action_homeFragment_to_diaryFragment)
        }

        binding.sleepSection.setOnClickListener{
            val intentSleep = Intent(this.context, SleepRegisterActivity::class.java)
            startActivity(intentSleep)
        }

        binding.buttonChangeWeight.setOnClickListener{
            val intentWeight = Intent(this.context, RegisterWeightActivity::class.java)
            startActivity(intentWeight)
        }
        //TODO 5: Implementar la sección de ejercicio

        return view
    }

    private fun fetchDayData(date: String) {
        FirebaseUtils().getDayReference(date)
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
            val totalMinutesOfSleep = TimeUtils().toMinutes(day.hoursOfSleep, day.minutesOfSleep)
            val targetMinutesOfSleep = 8 * 60
            var percentage = ((totalMinutesOfSleep.toDouble() / targetMinutesOfSleep) * 100).toInt()

            if(percentage > 100){
                percentage = 100
            }
            binding.progressBarSleep.progress = percentage
        }
    }

    private fun sendInfoToDiary() {
        FirebaseUtils().saveDayByFirstTime(day, requireContext())
            .addOnSuccessListener {
                 FirebaseUtils().getDayId(day.date) { id ->
                    // Handle the id here
                    if (id != null) {
                        day.id = id
                        showDayData()
                    }
                }
                Log.e("Saved", "Saved succesfully")
            }
            .addOnFailureListener { exception ->
                Log.e("No save", exception.message.toString())
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