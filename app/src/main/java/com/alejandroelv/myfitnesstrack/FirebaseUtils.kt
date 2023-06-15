package com.alejandroelv.myfitnesstrack

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.alejandroelv.myfitnesstrack.data.model.Day
import com.alejandroelv.myfitnesstrack.data.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlin.math.roundToInt

class FirebaseUtils {

    fun getDayReference(date: String): Task<QuerySnapshot> {
        val uid = FirebaseUtils().getUserId()

        val db = FirebaseFirestore.getInstance()
        val userDocumentRef = db.collection("users").document(uid)
        val dayDocumentRef = userDocumentRef.collection("days")

        return dayDocumentRef.whereEqualTo("date", date).get()
    }

    fun saveUserData(user: User, context: Context) : Task<Void>{
        val userId: String = FirebaseUtils().getUserId()
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val userDocumentRef = db.collection("users").document(userId)

        val userMap = FirebaseUtils().createUserDataMap(user)

        val goalCalories: Double = userMap["goalCalories"] as Double
        val roundedGoalCalories = goalCalories.roundToInt()

        val sharedPref: SharedPreferences? = context?.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor? = sharedPref?.edit()
        editor?.putInt("goalCalories", roundedGoalCalories)
        editor?.apply()

        return userDocumentRef.set(userMap)
    }

    private fun createUserDataMap(user: User) : HashMap<String, Any>{
        val userMap = hashMapOf<String, Any>()
        userMap["id"] = FirebaseUtils().getUserId()
        userMap["gender"] = user.gender
        userMap["age"] = user.age
        userMap["weight"] = user.weight
        userMap["height"] = user.height
        userMap["goal"] = user.goal
        userMap["goalByWeek"] = user.goalByWeek
        userMap["goalCalories"] = 88.362 + (13.397 * user.weight) + (4.799 * user.height) - (5.677 * user.age)

        return userMap
    }

    fun getUserId() : String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun saveDayByFirstTime(day: Day, context: Context): Task<Void> {
        val uid = FirebaseUtils().getUserId()

        val db = FirebaseFirestore.getInstance()
        val userDocumentRef = db.collection("users").document(uid)

        val dayDocumentRef = if(day.id == null){
            day.goalCalories = (context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                ?.getInt("goalCalories", 0)?.toLong() ?: 0L).toDouble()

            userDocumentRef.collection("days").document()
        }else{
            userDocumentRef.collection("days").document(day.id!!)
        }

        return dayDocumentRef.set(day)
    }

    fun getDayId(date: String, callback: (String?) -> Unit) {
        FirebaseUtils().getDayReference(date).addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents[0]
                callback.invoke(document.id)
            } else {
                callback.invoke(null)
            }
        }
    }

    fun getUserData(user: User){
        val uid = FirebaseUtils().getUserId()

        val db = FirebaseFirestore.getInstance()
        val userDocumentRef = db.collection("users").document(uid)

        userDocumentRef
            .get()
            .addOnSuccessListener { documentSnapshot ->
                user.weight = (documentSnapshot.get("weight") as Long).toInt()
                user.height = (documentSnapshot.get("height") as Long).toInt()
                user.age = (documentSnapshot.get("age") as Long).toInt()
                user.gender = (documentSnapshot.get("gender") as Long).toInt()
                user.goal = (documentSnapshot.get("goal") as Long).toInt()
                user.goalByWeek = (documentSnapshot.get("goalByWeek") as Double)
            }
            .addOnFailureListener{ exception ->
                Log.e("TAG", "Error querying documents: ${exception.message}")
            }
    }
}