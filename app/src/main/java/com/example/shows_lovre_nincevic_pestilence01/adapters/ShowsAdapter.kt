package com.example.shows_lovre_nincevic_pestilence01.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.shows_lovre_nincevic_pestilence01.R
import com.example.shows_lovre_nincevic_pestilence01.databinding.ShowItemBinding
import com.example.shows_lovre_nincevic_pestilence01.models.Show
import com.example.shows_lovre_nincevic_pestilence01.utils.Constants

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

            binding.showCardView.setOnClickListener { view ->
                view.findNavController().navigate(R.id.action_showsFragment_to_showDetailsFragment, bundleOf(Constants.LOGIN_EMAIL_KEY to username, Constants.SHOW_EXTRA_KEY to items[position]))
            }
        }
    }
}