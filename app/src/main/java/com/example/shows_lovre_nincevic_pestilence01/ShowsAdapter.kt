package com.example.shows_lovre_nincevic_pestilence01

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shows_lovre_nincevic_pestilence01.databinding.ShowItemBinding

class ShowsAdapter(
    private var context: Context,
    private var username: String,
    private var items: List<Show>,
) : RecyclerView.Adapter<ShowsAdapter.ShowViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding = ShowItemBinding.inflate(LayoutInflater.from(parent.context))
        return ShowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }


    inner class ShowViewHolder(private val binding: ShowItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Show){
            binding.showItemImage.setImageResource(item.imageResourceID)
            binding.showItemTitle.text = item.title
            binding.showItemDescription.text = item.description

            binding.showCardView.setOnClickListener {
                val intent: Intent = Intent(context, ShowDetailsActivity::class.java)
                intent.putExtra(Constants.SHOW_EXTRA_KEY, items[position])  // parcelable extra
                intent.putExtra(Constants.LOGIN_EMAIL_KEY, username.split("@")[0])    // Passes the "username", which, for simplicity sake, is the part of the email before the "@"
                context.startActivity(intent)
            }
        }
    }
}