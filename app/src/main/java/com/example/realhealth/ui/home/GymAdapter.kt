package com.example.realhealth.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.realhealth.R
import com.example.realhealth.model.Gym

class GymAdapter(
    private val gyms: List<Gym>,
    private val onClick: (Gym) -> Unit
) : RecyclerView.Adapter<GymAdapter.GymViewHolder>() {

    inner class GymViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.gym_image)
        private val nameView: TextView = itemView.findViewById(R.id.gym_name)
        private val distanceView: TextView = itemView.findViewById(R.id.gym_distance)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.gym_rating)

        fun bind(gym: Gym) {
            nameView.text = gym.name
            distanceView.text = gym.distance ?: "거리 정보 없음"

            if (gym.rating != null && gym.rating >= 0) {
                ratingBar.visibility = View.VISIBLE
                ratingBar.rating = gym.rating.toFloat()
            } else {
                ratingBar.visibility = View.GONE
            }

            Glide.with(itemView.context)
                .load(gym.photoReference)
                .placeholder(R.drawable.ic_home_black_24dp) // drawable에 있는 기본 이미지
                .into(imageView)

            itemView.setOnClickListener {
                onClick(gym)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GymViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.gym_list, parent, false)
        return GymViewHolder(view)
    }

    override fun onBindViewHolder(holder: GymViewHolder, position: Int) {
        holder.bind(gyms[position])
    }

    override fun getItemCount(): Int = gyms.size
}