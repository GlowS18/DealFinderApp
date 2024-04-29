package com.example.dealfinder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserPostsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_posts)
        auth = Firebase.auth
        val user = auth.currentUser
        val db = Firebase.firestore

        val posts = mutableListOf<Post>()

        val addPostImageView: ImageView = findViewById(R.id.addPost_imageView)
        val profileRecyclerView: RecyclerView = findViewById(R.id.profile_recyclerView)
        val homePostsImageView: ImageView = findViewById(R.id.home_imageView)
        val username: TextView = findViewById(R.id.profileName)
        val adapter = PostAdapter(posts)
        profileRecyclerView.adapter = adapter
        profileRecyclerView.layoutManager = LinearLayoutManager(this)

//        val docRef = db.collection("users").document(user!!.uid)
//        docRef.get()
//            .addOnSuccessListener { document ->
//                if (document != null) {
//                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
//                    username.text = document["login"].toString()
//                } else {
//                    Log.d("TAG", "No such document")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.d("TAG", "get failed with ", exception)
//            }

//        db.collection("posts")
//            .whereEqualTo("uid", user!!.uid)
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    Log.d("Profile", "${document.id} => ${document.data}")
//                    posts.add(
//                        Post(document.id ,document.data["uid"].toString(), document.data["text"].toString(), document.data["desc"].toString())
//                    )
//                }
//                adapter.notifyDataSetChanged()
//
//            }
//            .addOnFailureListener { exception ->
//                Log.w("Profile", "Error getting documents: ", exception)
//            }
        val follows = mutableListOf<String>()

        db.collection("follows")
            .whereEqualTo("followUserID", user!!.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    follows.add(document.data["followPostId"].toString())
                }
                for (postId in follows) {
                }
                for (follow in follows) {
                    val docRef = db.collection("posts").document(follow)
                    docRef.get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                posts.add(
                                    Post(
                                        document.id,
                                        document.data!!["desc"].toString(),
                                        document.data!!["text"].toString(),
                                        document.data!!["uid"].toString()
                                    )
                                )
                                adapter.notifyDataSetChanged()
                                Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                            } else {
                                Log.d("TAG", "No such document")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("TAG", "get failed with ", exception)
                        }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("FavouritePosts", "Error getting documents: ", exception)
            }
        addPostImageView.setOnClickListener {
            val intent = Intent(this, AddPostActivity::class.java)
            startActivity(intent)
        }
        homePostsImageView.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}