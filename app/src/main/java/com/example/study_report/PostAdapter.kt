package com.example.study_report

import BulletinPost
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.study_report.databinding.ItemPostBinding

class PostAdapter(
    private val posts: List<BulletinPost>,
    private val onItemClick: (BulletinPost) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
        holder.itemView.setOnClickListener {
            onItemClick(post)
        }
    }

    override fun getItemCount(): Int = posts.size

    class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: BulletinPost) {
            binding.textTitle.text = post.title
            binding.textContent.text = post.content
            binding.textAuthor.text = post.userNickname
            binding.textDate.text = post.timestamp

            if (post.hasAttachment) {
                binding.iconAttachment.visibility = View.VISIBLE
            } else {
                binding.iconAttachment.visibility = View.GONE
            }
        }
    }
}
