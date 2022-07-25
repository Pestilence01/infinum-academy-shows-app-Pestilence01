package com.example.shows_lovre_nincevic_pestilence01.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shows_lovre_nincevic_pestilence01.R
import com.example.shows_lovre_nincevic_pestilence01.databinding.ReviewItemBinding
import com.example.shows_lovre_nincevic_pestilence01.models.Review

class ReviewsAdapter(
    private var items: ArrayList<Review>,
    private var context: Context
) : RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ReviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ReviewViewHolder(private val binding: ReviewItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Review){
            if(item.profileImageBitmap == null) {
                Glide.with(context).load(R.drawable.ic_profile_placeholder).into(binding.profileCircularImage)
            } else {
                Glide.with(context).load(item.profileImageBitmap).into(binding.profileCircularImage)
            }
            binding.username.text = item.username
            binding.comment.text = item.comment
            binding.score.text = item.rating.toString()
        }
    }
}