package com.alejandroelv.myfitnesstrack.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alejandroelv.myfitnesstrack.R
import com.alejandroelv.myfitnesstrack.data.model.Day
import com.alejandroelv.myfitnesstrack.data.model.edamamModels.Hint
import com.alejandroelv.myfitnesstrack.databinding.FragmentDiaryBinding
import com.alejandroelv.myfitnesstrack.ui.adapters.FoodAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

class DiaryFragment : Fragment() {
    private lateinit var binding: FragmentDiaryBinding
    private lateinit var day: Day
    private lateinit var activityLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_diary, container, false)
        binding = FragmentDiaryBinding.bind(view)

        initLauncher()
        fetchDayData(Calendar.getInstance().time.toString())

        this.initRecyclerView(binding.rvBreakfast, "breakfast")
        this.initRecyclerView(binding.rvLunch, "lunch")
        this.initRecyclerView(binding.rvDinner, "dinner")
        this.initRecyclerView(binding.rvSnacks, "snacks")

        //TODO 5: Añadir que se actualicen los valores de las calorias de la seccion Remaining Calories

        binding.tvAddBreakfast.setOnClickListener{
            callSearchFoods("breakfast")
        }

        binding.tvAddLunch.setOnClickListener{
            callSearchFoods("lunch")
        }

        binding.tvAddDinner.setOnClickListener{
            callSearchFoods("dinner")
        }

        binding.tvAddSnacks.setOnClickListener{
            callSearchFoods("snacks")
        }

        //TODO 9: Poner un textListener a food/ExerciseCalories/ para recalcular las calorias (Implementar otra recarga?)

        return view
    }

    //TODO 3: Llamar a firebase para traerme el Day

    private fun fetchDayData(date: String){
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user!!.uid

        val reference = FirebaseDatabase.getInstance().getReference("users")
            .child(uid)
            .child("days")
            .child(date)

        //TODO 10: Testar fetchDayData
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // The object data is available in dataSnapshot
                if (dataSnapshot.exists()) {
                    day = dataSnapshot.getValue(Day::class.java)!!

                } else {
                    // Object does not exist in the reference

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun initLauncher(){
        activityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val meal = data?.getStringExtra("meal")
                val newFood = data?.getSerializableExtra("hintList") as? Hint

                if (newFood != null) {
                    //Add or update the food
                    day.meals[meal]?.foods = addFoodToList(day.meals[meal]!!.foods, newFood)

                    //TODO 7: Almacenar el desayuno/lunch en la base de datos (Almacenar el dia entero?)
                    updateDay()

                    //TODO 8: Actualizar las foodCalories

                }
            }
        }
    }

    private fun initRecyclerView(rv: RecyclerView, meal: String){
       rv.adapter = this.day.meals[meal]?.let {
           FoodAdapter(it.foods, object : FoodAdapter.OnItemClickListener {
               override fun onItemClick(result: Hint) { callFoodDetails(result) }
           })
       }

        rv.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL,false)
    }

    private fun callSearchFoods(meal: String){
        val intent = Intent(this.context, SearchFoodActivity::class.java)
        intent.putExtra("meal", meal)
        activityLauncher.launch(intent)
    }

    private fun calculateRemainingCalories(){
        val exerciseCalories = Integer.valueOf(binding.tvExerciseCalories.text.toString())
        val goalCalories = Integer.valueOf(binding.tvGoalCalories.text.toString())
        val foodCalories = Integer.valueOf(binding.tvFoodCalories.text.toString())
        binding.tvRemainingCalories.text = (goalCalories - foodCalories + exerciseCalories).toString()
    }

    private fun callFoodDetails(food: Hint){
        //TODO 4.5: Llamar a FoodDetailsActivity
//        val callFoodDetails = Intent(this@DiaryFragment, PeliculaActivity::class.java)
//        callFoodDetails.putExtra("id", result.foodId)
//        startActivity(callFoodDetails)
        Log.e("si", food.food?.foodId!!)
    }

    private fun searchFoodOnList(foodList: List<Hint>, foodId: String) : Int{
        return foodList.indexOfFirst { hint -> hint.food?.foodId == foodId }
    }

    private fun addFoodToList(foodList: List<Hint>, newFood: Hint) : List<Hint>{
        //If the food is not on the list, add it. Otherwise, update it
        val foodPosition :Int = searchFoodOnList(foodList, newFood.food?.foodId!!)
        var newFoodList = foodList.toMutableList()

        if(foodPosition == -1){
            newFoodList.add(newFood)
        }else{
            newFoodList[foodPosition] = newFood
        }

        return newFoodList
    }

    private fun updateDay(){
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            val reference = FirebaseDatabase.getInstance().getReference("users")
                .child(uid).child("days").child(day.date.toString())

            val updates = HashMap<String, Any>()
            updates["date"] = day.date
            updates["meals"] = day.meals

            //TODO 10: Testar almacenamiento del day en Firebase
            reference.push().updateChildren(updates)
                .addOnSuccessListener {
                    // Value updated successfully


                }
                .addOnFailureListener {
                    // An error occurred while updating the value

                }
        }
    }
}