package com.alejandroelv.myfitnesstrack.data.model

import com.alejandroelv.myfitnesstrack.TimeUtils

class Day {
    var id: String? = null
    var date: String
    var meals: HashMap<String, Meal>
    var exercises: List<Exercise>
    var goalCalories: Double
    var waterGlasses: Int
    var hoursOfSleep: Int
    var minutesOfSleep: Int
    var walkedSteps: Int

    constructor() {
        date = TimeUtils().getTodayDate()
        meals = HashMap()
        meals["breakfast"] = Meal("breakfast", ArrayList())
        meals["lunch"] = Meal("lunch", ArrayList())
        meals["dinner"] = Meal("dinner", ArrayList())
        meals["snacks"] = Meal("snacks", ArrayList())
        exercises = ArrayList()
        goalCalories = 0.0
        waterGlasses = 0
        hoursOfSleep = 0
        minutesOfSleep = 0
        walkedSteps = 0
    }

    constructor(date: String) {
        this.date = date
        meals = HashMap()
        meals["breakfast"] = Meal("breakfast", ArrayList())
        meals["lunch"] = Meal("lunch", ArrayList())
        meals["dinner"] = Meal("dinner", ArrayList())
        meals["snacks"] = Meal("snacks", ArrayList())
        exercises = ArrayList()
        goalCalories = 0.0
        waterGlasses = 0
        hoursOfSleep = 0
        minutesOfSleep = 0
        walkedSteps = 0
    }

    fun getExerciseCalories() : Double{
        var totalCalories = 0.0

        for(exercise in exercises){
            totalCalories += exercise.caloriesBurned
        }

        return totalCalories
    }

    fun getMealCalories() : Double{
        var totalCalories = 0.0

        for(meal in meals.keys){
            totalCalories += meals[meal]?.totalKcal!!
        }

        return totalCalories
    }
}