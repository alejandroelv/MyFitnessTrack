package com.alejandroelv.myfitnesstrack.data.model

import com.alejandroelv.myfitnesstrack.data.model.Meal
import com.alejandroelv.myfitnesstrack.data.model.edamamModels.Hint
import java.util.*

class Day {
    var date: Date
    var meals: HashMap<String, Meal>
    var exercises: List<Exercise>
    var goalCalories: Double

    constructor() {
        date = Calendar.getInstance().time
        meals = HashMap()
        meals["breakfast"] = Meal("breakfast", ArrayList<Hint>())
        meals["lunch"] = Meal("lunch", ArrayList<Hint>())
        meals["dinner"] = Meal("dinner", ArrayList<Hint>())
        meals["snacks"] = Meal("snacks", ArrayList<Hint>())
        exercises = ArrayList()
        goalCalories = 0.0
    }

    constructor(date: Date, meals: HashMap<String, Meal>, exercises: List<Exercise>, goalCalories: Double) {
        this.date = date
        this.meals = meals
        this.exercises = exercises
        this.goalCalories = goalCalories
    }

    fun getExerciseCalories() : Double{
        var totalCalories: Double = 0.0

        for(exercise in exercises){
            totalCalories += exercise.caloriesBurned
        }

        return totalCalories
    }

    fun getMealCalories() : Double{
        var totalCalories: Double = 0.0

        for(meal in meals.keys){
            totalCalories += meals[meal]?.totalKcal!!
        }

        return totalCalories
    }
}