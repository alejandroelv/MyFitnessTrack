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
import com.alejandroelv.myfitnesstrack.TimeUtils
import com.alejandroelv.myfitnesstrack.data.model.Day
import com.alejandroelv.myfitnesstrack.data.model.edamamModels.Hint
import com.alejandroelv.myfitnesstrack.databinding.FragmentDiaryBinding
import com.alejandroelv.myfitnesstrack.ui.adapters.FoodAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class DiaryFragment : Fragment() {
    private lateinit var binding: FragmentDiaryBinding
    private lateinit var day: Day

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_diary, container, false)
        binding = FragmentDiaryBinding.bind(view)

        fetchDayData(TimeUtils().getTodayDate())

        //TODO 5: Añadir que se actualicen los valores de las calorias de la seccion Remaining Calories

        binding.tvAddBreakfast.setOnClickListener{ callSearchFoods("breakfast") }

        binding.tvAddLunch.setOnClickListener{ callSearchFoods("lunch") }

        binding.tvAddDinner.setOnClickListener{ callSearchFoods("dinner") }

        binding.tvAddSnacks.setOnClickListener{ callSearchFoods("snacks") }

        //TODO 9: Poner un textListener a food/ExerciseCalories/ para recalcular las calorias (Implementar otra recarga?)

        return view
    }

    //TODO 3: Llamar a firebase para traerme el Day
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
                    day = Day()
                    sendInfoToDiary()
                } else {
                    // Document exists, access the first matching document
                    val documentSnapshot = querySnapshot.documents[0]
                    day = documentSnapshot.toObject(Day::class.java)!!
                    day.id = documentSnapshot.id
                    initAllRecyclerViews()
                    setCalories()
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure while querying the documents
                Log.e("TAG", "Error querying documents: ${exception.message}")
                // Perform error handling or show an error message to the user
            }
    }

    private fun initAllRecyclerViews(){
        this.initRecyclerView(binding.rvBreakfast, "breakfast")
        this.initRecyclerView(binding.rvLunch, "lunch")
        this.initRecyclerView(binding.rvDinner, "dinner")
        this.initRecyclerView(binding.rvSnacks, "snacks")
    }

    private fun initRecyclerView(rv: RecyclerView, meal: String){
       rv.adapter = this.day.meals[meal]?.let {
           FoodAdapter(it.foods, object : FoodAdapter.OnItemClickListener {
               override fun onItemClick(result: Hint) { callFoodDetails(result, meal) }
           })
       }

        rv.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL,false)
    }

    private fun callSearchFoods(meal: String){
        val intent = Intent(this.context, SearchFoodActivity::class.java)
        intent.putExtra("meal", meal)
        startActivity(intent)
    }

    private fun setCalories(){
        val exerciseCalories = day.getExerciseCalories()
        val mealCalories = day.getMealCalories()
        val goalCalories = day.goalCalories

        binding.tvFoodCalories.text = mealCalories.toInt().toString()
        binding.tvExerciseCalories.text = exerciseCalories.toInt().toString()
        binding.tvGoalCalories.text = goalCalories.toInt().toString()
        binding.tvRemainingCalories.text = (goalCalories - mealCalories - exerciseCalories).toInt().toString()
    }

    private fun callFoodDetails(food: Hint, meal: String){
        //TODO 4.5: Llamar a FoodDetailsActivity
        val intent = Intent(this.context, FoodDetailsActivity::class.java)
        intent.putExtra("food", food)
        intent.putExtra("meal", meal)
        startActivity(intent)
    }

    private fun sendInfoToDiary() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            val db = FirebaseFirestore.getInstance()
            val userDocumentRef = db.collection("users").document(uid)
            val dayDocumentRef = userDocumentRef.collection("days").document()

            dayDocumentRef.set(day)
                .addOnSuccessListener {
                    Log.e("Saved", "Saved succesfully")
                }
                .addOnFailureListener { exception ->
                    Log.e("No save", exception.message.toString())
                }
        }
    }
}