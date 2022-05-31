package com.example.walkingpark.presentation.adapter.home

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.walkingpark.R
import com.example.walkingpark.data.model.dto.WeatherDTO
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HumidityAdapter : RecyclerView.Adapter<HumidityAdapter.HumidityViewHolder>(){

    var data = emptyList<WeatherDTO>()

    class HumidityViewHolder constructor(itemView: View, val seperator:View) : RecyclerView.ViewHolder(itemView) {
        val imageViewIcon:AppCompatImageView = itemView.findViewById(R.id.imageViewHumidityIcon)
        val textViewTime:AppCompatTextView = itemView.findViewById(R.id.textViewHumidityTime)
        val textViewValue:AppCompatTextView = itemView.findViewById(R.id.textViewHumidityValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HumidityViewHolder {
        return HumidityViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.home_recyclerview_item_humidity, parent, false),
            LayoutInflater.from(parent.context)
                .inflate(R.layout.home_recyclerview_item_seperator, parent, false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: HumidityViewHolder, position: Int) {
        val item = data[position]
        val date = item.date
        val time = item.time
        val dateTime = getLocalDateTime(item.date+item.time)


        holder.textViewTime.text = time
        holder.textViewValue.text = item.humidity
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