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
import com.example.walkingpark.constants.WindDirection
import com.example.walkingpark.data.model.dto.simple_panel.SimplePanelDTO
import com.example.walkingpark.ui.viewmodels.getCalendarFromItem
import com.example.walkingpark.ui.viewmodels.returnAmPmAfterCheck
import java.lang.NumberFormatException
import java.util.*
import kotlin.math.ceil

class WindAdapter : RecyclerView.Adapter<WindAdapter.WindViewHolder>() {

    var data = emptyList<SimplePanelDTO?>()
    private var prevDate: Calendar = Calendar.getInstance().apply {
        set(1990, 1, 1)
    }

    class WindViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val imageViewIcon: AppCompatImageView = itemView.findViewById(R.id.imageViewWindIcon)
        val textViewTime: AppCompatTextView = itemView.findViewById(R.id.textViewWindTime)
        val textViewDirection: AppCompatTextView = itemView.findViewById(R.id.textViewWindDirection)
        val textViewValue: AppCompatTextView = itemView.findViewById(R.id.textViewWindValue)
        val container: LinearLayoutCompat = itemView.findViewById(R.id.windItem)
        val seperator: LinearLayoutCompat = itemView.findViewById(R.id.windSeperator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WindViewHolder {
        return WindViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.home_recyclerview_item_wind, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WindViewHolder, position: Int) {
        val item = data[position]

        if (item != null) {
            holder.container.visibility = View.VISIBLE
            holder.seperator.visibility = View.GONE
            val date = item.date
            val time = item.time
            val dateTime = getCalendarFromItem(item)

            holder.seperator.visibility = View.GONE     // RecyclerView 의 특성장 기존 View 를 재사용하므로,
            // Seperator 초기화 필요.
            val value = item.windSpeed.run {
                try {
                    ceil(this.toFloat()).toInt()
                } catch (e: NumberFormatException) {
                    0
                }
            }
            // 북남 (북:+, 남:-)
            // 바람 세기 : 0.3 이하는 고요 -> 수치 버림, 나머지는 올림 수행. by 보퍼트 풍력 기준.
            val ns = item.windNS.run {
                try {
                    ceil(this.toFloat())
                } catch (e: NumberFormatException) {
                    0f
                }
            }

            // 동서 (동:+, 서:-)
            // 바람 세기 : 0.3 이하는 고요 -> 수치 버림, 나머지는 올림 수행. by 보퍼트 풍력 기준.
            val ew = item.windEW.run {
                try {
                    ceil(this.toFloat())
                } catch (e: NumberFormatException) {
                    0f
                }
            }

            holder.textViewTime.text = if (position == 0) " 지금 " else returnAmPmAfterCheck(
                dateTime.get(Calendar.HOUR_OF_DAY),
                dateTime.get(Calendar.HOUR)
            )

            when {
                // 북 : N
                ns > 0f && ew == 0f -> setViewItems(holder, value, WindDirection.N)

                // 북동 : NE
                ns > 0f && ew > 0f -> setViewItems(holder, value, WindDirection.NE)

                // 동 : E
                ns == 0f && ew > 0f -> setViewItems(holder, value, WindDirection.E)

                // 남동 : SE
                ns < 0f && ew > 0f -> setViewItems(holder, value, WindDirection.SE)

                // 남 : S
                ns < 0f && ew == 0f -> setViewItems(holder, value, WindDirection.S)

                // 남서 : SW
                ns < 0f && ew < 0f -> setViewItems(holder, value, WindDirection.SW)

                // 서 : W
                ns == 0f && ew < 0f -> setViewItems(holder, value, WindDirection.W)

                // 북서 : NW
                ns > 0f && ew < 0f -> setViewItems(holder, value, WindDirection.NW)
            }
            prevDate.set(
                dateTime.get(Calendar.YEAR),
                dateTime.get(Calendar.MONTH),
                dateTime.get(Calendar.DAY_OF_MONTH)
            )
        } else {
            holder.container.visibility = View.GONE
            holder.seperator.visibility = View.VISIBLE
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setViewItems(holder: WindViewHolder, value: Int, direction: WindDirection) {
        holder.imageViewIcon.rotation = direction.DEGREE
        holder.textViewDirection.text = direction.text
        holder.textViewValue.text = "${value}m/s"
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setAdapterData(data: List<SimplePanelDTO?>) {
        this.data = data
        notifyDataSetChanged()
    }
}