package com.example.walkingpark.presentation.adapter.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.walkingpark.R
import com.example.walkingpark.data.model.dto.WeatherResponse

class TabAdapterWeather (): RecyclerView.Adapter<TabAdapterWeather.WeatherViewHolder>() {

    var data = emptyMap<String, Map<String, Map<String, String>>>()

    class WeatherViewHolder constructor(itemView: View, val seperator: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewIcon:AppCompatImageView = itemView.findViewById(R.id.imageViewIcon)
        val textViewTime:AppCompatTextView = itemView.findViewById(R.id.textViewTime)
        val textViewValue:AppCompatTextView = itemView.findViewById(R.id.textViewValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        return WeatherViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.home_recyclerview_item_weather, parent, false),
            LayoutInflater.from(parent.context).inflate(R.layout.home_recyclerview_item_seperator, parent, false)
        )
    }

    // TODO API 에서는 시작시간 (5시) 에 TMX TMN 으로 최고 최저기온 데이터 제공
    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        //val item = holder.absoluteAdapterPosition = 1

    }

    override fun getItemCount(): Int {
        var count = 0
        data.values.forEach {
            count += it.size
        }
        return count
    }
}


fun getTimeStamp(){

}