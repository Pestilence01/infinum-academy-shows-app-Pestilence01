package com.example.shows_lovre_nincevic_pestilence01

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shows_lovre_nincevic_pestilence01.databinding.ReviewItemBinding

class ReviewsAdapter(
    private var items: MutableList<Review>,
) : RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsAdapter.ReviewViewHolder {
        val binding = ReviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewsAdapter.ReviewViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ReviewViewHolder(private val binding: ReviewItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Review){
            binding.profileCircularImage.setImageResource(item.profileImageResourceID)
            binding.username.text = item.username
            binding.comment.text = item.comment
            binding.score.text = item.rating.toString()
        }
    }
}