package com.example.dealfinder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddPostActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val addButton: Button = findViewById(R.id.addPost_button)
        val addOfferName: EditText = findViewById(R.id.offerName_editText)
        val addOfferDescription: EditText = findViewById(R.id.offerDescription_editText)
        val profile: ImageView = findViewById(R.id.userPosts_imageView)
        val homePostsImageView: ImageView = findViewById(R.id.home_imageView)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        homePostsImageView.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        profile.setOnClickListener {
            val intent = Intent(this, UserPostsActivity::class.java)
            startActivity(intent)
        }

        addButton.setOnClickListener {

            val post = hashMapOf(
                "uid" to user!!.uid,
                "text" to addOfferName.text.toString(),
                "desc" to addOfferDescription.text.toString()
            )

            db.collection("posts")
                .add(post)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot written with ID: ${documentReference.id}")
                    Toast.makeText(this, "Post added successfully!", Toast.LENGTH_SHORT).show()
                    addOfferName.text.clear()
                    addOfferDescription.text.clear()
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error adding document", e)
                    Toast.makeText(this, "Failed to add post!", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
