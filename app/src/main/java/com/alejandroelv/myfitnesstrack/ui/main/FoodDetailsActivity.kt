package com.alejandroelv.myfitnesstrack.ui.main

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.alejandroelv.myfitnesstrack.data.model.Day
import com.alejandroelv.myfitnesstrack.data.model.edamamModels.Hint
import com.alejandroelv.myfitnesstrack.databinding.ActivityFoodDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FoodDetailsActivity : AppCompatActivity() {
    var food: Hint? = null
    var meal: String? = null
    var date: String? = null
    lateinit var binding: ActivityFoodDetailsBinding
    private lateinit var day: Day

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityFoodDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        food = intent.extras?.getSerializable("food") as? Hint
        meal = intent.extras?.getString("meal")
        date = intent.extras?.getString("date")

        date?.let { fetchDayData(it) }

        if(food != null){
            displayFoodDetails()
        }

        binding.tvSaveFood.setOnClickListener{ sendInfoToDiary() }

        binding.tvNumberOfServings.text = Editable.Factory.getInstance().newEditable(food?.food?.selectedWeight.toString())

        binding.tvNumberOfServings.setOnEditorActionListener { _, actionId, event ->
            if (isActionDoneOrEnter(actionId, event)) {
                recalculateFoodNutrients()
                true
            } else {
                false
            }
        }
    }

    private fun isActionDoneOrEnter(actionId: Int, event: KeyEvent?): Boolean {
        return actionId == EditorInfo.IME_ACTION_DONE ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
    }

    private fun recalculateFoodNutrients(){
        val nutrients = food?.food?.nutrients
        val servingSize = Integer.parseInt(binding.tvNumberOfServings.text.toString())

        food?.food?.nutrients?.enercKcal = (servingSize * nutrients?.enercKcal!!) / food?.food?.selectedWeight!!
        food?.food?.nutrients?.procnt = (servingSize * nutrients?.procnt!!) / food?.food?.selectedWeight!!
        food?.food?.nutrients?.chocdf = (servingSize * nutrients?.chocdf!!) / food?.food?.selectedWeight!!
        food?.food?.nutrients?.fat = (servingSize * nutrients?.fat!!) / food?.food?.selectedWeight!!
        food?.food?.selectedWeight = servingSize

        displayFoodDetails()
    }

    private fun displayFoodDetails(){
        binding.tvMeal.text = meal

        binding.tvFoodName.text = food?.food?.label
        binding.tvCalories.text = String.format("%.2f", food?.food?.nutrients?.enercKcal)
        binding.tvCarbohydrates.text = "${String.format("%.2f", food?.food?.nutrients?.chocdf)}g"
        binding.tvFat.text = "${String.format("%.2f",food?.food?.nutrients?.fat)} g"
        binding.tvProtein.text = "${String.format("%.2f",food?.food?.nutrients?.procnt)} g"
    }

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
                    day = Day(date)
                    sendInfoToDiary()
                } else {
                    // Document exists, access the first matching document
                    val documentSnapshot = querySnapshot.documents[0]
                    day = documentSnapshot.toObject(Day::class.java)!!
                }
            }
            .addOnFailureListener { exception ->
                Log.e("TAG", "Error querying documents: ${exception.message}")
            }
    }

    private fun addFoodToMeal(){
        val newList = day.meals[meal]?.foods.orEmpty().toMutableList()
        val foodPosition = searchFoodInMeal(food!!)

        if (foodPosition != -1) {
            newList[foodPosition] = food!!
            Log.e("Updateo", "Comidita")
        } else {
            newList.add(food!!)
            Log.e("Añado", "Comidita")
        }

        day.meals[meal]?.foods = newList.toList()
        day.meals[meal]?.calculateTotals()
    }

    private fun searchFoodInMeal(foodToSearch : Hint) : Int{
        var foodPosition = -1

        for((index, food) in day.meals[meal]?.foods!!.withIndex()){
            if (food.food?.foodId == foodToSearch.food?.foodId){
                foodPosition = index
                break
            }
        }

        return foodPosition
    }

    private fun sendInfoToDiary() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        addFoodToMeal()

        if (uid != null) {
            val db = FirebaseFirestore.getInstance()
            val userDocumentRef = db.collection("users").document(uid)
            val dayDocumentRef = userDocumentRef.collection("days")

            dayDocumentRef
                .whereEqualTo("date", date).limit(1)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]
                        val ref = userDocumentRef.collection("days").document(document.id)
                        day.id = document.id

                        ref.set(day).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.e("Success", "Succesful update")
                                backToDiary()
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

    private fun backToDiary(){
        val intent = Intent(this@FoodDetailsActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}