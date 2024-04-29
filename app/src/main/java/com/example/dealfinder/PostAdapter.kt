package com.example.dealfinder

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PostAdapter(val posts: MutableList<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    val db = Firebase.firestore
    var auth: FirebaseAuth = Firebase.auth
    val user = auth.currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post_desc: TextView = holder.itemView.findViewById(R.id.post_desc_textView)
        val post_text: TextView = holder.itemView.findViewById(R.id.post_text_textView)
        val deleteImageView: ImageView = holder.itemView.findViewById(R.id.delete_imageView)
        val followImage: ImageView = holder.itemView.findViewById(R.id.follow_imageView)

        post_desc.text = posts[position].desc
        post_text.text = posts[position].text

        val docRef = db.collection("follows")
            .document(user!!.uid + ":" + posts[position].id)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    followImage.setImageResource(R.drawable.baseline_star_24)
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }

        followImage.setOnClickListener {
            db.collection("follows").document(user!!.uid + ":" + posts[position].id)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        db.collection("follows").document(user!!.uid + ":" + posts[position].id)
                            .delete()
                            .addOnSuccessListener {
                                followImage.setImageResource(R.drawable.baseline_star_outline_24)
                            }
                            .addOnFailureListener { e -> Log.w("TAG", "Error deleting document", e) }
                    } else {
                        val newFollow = hashMapOf(
                            "followUserID" to user!!.uid,
                            "followPostId" to posts[position].id
                        )
                        db.collection("follows").document(user!!.uid + ":" + posts[position].id)
                            .set(newFollow)
                            .addOnSuccessListener {
                                followImage.setImageResource(R.drawable.baseline_star_24)
                            }
                            .addOnFailureListener { e -> Log.w("Follow", "Error writing document", e) }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "get failed with ", exception)
                }
        }


        //deleting posts
        if(user!!.uid != posts[position].uid)
        {
            deleteImageView.visibility = View.INVISIBLE
        }
        deleteImageView.setOnClickListener {
            if(user!!.uid == posts[position].uid)
            {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Delete")
                    .setMessage("Are you shure you want to delete this post?")
                    .setPositiveButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }.setNegativeButton("Delete") { dialog, which ->
                        db.collection("posts").document(posts[position].id)
                            .delete()
                            .addOnSuccessListener {
                                Log.d("TAG", "DocumentSnapshot successfully deleted!")
                                posts.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, posts.size)
                            }
                            .addOnFailureListener { e -> Log.w("TAG", "Error deleting document", e) }
                    }.show()


            }

        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}