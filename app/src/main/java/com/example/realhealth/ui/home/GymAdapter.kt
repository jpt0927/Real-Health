package com.example.realhealth.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.realhealth.R
import com.example.realhealth.model.Gym
import com.example.realhealth.util.FavoriteManager

class GymAdapter(
    private val gyms: List<Gym>,
    private val onClick: (Gym) -> Unit,
) : RecyclerView.Adapter<GymAdapter.GymViewHolder>() {

    inner class GymViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.btn_favorite)
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

            val context = itemView.context
            // 즐겨찾기 상태 확인
            val isFav = FavoriteManager.isFavorite(context, gym.placeId)
            // 이미지 변경 (빈 별/채워진 별)
            favoriteButton.setImageResource(
                if (isFav) R.drawable.star_fill else R.drawable.star
            )

            // 클릭 리스너로 토글 기능 구현
            favoriteButton.setOnClickListener {
                val newState = !FavoriteManager.isFavorite(context, gym.placeId)
                FavoriteManager.setFavorite(context, gym.placeId, newState)
                notifyItemChanged(adapterPosition) // UI 갱신
            }

            Glide.with(context)
                .load(gym.photoReference)
                .placeholder(R.drawable.muscle)
                .into(imageView)

            itemView.setOnClickListener {
                onClick(gym)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GymViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gym_list, parent, false)
        return GymViewHolder(view)
    }

    override fun onBindViewHolder(holder: GymViewHolder, position: Int) {
        holder.bind(gyms[position])
    }

    override fun getItemCount(): Int = gyms.size
}