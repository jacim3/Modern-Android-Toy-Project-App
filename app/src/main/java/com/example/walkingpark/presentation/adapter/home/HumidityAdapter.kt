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
import com.example.walkingpark.data.model.dto.WeatherDTO
import com.example.walkingpark.presentation.GetScrollItemListener
import java.lang.NumberFormatException
import java.util.*

class HumidityAdapter(val listener: GetScrollItemListener) : RecyclerView.Adapter<HumidityAdapter.HumidityViewHolder>() {

    var data = emptyList<WeatherDTO?>()
    private var prevDate: Calendar = Calendar.getInstance().apply {
        set(1990, 1, 1)
    }

    class HumidityViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val imageViewIcon: AppCompatImageView = itemView.findViewById(R.id.imageViewHumidityIcon)
        val textViewTime: AppCompatTextView = itemView.findViewById(R.id.textViewHumidityTime)
        val textViewValue: AppCompatTextView = itemView.findViewById(R.id.textViewHumidityValue)
        val container: LinearLayoutCompat = itemView.findViewById(R.id.humidityItem)
        val seperator: LinearLayoutCompat = itemView.findViewById(R.id.humiditySeperator)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HumidityViewHolder {
        return HumidityViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.home_recyclerview_item_humidity, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: HumidityViewHolder, position: Int) {
        val item = data[position]

        if (item != null) {
            holder.container.visibility = View.VISIBLE
            holder.seperator.visibility = View.GONE

            val date = item.date
            val time = item.time
            val dateTime = getCalendarFromItem(item)

            val value = item.humidity.run {
                try {
                    this.toInt()
                } catch (e: NumberFormatException) {
                    0
                }
            }

            holder.imageViewIcon.setBackgroundResource(
                when (value) {
                    in 5..10 -> R.drawable.ic_weather_humidity_1
                    in 11..20 -> R.drawable.ic_weather_humidity_2
                    in 21..30 -> R.drawable.ic_weather_humidity_3
                    in 31..40 -> R.drawable.ic_weather_humidity_4
                    in 41..50 -> R.drawable.ic_weather_humidity_5
                    in 51..65 -> R.drawable.ic_weather_humidity_6
                    in 66..79 -> R.drawable.ic_weather_humidity_7
                    in 80..100 -> R.drawable.ic_weather_humidity_8
                    else -> R.drawable.ic_weather_humidity_0
                }
            )
            holder.textViewTime.text = time
            holder.textViewValue.text = item.humidity + "%"
            listener.getItem(holder.absoluteAdapterPosition, "$date $time")
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