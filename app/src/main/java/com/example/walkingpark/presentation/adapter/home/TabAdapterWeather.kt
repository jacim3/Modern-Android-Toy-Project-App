package com.example.walkingpark.presentation.adapter.home

import android.icu.util.LocaleData
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.walkingpark.R
import com.example.walkingpark.constants.Common
import com.example.walkingpark.data.model.dto.WeatherDTO
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class TabAdapterWeather() : RecyclerView.Adapter<TabAdapterWeather.WeatherViewHolder>() {

    var data = emptyList<WeatherDTO>()

    class WeatherViewHolder constructor(itemView: View, val seperator: View) :
        RecyclerView.ViewHolder(itemView) {
        val imageViewIcon: AppCompatImageView = itemView.findViewById(R.id.imageViewIcon)
        val textViewTime: AppCompatTextView = itemView.findViewById(R.id.textViewTime)
        val textViewTemperature: AppCompatTextView = itemView.findViewById(R.id.textViewTemperature)
        val textViewRainChance: AppCompatTextView = itemView.findViewById(R.id.textViewRainChance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        return WeatherViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.home_recyclerview_item_weather, parent, false),
            LayoutInflater.from(parent.context)
                .inflate(R.layout.home_recyclerview_item_seperator, parent, false)
        )
    }

    // TODO API 에서는 시작시간 (05시) 에 TMX TMN 으로 최고 최저기온 데이터 제공
    //  SKY - 1(맑음), 3(구름많음), 4(흐림)
    //  PTY - 0(없음), 1(비), 2(비/눈), 3(눈), 4(소나기)
    //  18시 부터 오후로 취급.
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        //val item = holder.absoluteAdapterPosition = 1
        val item = data[position]
        val status = item.sky

        val weatherSun = R.drawable.ic_weather_sun
        val weatherCloud = R.drawable.ic_weather_cloud
        val weatherCloudPlus = R.drawable.ic_weather_fog
        val weatherMoon = R.drawable.ic_weather_moon
        val weatherRain = R.drawable.ic_weather_rain

        val dateTime = getLocalDateTime(item.date+item.time)

        // 1. 날씨 체크
        if(status != Common.NO_DATA) {
            when(status.toInt()) {
                1 -> {
                    holder.imageViewIcon.setImageResource(weatherSun)
                }
                3 -> {
                    holder.imageViewIcon.setImageResource(weatherCloud)
                }
                4 -> {
                    holder.imageViewIcon.setImageResource(weatherCloudPlus)
                }
                else -> {
                    holder.imageViewIcon.setImageResource(weatherSun)
                }
            }
        }

        // 2. 시간 체크
        if ( 5 >= dateTime.hour || dateTime.hour >= 20) {
            holder.imageViewIcon.setImageResource(weatherMoon)
        }

        holder.textViewTime.text = dateTime.hour.toString()+" 시"
        holder.textViewTemperature.text = item.temperature+" °"
        holder.textViewRainChance.text = item.rainChance+" %"
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setAdapterData(data: List<WeatherDTO>) {
        this.data = data
        notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getLocalDateTime(dateTime: String) = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyyMMddHHmm"))
}


fun getTimeStamp() {

}