package com.alejandroelv.myfitnesstrack

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.alejandroelv.myfitnesstrack.databinding.ActivityLoginBinding
import com.alejandroelv.myfitnesstrack.ui.register.RegisterActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {
    private val firebaseUser: FirebaseUser? = Firebase.auth.currentUser
    private lateinit var binding: ActivityLoginBinding

    override fun onStart() {
        super.onStart()

        if(this.firebaseUser != null){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            this.tryToLogin()
        }

        binding.registerButton.setOnClickListener {
            //TODO 1: Llamar a RegisterActivity
            val llamarRegister = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(llamarRegister)
        }
    }

    private fun tryToLogin(){
        val username: String = binding.username.text.toString()
        val password: String = binding.password.text.toString()

        if(username.isBlank() || password.isBlank()){
            this.showLoginFailed(R.string.empty_fields_login)
            return
        }

        val progressBar = binding.loading
        progressBar.visibility = View.VISIBLE

        Firebase.auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener(this@LoginActivity){task ->
                if(task.isSuccessful){
                    val llamarMain = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(llamarMain)
                    finish()
                }else{
                    this.showLoginFailed(R.string.login_failed)
                }
            }

        progressBar.visibility = View.GONE
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}