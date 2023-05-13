package com.alejandroelv.myfitnesstrack.data.model

import com.alejandroelv.myfitnesstrack.data.model.Meal
import com.alejandroelv.myfitnesstrack.data.model.edamamModels.Hint
import java.util.*

class Day {
    var date: Date
    var meals: HashMap<String, Meal>

    //TODO 1: Implementar que también se almacenen los ejercicios
    constructor() {
        date = Calendar.getInstance().time
        meals = HashMap()
        meals["breakfast"] = Meal("breakfast", ArrayList<Hint>())
        meals["lunch"] = Meal("lunch", ArrayList<Hint>())
        meals["dinner"] = Meal("dinner", ArrayList<Hint>())
        meals["snacks"] = Meal("snacks", ArrayList<Hint>())
    }

    constructor(date: Date, meals: HashMap<String, Meal>) {
        this.date = date
        this.meals = meals
    }
}