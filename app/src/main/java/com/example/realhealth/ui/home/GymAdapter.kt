package com.example.realhealth.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.realhealth.model.Gym

class GymAdapter(
    private val gyms: List<Gym>,
    private val onClick: (Gym) -> Unit
) : RecyclerView.Adapter<GymAdapter.GymViewHolder>() {

    inner class GymViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameView: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(gym: Gym) {
            nameView.text = gym.name
            itemView.setOnClickListener {
                onClick(gym)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GymViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return GymViewHolder(view)
    }

    override fun onBindViewHolder(holder: GymViewHolder, position: Int) {
        holder.bind(gyms[position])
    }

    override fun getItemCount(): Int = gyms.size
}