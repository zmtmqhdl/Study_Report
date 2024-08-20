package com.example.study_report

import BulletinPost
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.study_report.databinding.ActivityBulletinBoardBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BulletinBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBulletinBoardBinding
    private lateinit var adapter: PostAdapter
    private val posts: MutableList<BulletinPost> = mutableListOf()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("posts")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBulletinBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = PostAdapter(posts)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        loadPosts()

        binding.fabAddPost.setOnClickListener {
            val intent = Intent(this, PostActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadPosts() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                posts.clear()
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(BulletinPost::class.java)
                    post?.let { posts.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors here
            }
        })
    }
}

