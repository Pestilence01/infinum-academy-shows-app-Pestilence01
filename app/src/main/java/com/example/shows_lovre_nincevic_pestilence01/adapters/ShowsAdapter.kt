package com.example.shows_lovre_nincevic_pestilence01.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shows_lovre_nincevic_pestilence01.compoundviews.ShowCardView
import com.example.shows_lovre_nincevic_pestilence01.databinding.ShowItemBinding
import com.example.shows_lovre_nincevic_pestilence01.models.Show

class ShowsAdapter(
    private var context: Context,
    private var items: List<Show>,
    private val onItemClickCallback: (Show) -> Unit
) : RecyclerView.Adapter<ShowsAdapter.ShowViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val showCardView = ShowCardView(context)
        return ShowViewHolder(showCardView)
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.setOnClick(items[position])
        holder.getCardView().setShowData(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }


    inner class ShowViewHolder(private val showCardView: ShowCardView) :
        RecyclerView.ViewHolder(showCardView) {

        fun setOnClick(item: Show){
            showCardView.binding.showCardViewCard.setOnClickListener {
                onItemClickCallback(item)
            }
        }

        fun getCardView(): ShowCardView {
            return showCardView
        }
    }


}