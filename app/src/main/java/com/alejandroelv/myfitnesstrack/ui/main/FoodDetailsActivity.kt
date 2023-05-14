package com.alejandroelv.myfitnesstrack.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alejandroelv.myfitnesstrack.R
import com.alejandroelv.myfitnesstrack.data.model.edamamModels.Hint
import com.alejandroelv.myfitnesstrack.databinding.ActivityFoodDetailsBinding

class FoodDetailsActivity : AppCompatActivity() {
    var food: Hint? = null
    var meal: String? = null
    lateinit var binding: ActivityFoodDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityFoodDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        food = intent.extras?.getSerializable("food") as? Hint
        meal = intent.extras?.getString("meal")

        if(food != null){
            displayFoodDetails()
        }
    }

    private fun displayFoodDetails(){
        binding.tvMeal.text = meal

        if(meal != null){
            binding.tvMeal.text = meal
        }else{
            binding.tvMeal.text = getString(R.string.breakfast)
        }

        //TODO IMPORTANTE 1: Implementar la seleccion de raciones
        //TODO IMPORTANTE 2: Almacenar la comida seleccionada como un objeto
        //TODO IMPORTANTE 3: Devolver dicho objeto a DiaryFragment y hacer que lo almacene en la meal correspondiente
        binding.tvFoodName.text = food?.food?.label
        binding.tvNumberOfServings.text = food?.food?.nutrients?.procnt.toString()
        binding.tvServingSize.text = food?.measures?.get(0)?.label
        binding.tvCalories.text = food?.food?.nutrients?.enercKcal.toString()
        binding.tvCarbohydrates.text = food?.food?.nutrients?.chocdf.toString()
        binding.tvFat.text = food?.food?.nutrients?.fat.toString()
        binding.tvProtein.text = food?.food?.nutrients?.procnt.toString()
    }
}