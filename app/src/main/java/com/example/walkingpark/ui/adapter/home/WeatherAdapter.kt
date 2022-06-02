package com.example.walkingpark.ui.adapter.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.walkingpark.R
import com.example.walkingpark.constants.Common
import com.example.walkingpark.data.model.dto.simple_panel.SimplePanel5
import com.example.walkingpark.ui.viewmodels.getCalendarFromItem
import com.example.walkingpark.ui.viewmodels.returnAmPmAfterCheck
import java.util.*

const val CLEAR = 1
const val CLOUDY = 3
const val OVER_CAST = 4

const val NONE = 0
const val RAIN = 1
const val RAIN_SNOW = 2
const val SNOW = 3
const val SHOWER = 4

class WeatherAdapter() : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    var data = emptyList<SimplePanel5?>()
    private var prevDate: Calendar = Calendar.getInstance().apply {
        set(1990, 1, 1)
    }

    class WeatherViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val imageViewIcon: AppCompatImageView = itemView.findViewById(R.id.imageViewWeatherIcon)
        val textViewTime: AppCompatTextView = itemView.findViewById(R.id.textViewWeatherTime)
        val textViewTemperature: AppCompatTextView =
            itemView.findViewById(R.id.textViewWeatherTemperature)
        val textViewRainChance: AppCompatTextView =
            itemView.findViewById(R.id.textViewWeatherRainChance)
        val container: LinearLayoutCompat = itemView.findViewById(R.id.weatherItem)
        val seperator: LinearLayoutCompat = itemView.findViewById(R.id.weatherSeperator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        return WeatherViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.home_recyclerview_item_weather, parent, false)
        )
    }

    // TODO API 에서는 시작시간 (05시) 에 TMX TMN 으로 최고 최저기온 데이터 제공
    //  SKY(하늘) - 1(맑음), 3(구름많음), 4(흐림)
    //  PTY(강수타입) - 0(없음), 1(비), 2(비/눈), 3(눈), 4(소나기)
    //  18시 부터 오후로 취급.
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {

        val item = data[position]

        if (item != null) {
            holder.container.visibility = View.VISIBLE
            holder.seperator.visibility = View.GONE

            // 오전
            val statusSky = item.sky
            val rainType = item.rainType

            val weatherSun = R.drawable.ic_weather_sun
            val weatherCloud = R.drawable.ic_weather_cloud
            val weatherCloudPlus = R.drawable.ic_weather_fog
            val weatherMoon = R.drawable.ic_weather_moon
            val weatherRain = R.drawable.ic_weather_rain
            // 오후
            
            
            val dateTime = getCalendarFromItem(item)

            // TODO 배열로 담아서 메서드로 처리할것 !
            // 1. 날씨 체크
            if (statusSky != Common.NO_DATA) {
                when (statusSky.toInt()) {
                    // 맑음
                    CLEAR -> {
                        if (rainType != Common.NO_DATA) {
                            when (rainType.toInt()) {
                                NONE -> {}
                                RAIN -> {}
                                RAIN_SNOW -> {}
                                SNOW -> {}
                                SHOWER -> {}
                            }
                        } else {
                            holder.imageViewIcon.setImageResource(weatherSun)
                        }
                    }
                    CLOUDY -> {
                        if (rainType != Common.NO_DATA) {
                            when (rainType.toInt()) {
                                NONE -> {}
                                RAIN -> {}
                                RAIN_SNOW -> {}
                                SNOW -> {}
                                SHOWER -> {}
                            }
                        } else {
                            holder.imageViewIcon.setImageResource(weatherSun)
                        }
                    }
                    OVER_CAST -> {
                        if (rainType != Common.NO_DATA) {
                            when (rainType.toInt()) {
                                NONE -> {}
                                RAIN -> {}
                                RAIN_SNOW -> {}
                                SNOW -> {}
                                SHOWER -> {}
                            }
                        } else {
                            holder.imageViewIcon.setImageResource(weatherSun)
                        }
                    }
                    else -> {

                    }
                }
            }
            // NUll -> 정보 없음.
            else {

            }
            // 2. 시간 체크
            // HOUR_OF_DAY : 24시간
            // HOUR : 12시간
            if (5 >= dateTime.get(Calendar.HOUR_OF_DAY) || dateTime.get(Calendar.HOUR_OF_DAY) >= 20) {
                holder.imageViewIcon.setImageResource(weatherMoon)
            }

            holder.textViewTime.text =
                if (position == 0) " 지금 " else returnAmPmAfterCheck(
                    dateTime.get(Calendar.HOUR_OF_DAY),
                    dateTime.get(Calendar.HOUR)
                )
            holder.textViewTemperature.text = item.temperature + "°"
            holder.textViewRainChance.text = item.rainChance + " %"

            // prevDate.set(dateTime.year, dateTime.monthValue, dateTime.dayOfMonth)
        } else {
            holder.container.visibility = View.GONE
            holder.seperator.visibility = View.VISIBLE
        }
    }


    override fun getItemCount(): Int {
        return data.size
    }

    fun setAdapterData(data: List<SimplePanel5?>) {
        this.data = data
        notifyDataSetChanged()
    }
}

fun getTimeStamp() {

}