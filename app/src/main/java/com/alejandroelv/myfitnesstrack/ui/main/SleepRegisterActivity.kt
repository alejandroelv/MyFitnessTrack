package com.alejandroelv.myfitnesstrack.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alejandroelv.myfitnesstrack.FirebaseUtils
import com.alejandroelv.myfitnesstrack.TimeUtils
import com.alejandroelv.myfitnesstrack.data.model.Day
import com.alejandroelv.myfitnesstrack.databinding.ActivitySleepRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime
import java.util.*

class SleepRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySleepRegisterBinding
    private lateinit var day: Day

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySleepRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchDayData(TimeUtils().getTodayDate())

        binding.buttonSave.setOnClickListener{
            sendInfoToDiary()
        }
    }

    private fun fetchDayData(date: String) {
        FirebaseUtils().getDayReference(date)
            .addOnSuccessListener { querySnapshot ->
                // Handle successful query results
                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents[0]
                    day = documentSnapshot.toObject(Day::class.java)!!
                }
            }
            .addOnFailureListener { exception ->
                Log.e("TAG", "Error querying documents: ${exception.message}")
            }
    }

    private fun checkUserInput() : Boolean{
        val sleepTime = binding.etSleepTime.text.toString()
        val wakeUpTime = binding.etAwakenTime.text.toString()

        if(checkFormatTime(sleepTime) || checkFormatTime(wakeUpTime)){
            return false
        }

        return true
    }

    private fun checkFormatTime(timeInput: String) : Boolean{
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        var isValid = true

        try {
            val date = format.parse(timeInput)
            val calendar = Calendar.getInstance()
            if (date != null) {
                calendar.time = date
            }

        } catch (e: ParseException) {
            // Invalid time input
            Toast.makeText(this, "Invalid time format", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun calculateHoursAndMinutes(){
        val inputSleep = binding.etSleepTime.text.toString()
        val inputWakeUp = binding.etAwakenTime.text.toString()

        val startTime = LocalTime.parse(inputSleep)
        var endTime = LocalTime.parse(inputWakeUp)

        // Calculate the duration between the times
        val duration = Duration.between(startTime, endTime)

        // Get the difference in hours and minutes
        if(duration.toHours() < 0){
            day.hoursOfSleep = duration.toHours().toInt() + 24
        }else{
            day.hoursOfSleep = duration.toHours().toInt()
        }
        day.minutesOfSleep = (duration.toMinutes() % 60).toInt()
    }

    private fun sendInfoToDiary() {
        if(checkUserInput()){ return }

        calculateHoursAndMinutes()

        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            val db = FirebaseFirestore.getInstance()
            val userDocumentRef = db.collection("users").document(uid)
            val dayDocumentRef = userDocumentRef.collection("days")

            dayDocumentRef
                .whereEqualTo("date", TimeUtils().getTodayDate()).limit(1)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]
                        val ref = userDocumentRef.collection("days").document(document.id)
                        day.id = document.id

                        ref.set(day).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.e("Success", "Succesful update")
                                backToMain()
                            } else {
                                Log.e("Error", "Error on update")
                            }
                        }
                    } else {
                        Log.e("Error", "No document found")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("TAG", "Error querying documents: ${exception.message}")
                }
        }
    }

    private fun backToMain(){
        val intent = Intent(this@SleepRegisterActivity, MainActivity::class.java)
        startActivity(intent)
    }
}