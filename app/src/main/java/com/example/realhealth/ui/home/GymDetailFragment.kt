package com.example.realhealth.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import android.widget.RatingBar
import com.bumptech.glide.Glide
import androidx.fragment.app.Fragment
import com.example.realhealth.R
import com.example.realhealth.model.Gym

class GymDetailFragment : Fragment() {
    private lateinit var gym: Gym

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments?.let { GymDetailFragmentArgs.fromBundle(it) }
        gym = args?.gym ?: error("Gym argument is missing")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.gym_detail, container, false)

        view.findViewById<TextView>(R.id.gym_name).text = gym.name
        view.findViewById<TextView>(R.id.gym_address).text = gym.address
        view.findViewById<TextView>(R.id.gym_phone).text = gym.phoneNumber
        view.findViewById<TextView>(R.id.gym_hours).text = gym.openingHours

        val ratingBar = view.findViewById<RatingBar>(R.id.gym_rating)
        val ratingText = view.findViewById<TextView>(R.id.gym_rating_text)
        val ratingNaText = view.findViewById<TextView>(R.id.gym_rating_na_text)

        val rating = gym.rating

        if (rating != null && rating >= 0) {
            ratingBar.visibility = View.VISIBLE
            ratingText.visibility = View.VISIBLE
            ratingNaText.visibility = View.GONE

            ratingBar.rating = rating.toFloat()
            ratingText.text = String.format("%.1f", rating)
        } else {
            ratingBar.visibility = View.GONE
            ratingText.visibility = View.GONE
            ratingNaText.visibility = View.VISIBLE
        }

        val gymImageView = view.findViewById<ImageView>(R.id.gym_image)

        gym.photoReference?.let { photoUrl ->
            Glide.with(requireContext())
                .load(photoUrl)
                .placeholder(R.drawable.ic_home_black_24dp) // 로딩 중 보여줄 이미지
                .into(gymImageView)
        }

        return view
    }
}