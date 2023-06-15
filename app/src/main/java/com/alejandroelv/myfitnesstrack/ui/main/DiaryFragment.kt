package com.alejandroelv.myfitnesstrack.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alejandroelv.myfitnesstrack.FirebaseUtils
import com.alejandroelv.myfitnesstrack.R
import com.alejandroelv.myfitnesstrack.TimeUtils
import com.alejandroelv.myfitnesstrack.data.model.Day
import com.alejandroelv.myfitnesstrack.data.model.edamamModels.Hint
import com.alejandroelv.myfitnesstrack.databinding.FragmentDiaryBinding
import com.alejandroelv.myfitnesstrack.ui.adapters.FoodDiaryAdapter
import com.alejandroelv.myfitnesstrack.ui.adapters.ItemClickListener

class DiaryFragment : Fragment(), ItemClickListener {
    private lateinit var binding: FragmentDiaryBinding
    private lateinit var day: Day
    private lateinit var date: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_diary, container, false)
        binding = FragmentDiaryBinding.bind(view)

        date = TimeUtils().getTodayDate()
        fetchDayData(date)

        binding.tvAddBreakfast.setOnClickListener{ callSearchFoods("breakfast") }

        binding.tvAddLunch.setOnClickListener{ callSearchFoods("lunch") }

        binding.tvAddDinner.setOnClickListener{ callSearchFoods("dinner") }

        binding.tvAddSnacks.setOnClickListener{ callSearchFoods("snacks") }

        binding.tvDayBefore.setOnClickListener{
            date = TimeUtils().calculateDate(date, -1)
            fetchDayData(date)
        }

        binding.tvDayAfter.setOnClickListener{
            date = TimeUtils().calculateDate(date, 1)
            fetchDayData(date)
        }

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

                initAllRecyclerViews()
                setCalories()

                if(date != TimeUtils().getTodayDate()){
                    binding.tvDay.text = day.date
                }else{
                    binding.tvDay.text = getString(R.string.today)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("TAG", "Error querying documents: ${exception.message}")
            }
    }

    private fun initAllRecyclerViews(){
        this.initRecyclerView(binding.rvBreakfast, "breakfast")
        this.initRecyclerView(binding.rvLunch, "lunch")
        this.initRecyclerView(binding.rvDinner, "dinner")
        this.initRecyclerView(binding.rvSnacks, "snacks")
    }

    private fun initRecyclerView(rv: RecyclerView, meal: String){

        rv.adapter = FoodDiaryAdapter(this.day.meals[meal]?.foods!!,meal ,object : FoodDiaryAdapter.OnItemClickListener{
            override fun onItemClick(result: Hint) { callFoodDetails(result, meal) }
        })

        (rv.adapter as FoodDiaryAdapter).setItemClickListener(this)

        rv.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL,false)
    }

    private fun callSearchFoods(meal: String){
        val intent = Intent(this.context, SearchFoodActivity::class.java)
        intent.putExtra("meal", meal)
        intent.putExtra("date", date)
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

        binding.tvBreakfastCalories.text = day.meals["breakfast"]?.totalKcal?.toInt().toString()
        binding.tvLunchCalories.text = day.meals["lunch"]?.totalKcal?.toInt().toString()
        binding.tvDinnerCalories.text = day.meals["dinner"]?.totalKcal?.toInt().toString()
        binding.tvSnacksCalories.text = day.meals["snacks"]?.totalKcal?.toInt().toString()
    }

    private fun callFoodDetails(food: Hint, meal: String){
        val intent = Intent(this.context, FoodDetailsActivity::class.java)
        intent.putExtra("food", food)
        intent.putExtra("meal", meal)
        intent.putExtra("date", date)
        startActivity(intent)
    }

    private fun sendInfoToDiary() {
        FirebaseUtils().saveDayByFirstTime(day, requireContext())
            .addOnSuccessListener {
                Log.e("Saved", "Saved succesfully")
            }
            .addOnFailureListener { exception ->
                Log.e("No save", exception.message.toString())
            }
    }

    override fun onDeleteButtonClicked(food: Hint, meal: String) {
        val newList = this.day.meals[meal]?.foods?.toMutableList()?.apply { remove(food) }
        this.day.meals[meal]?.foods = newList?.toList()!!
        day.meals[meal]?.calculateTotals()
        sendInfoToDiary()
        initAllRecyclerViews()
        setCalories()
    }
}