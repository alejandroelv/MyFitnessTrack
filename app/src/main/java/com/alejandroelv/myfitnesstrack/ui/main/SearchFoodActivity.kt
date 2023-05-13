package com.alejandroelv.myfitnesstrack.ui.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alejandroelv.myfitnesstrack.R
import com.alejandroelv.myfitnesstrack.api.EdamamService
import com.alejandroelv.myfitnesstrack.data.model.edamamModels.Foods
import com.alejandroelv.myfitnesstrack.data.model.edamamModels.Hint
import com.alejandroelv.myfitnesstrack.databinding.ActivitySearchFoodBinding
import com.alejandroelv.myfitnesstrack.ui.adapters.FoodAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchFoodActivity : AppCompatActivity() {
    private val api_key: String = "b52ec4b479a628404ba2dc251d40223a"
    private val app_key: String = "40e5cc81"
    private lateinit var binding: ActivitySearchFoodBinding
    private var foodList: ArrayList<Hint> = ArrayList()
    private lateinit var service: EdamamService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFoodService()
        rechargeRecentFoods()

        binding.etSearchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                s?.let { searchText ->
                    if (searchText.isEmpty()) {
                        rechargeRecentFoods()
                    }
                }
            }
        })

        binding.etSearchBar.setOnEditorActionListener { _, actionId, event ->
            if (isActionDoneOrEnter(actionId, event)) {
                searchFoods(binding.etSearchBar.text.toString())
                true
            } else {
                false
            }
        }

        //TODO 2: Inicializar el reciclerView de los resultados
        binding.rvFoundFoods.adapter = FoodAdapter(foodList, object : FoodAdapter.OnItemClickListener {
            override fun onItemClick(result: Hint) { callFoodDetails(result) }
        })

        binding.rvFoundFoods.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)

    }

    private fun initFoodService() {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.edamam.com/api/food-database/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(EdamamService::class.java)
    }

    private fun isActionDoneOrEnter(actionId: Int, event: KeyEvent?): Boolean {
        return actionId == EditorInfo.IME_ACTION_DONE ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
    }

    private fun rechargeRecentFoods(){
        binding.tvHint.text = getString(R.string.recent_foods)

        //TODO: Llamar a firebase para que me de las comidas recientes
        val database = FirebaseDatabase.getInstance()
        val recentsFoodsRef = database.getReference("recentFoods")

        recentsFoodsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Data has been fetched successfully
                // Iterate through the dataSnapshot to access the data
                for (childSnapshot in dataSnapshot.children) {
                    val recentFood = childSnapshot.getValue(Hint::class.java)
                    if (recentFood != null) {
                        foodList.add(recentFood)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Error occurred while fetching the data
                // Handle the error appropriately
            }
        })
    }

    private fun searchFoods(foodName: String){
        val callFoods: Call<Foods> = service.getFoods(app_key, api_key, foodName)
        val adapter: FoodAdapter = binding.rvFoundFoods.adapter as FoodAdapter

        callFoods.enqueue(object : Callback<Foods> {
            override fun onResponse(call: Call<Foods>, response: Response<Foods>) {
                foodList = response.body()?.hints as ArrayList<Hint>
                adapter.setDatos(foodList)
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<Foods>, t: Throwable) {
                Toast.makeText(this@SearchFoodActivity, "No foods found", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun callFoodDetails(food: Hint){

    }
}