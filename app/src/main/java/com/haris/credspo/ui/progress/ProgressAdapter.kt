package com.haris.credspo.ui.progress

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.haris.credspo.R
import com.haris.credspo.models.BadgeData
import com.haris.credspo.models.UserBadgesResponse
import android.graphics.ColorMatrixColorFilter

import android.graphics.ColorMatrix




class ProgressAdapter(
    private val context: Context,
    private val badgesResponse: UserBadgesResponse,
    private val type: Int
) : RecyclerView.Adapter<ProgressAdapter.ProgressViewHolder>() {
    inner class ProgressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val count = itemView.findViewById<TextView>(R.id.progress_item_badge_amount_text)
        private val star = itemView.findViewById<ImageView>(R.id.progress_item_star_icon)
        private val icon = itemView.findViewById<ImageView>(R.id.progress_item_badge_icon)
        private val name = itemView.findViewById<TextView>(R.id.progress_item_badge_name)

        fun bind(item: BadgeData) {
            name.text = item.name

            if(type == 1) {
                icon.setImageResource(R.drawable.attributes_icon)
            } else if(type == 2) {
                icon.setImageResource(R.drawable.waypoints_icon)
            }

            if(item.count > 0) {
                star.visibility = View.VISIBLE
                count.text = "${item.count}"
                count.visibility = View.VISIBLE
                icon.alpha = 1f

                val matrix = ColorMatrix()
                icon.colorFilter = ColorMatrixColorFilter(matrix)
            } else {
                star.visibility = View.INVISIBLE
                count.visibility = View.INVISIBLE
                icon.alpha = 0.5f

                val matrix = ColorMatrix()
                matrix.setSaturation(0f)
                icon.colorFilter = ColorMatrixColorFilter(matrix)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressViewHolder {
        return ProgressViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_progress_badge, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProgressViewHolder, position: Int) {
        holder.bind(badgesResponse.data[position])
    }

    override fun getItemCount(): Int {
        return  badgesResponse.data.size
    }
}