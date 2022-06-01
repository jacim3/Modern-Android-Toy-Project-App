package com.example.walkingpark.presentation.adapter.home

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.walkingpark.R
import com.example.walkingpark.constants.Common
import com.example.walkingpark.data.model.dto.WeatherDTO
import java.util.*

class WeatherAdapter() : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    var data = emptyList<WeatherDTO?>()
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
    //  SKY - 1(맑음), 3(구름많음), 4(흐림)
    //  PTY - 0(없음), 1(비), 2(비/눈), 3(눈), 4(소나기)
    //  18시 부터 오후로 취급.
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {

        val item = data[position]

        if (item != null) {
            holder.container.visibility = View.VISIBLE
            holder.seperator.visibility = View.GONE

            val status = item.sky
            val weatherSun = R.drawable.ic_weather_sun
            val weatherCloud = R.drawable.ic_weather_cloud
            val weatherCloudPlus = R.drawable.ic_weather_fog
            val weatherMoon = R.drawable.ic_weather_moon
            val weatherRain = R.drawable.ic_weather_rain

            val dateTime = getCalendarFromItem(item)

            // 1. 날씨 체크
            if (status != Common.NO_DATA) {
                when (status.toInt()) {
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
            // HOUR_OF_DAY : 24시간
            // HOUR : 12시간
            if (5 >= dateTime.get(Calendar.HOUR_OF_DAY) || dateTime.get(Calendar.HOUR_OF_DAY) >= 20) {
                holder.imageViewIcon.setImageResource(weatherMoon)
            }

            holder.textViewTime.text = dateTime.get(Calendar.HOUR).toString() + "시"
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

    fun setAdapterData(data: List<WeatherDTO?>) {
        this.data = data
        notifyDataSetChanged()
    }
}


// TODO Calendar 객체는 Month 가 0 부터 시작 (0~11) 이를 감안하여 처리해야 한다.
fun getCalendarFromItem(item: WeatherDTO): Calendar =
    (item.date + item.time).run {
        Calendar.getInstance().apply {
            set(
                this@run.substring(0, 4).toInt(),
                this@run.substring(4, 6).toInt()-1,
                this@run.substring(6, 8).toInt(),
                this@run.substring(8, 10).toInt(),
                this@run.substring(10).toInt()
            )
        }
    }

// Calendar 의 차이에 따른 날짜의 갯수를 구해야 하므로, 해당 날짜의 최소시작을 리턴
fun getCalendarToday(): Calendar = Calendar.getInstance().apply {
    set(
        this.get(Calendar.YEAR),
        this.get(Calendar.MONTH),
        this.get(Calendar.DAY_OF_MONTH),
        0,
        1
    )
}

fun getTimeStamp() {

}