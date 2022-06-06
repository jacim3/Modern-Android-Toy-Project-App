package com.example.walkingpark.ui.adapter.home

import android.annotation.SuppressLint
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.walkingpark.R
import com.example.walkingpark.constants.RAIN
import com.example.walkingpark.constants.SKY
import com.example.walkingpark.data.model.dto.simple_panel.SimplePanelDTO
import com.example.walkingpark.ui.viewmodels.getCalendarFromItem
import com.example.walkingpark.ui.viewmodels.returnAmPmAfterCheck
import com.google.android.material.internal.ViewUtils.dpToPx
import java.util.*


class WeatherAdapter() : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    var data = emptyList<SimplePanelDTO?>()

    class WeatherViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val imageViewIcon: AppCompatImageView = itemView.findViewById(R.id.imageViewWeatherIcon)
        val textViewTime: AppCompatTextView = itemView.findViewById(R.id.textViewWeatherTime)

        private val innerView: View = itemView.findViewById(R.id.includeGraph)
        val textViewTemperature: AppCompatTextView =
            innerView.findViewById(R.id.textViewValue)
        val viewMover: ConstraintLayout = innerView.findViewById(R.id.viewMover)
        val dotPointer: AppCompatImageView = innerView.findViewById(R.id.imageViewDot)

        val textViewRainChance: AppCompatTextView =
            itemView.findViewById(R.id.textViewWeatherRainChance)
        val container: ConstraintLayout = itemView.findViewById(R.id.weatherItem)
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

        item?.let {
            switchView(ITEM, holder)

            if (position == 0) holder.dotPointer.setImageResource(R.drawable.home_adapter_point_dot_big)
            else holder.dotPointer.setImageResource(R.drawable.home_adapter_point_dot)

            val dateTime = getCalendarFromItem(item)
            holder.imageViewIcon.setImageResource(checkTimeForSetWeatherMenu(item))

            holder.textViewTime.text =
                if (position == 0) "  지금  " else returnAmPmAfterCheck(
                    dateTime.get(Calendar.HOUR_OF_DAY),
                    dateTime.get(Calendar.HOUR)
                )
            holder.textViewTemperature.text = item.temperature + "°"
            holder.textViewRainChance.text = item.rainChance + " %"
            (holder.viewMover.layoutParams as ConstraintLayout.LayoutParams).let {
                it.matchConstraintPercentHeight =
                    (checkValueWeather(item.temperature)).run {
                        handleGraphValue(this)
                    }.run {
                        if (this > 1f) 1f else this
                    }
                holder.viewMover.layoutParams = it
            }
        } ?: switchView(SEPERATOR, holder)
    }

    private fun handleGraphValue(value: Int) =
        when (value) {
            in 0..10 -> value * 0.5f / 100
            in 11..19 -> value * 1.0f / 100
            in 20..30 -> value * 2f / 100
            else -> {
                value * 2.5f / 100
            }
        }

    private fun switchView(code: Int, holder: WeatherViewHolder) =
        if (code == ITEM) {
            holder.container.visibility = View.VISIBLE
            holder.seperator.visibility = View.GONE
        } else {
            holder.container.visibility = View.GONE
            holder.seperator.visibility = View.VISIBLE
        }

    override fun getItemCount()
            : Int {
        return data.size
    }

    fun setAdapterData(data: List<SimplePanelDTO?>) {
        this.data = data
        notifyDataSetChanged()
    }
}

private fun checkValueWeather(value: String) =
    value.run {
        try {
            this.toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }

fun checkTimeForSetWeatherMenu(item: SimplePanelDTO): Int {

    val rainType = checkValueWeather(item.rainType)
    val sky = checkValueWeather(item.sky)
    val dateTime = getCalendarFromItem(item)
    // 오후.
    return if (NIGHT_END >= dateTime.get(Calendar.HOUR_OF_DAY) || dateTime.get(Calendar.HOUR_OF_DAY) >= NIGHT_START) {
        setWeatherIcon(PM, rainType, sky)
    }
    // 오전.
    else {
        setWeatherIcon(AM, rainType, sky)
    }
}

fun setWeatherIcon(AmPm: Int, rainType: Int, sky: Int) =

    when (rainType) {
        RAIN.RAINY.index, RAIN.RAIN_SNOW.index, RAIN.SNOW.index, RAIN.SHOWER.index -> {
            RAIN_ICONS[AmPm][rainType]
        }
        else -> {
            when (sky) {
                SKY.CLEAR.index, SKY.CLOUDY.index, SKY.OVERCAST.index -> WEATHER_ICONS[AmPm][sky]
                else -> {
                    0
                }
            }
        }
    }



