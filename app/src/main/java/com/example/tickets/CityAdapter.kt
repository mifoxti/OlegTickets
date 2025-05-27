package com.example.tickets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CityAdapter(private var items: List<City>) :
    RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cityName: TextView = itemView.findViewById(R.id.textCityName)
        val countryName: TextView = itemView.findViewById(R.id.textCountry)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false)
        return CityViewHolder(view)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val city = items[position]
        holder.cityName.text = city.name ?: "—"
        holder.countryName.text = city.country ?: "—"
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<City>) {
        items = newItems
        notifyDataSetChanged()
    }
}

