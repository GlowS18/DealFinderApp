package com.example.dealfinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth

        val currentUser = auth.currentUser
        val db = Firebase.firestore

        val Email: EditText = findViewById(R.id.email_login_editText)
        val Password: EditText = findViewById(R.id.password_login_editText)
        val LoginButton: Button = findViewById(R.id.login_button)
        val RegisterText: TextView = findViewById(R.id.register_login_textView)


        RegisterText.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        LoginButton.setOnClickListener {
            if(Password.text.toString().isEmpty())
            {
                Toast.makeText(
                    baseContext,
                    "Password empty",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            else if(Email.text.toString().isEmpty())
            {
                Toast.makeText(
                    baseContext,
                    "Email empty",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            else {
                auth.signInWithEmailAndPassword(Email.text.toString(), Password.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success")
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.exception)
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
}