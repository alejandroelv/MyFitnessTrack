package com.alejandroelv.myfitnesstrack.data.model

import java.io.Serializable

class Exercise(var name: String, var kcalsPerMinute: Int) : Serializable {
    var duration = 0
    var caloriesBurned = 0.0

    fun calculateCaloriesBurned(userWeight: Int) {
        //Duration (in minutes)*(MET*3.5*weight in kg)/200
        caloriesBurned = duration * (userWeight * duration * 3.5 * userWeight) / 200
    }
}