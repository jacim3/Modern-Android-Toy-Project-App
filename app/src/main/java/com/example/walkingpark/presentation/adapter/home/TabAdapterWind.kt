package com.example.walkingpark.presentation.adapter.home

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.walkingpark.R
import com.example.walkingpark.data.model.dto.WeatherDTO
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TabAdapterWind: RecyclerView.Adapter<TabAdapterWind.WindViewHolder>() {

    var data = emptyList<WeatherDTO>()

    class WindViewHolder constructor(itemView: View, val seperator:View) : RecyclerView.ViewHolder(itemView) {
        val imageViewIcon: AppCompatImageView = itemView.findViewById(R.id.imageViewIcon)
        val textViewTime: AppCompatTextView = itemView.findViewById(R.id.textViewTime)
        val textViewDirection:AppCompatTextView = itemView.findViewById(R.id.textViewDirection)
        val textViewValue:AppCompatTextView = itemView.findViewById(R.id.textViewValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WindViewHolder {
        return WindViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.home_recyclerview_item_weather, parent, false),
            LayoutInflater.from(parent.context)
                .inflate(R.layout.home_recyclerview_item_seperator, parent, false)
        )
    }

    override fun onBindViewHolder(holder: WindViewHolder, position: Int) {
        val item = data[position]
        val date = item.date
        val time = item.time
        val ns = item.windNS
        val ew = item.windEW

        holder.textViewTime.text = time
        // holder.textViewDirection.text =
        holder.textViewValue.text = item.windSpeed
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