package com.alejandroelv.myfitnesstrack.ui.main

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.alejandroelv.myfitnesstrack.R
import com.alejandroelv.myfitnesstrack.TimeUtils
import com.alejandroelv.myfitnesstrack.data.model.Day
import com.alejandroelv.myfitnesstrack.data.model.edamamModels.Hint
import com.alejandroelv.myfitnesstrack.databinding.ActivityFoodDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class FoodDetailsActivity : AppCompatActivity() {
    var food: Hint? = null
    var meal: String? = null
    lateinit var binding: ActivityFoodDetailsBinding
    private lateinit var day: Day

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityFoodDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchDayData(TimeUtils().getTodayDate())

        food = intent.extras?.getSerializable("food") as? Hint
        meal = intent.extras?.getString("meal")

        if(food != null){
            displayFoodDetails()
        }

        binding.tvSaveFood.setOnClickListener{
            sendInfoToDiary()
        }

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
        binding.tvCalories.text = food?.food?.nutrients?.enercKcal.toString()
        binding.tvCarbohydrates.text = "${food?.food?.nutrients?.chocdf.toString()}g"
        binding.tvFat.text = "${food?.food?.nutrients?.fat.toString()} g"
        binding.tvProtein.text = "${food?.food?.nutrients?.procnt.toString()} g"
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
                    day = Day()
                    sendInfoToDiary()
                } else {
                    // Document exists, access the first matching document
                    val documentSnapshot = querySnapshot.documents[0]
                    day = documentSnapshot.toObject(Day::class.java)!!
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure while querying the documents
                Log.e("TAG", "Error querying documents: ${exception.message}")
                // Perform error handling or show an error message to the user
            }
    }
    //TODO 2: BUSCAR SI LA COMIDA A ALMACENAR YA ESTA EN LA MEAL INDICADA Y SUSTITUIRLA O AÑADIRLA
    private fun addFoodToMeal(){
        val newList = day.meals[meal]?.foods.orEmpty().toMutableList()
        val foodPosition = searchFoodInMeal(food!!)

        if (foodPosition != -1) {
            newList[foodPosition] = food!!
        } else {
            newList.add(food!!)
        }

        day.meals[meal]?.foods = newList.toList()
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
                .whereEqualTo("date", TimeUtils().getTodayDate()).limit(1)
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