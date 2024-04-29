package com.example.dealfinder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_acitivity)

        auth = Firebase.auth

        val currentUser = auth.currentUser
        val db = Firebase.firestore

        val emailEdit: EditText = findViewById(R.id.email_Register_editText)
        val RegisterLogin: EditText = findViewById(R.id.login_register_editText)
        val passwordEdit: EditText = findViewById(R.id.password_Register_editText)
        val RepeatRegisterPassword: EditText = findViewById(R.id.repeatPassword_Register_editText)
        val RegisterButton: Button = findViewById(R.id.register_button)
        val LogInText: TextView = findViewById(R.id.login_register_textView)

        LogInText.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        RegisterButton.setOnClickListener {
            if(passwordEdit.text.toString()!=RepeatRegisterPassword.text.toString())
            {
                Toast.makeText(
                    baseContext,
                    "Password invalid",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            else if(RegisterLogin.text.toString().isEmpty())
            {
                Toast.makeText(
                    baseContext,
                    "Login is empty",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            else {
                auth.createUserWithEmailAndPassword(
                    emailEdit.text.toString(),
                    passwordEdit.text.toString()
                )
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success")
                            val user = auth.currentUser
                            Log.w("TAG", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Hello:" + user!!.email.toString(),
                                Toast.LENGTH_SHORT,
                            ).show()

                            val newUser = hashMapOf(
                                "email" to user.email,
                                "id" to user.uid,
                                "password" to passwordEdit.text.toString(),
                                "login" to RegisterLogin.text.toString()
                            )

                            db.collection("users").document(user.uid)
                                .set(newUser)
                                .addOnSuccessListener {
                                    Log.d("TAG", "DocumentSnapshot successfully written!")
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Log.w(
                                        "TAG",
                                        "Error writing document",
                                        e
                                    )
                                }


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            }
        }
    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.d("Main", "Nie zalogowany")
            Toast.makeText(
                baseContext,
                "Niezalogowany",
                Toast.LENGTH_SHORT,
            ).show()
        }
        else {
            Log.d("Main", "Zalogowany")
            Toast.makeText(
                baseContext,
                "Zalogowany",
                Toast.LENGTH_SHORT,
            ).show()
            val intent = Intent(baseContext, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}