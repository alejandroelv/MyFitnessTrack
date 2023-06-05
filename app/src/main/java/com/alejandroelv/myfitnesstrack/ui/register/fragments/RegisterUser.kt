package com.alejandroelv.myfitnesstrack.ui.register.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.alejandroelv.myfitnesstrack.R
import com.alejandroelv.myfitnesstrack.data.model.User
import com.alejandroelv.myfitnesstrack.databinding.FragmentRegisterUserBinding
import com.alejandroelv.myfitnesstrack.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterUser : Fragment() {
    private lateinit var user: User
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentRegisterUserBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_register_user, container, false)
        binding = FragmentRegisterUserBinding.bind(view)

        user = arguments?.getParcelable<User>("user")!!
        this.auth = FirebaseAuth.getInstance()

        binding.buttonPrevious.setOnClickListener{passToPreviousFragment()}
        binding.buttonNext.setOnClickListener{checkFields()}
        return view
    }

    private fun passToPreviousFragment(){
        parentFragmentManager.popBackStack()
    }

    private fun checkFields(){
        val email: String = binding.etEmail.text.toString().trim()
        val password: String = binding.etPassword.text.toString().trim()

        when{
            email.isBlank() || password.isBlank() -> {
                Toast.makeText(this.context, R.string.empty_fields_register, Toast.LENGTH_SHORT).show()
            }
            password.length < 6 -> {
                Toast.makeText(this.context, R.string.empty_fields_register, Toast.LENGTH_SHORT).show()
            }
            else -> {
                register(email, password)
            }
        }
    }

    private fun register(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val userId: String = auth.currentUser!!.uid
                    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
                    val userDocumentRef = db.collection("users").document(userId)

                    val userMap = hashMapOf<String, Any>()
                    userMap["id"] = userId
                    userMap["gender"] = user.gender
                    userMap["age"] = user.age
                    userMap["weight"] = user.weight
                    userMap["height"] = user.height
                    userMap["goal"] = user.goal
                    userMap["goalByWeek"] = user.goalByWeek
                    userMap["goalCalories"] = 88.362 + (13.397 * user.weight) + (4.799 * user.height) - (5.677 * user.age)

                    userDocumentRef.set(userMap)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                val llamarMain = Intent(this.context, MainActivity::class.java)
                                startActivity(llamarMain)
                                requireActivity().finish()
                            }
                        }
                }
            }
    }
}