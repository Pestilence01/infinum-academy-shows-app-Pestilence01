package com.example.shows_lovre_nincevic_pestilence01.compoundviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.example.shows_lovre_nincevic_pestilence01.R
import com.example.shows_lovre_nincevic_pestilence01.databinding.ShowCardViewBinding
import com.example.shows_lovre_nincevic_pestilence01.models.Show

class ShowCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var binding: ShowCardViewBinding

    init {
        binding = ShowCardViewBinding.inflate(LayoutInflater.from(context), this)

        clipToPadding = false

        setPadding(context.resources.getDimensionPixelOffset(R.dimen.spacing_1x))

    }

    fun setShowData(show: Show){
        Glide.with(context).load(show.image_url).into(binding.showItemImage)
        binding.showItemTitle.text = show.title
        binding.showItemDescription.text = show.description
    }
}