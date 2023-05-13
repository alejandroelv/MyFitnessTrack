package com.alejandroelv.myfitnesstrack.data.model

import com.alejandroelv.myfitnesstrack.data.model.edamamModels.Hint
import java.io.Serializable

class Meal : Serializable {
    var name: String
    var foods: List<Hint>
    var totalKcal: Double
    var totalProtein: Double
    var totalFat: Double
    var totalCarbohydrates: Double

    constructor() {
        name = ""
        foods = ArrayList()
        totalKcal = 0.0
        totalProtein = 0.0
        totalFat = 0.0
        totalCarbohydrates = 0.0
    }

    constructor(name: String, foods: ArrayList<Hint>) {
        this.name = name
        this.foods = foods
        totalKcal = 0.0
        totalProtein = 0.0
        totalFat = 0.0
        totalCarbohydrates = 0.0
    }

    fun calculateTotals() {
        for (hint in foods) {
            totalKcal += hint.food?.nutrients?.enercKcal!!
            totalProtein += hint.food?.nutrients?.procnt!!
            totalCarbohydrates += hint.food?.nutrients?.chocdf!!
            totalFat += hint.food?.nutrients?.fat!!
        }
    }
}