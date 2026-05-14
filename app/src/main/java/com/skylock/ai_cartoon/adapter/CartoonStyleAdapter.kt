package com.skylock.ai_cartoon.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skylock.ai_cartoon.R
import com.skylock.ai_cartoon.model.CartoonStyle


class CartoonStyleAdapter(
    private val styles: List<CartoonStyle>,
    private val onStyleClick: (CartoonStyle) -> Unit
) : RecyclerView.Adapter<CartoonStyleAdapter.StyleViewHolder>() {

    inner class StyleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon: ImageView = itemView.findViewById(R.id.iv_style_icon)
        val tvName: TextView = itemView.findViewById(R.id.tv_style_name)

        fun bind(style: CartoonStyle) {
            tvName.text = style.name

            Glide.with(itemView.context)
                .load(style.iconUrl)
                .placeholder(R.drawable.place_holder) // add a placeholder drawable
                .into(ivIcon)

            itemView.setOnClickListener {
                onStyleClick(style)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StyleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cartoon_style, parent, false)
        return StyleViewHolder(view)
    }

    override fun onBindViewHolder(holder: StyleViewHolder, position: Int) {
        holder.bind(styles[position])
    }

    override fun getItemCount(): Int = styles.size
}