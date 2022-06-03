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

    // 북남 (북:+, 남:-) / 동서 (동:+, 서:-)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WindViewHolder, position: Int) {
        val item = data[position]

        if (item != null) {

            switchView(ITEM, holder)
            val dateTime = getCalendarFromItem(item)
            val value = checkValue(item.windSpeed)

            calculateWindDirection(checkValue(item.windNS), checkValue(item.windEW), holder).let {
                holder.imageViewIcon.rotation = it[0] as Float
                holder.textViewDirection.text = "${it[1]}°"
            }

            holder.textViewTime.text = if (position == 0) " 지금 " else returnAmPmAfterCheck(
                dateTime.get(Calendar.HOUR_OF_DAY),
                dateTime.get(Calendar.HOUR)
            )

            holder.textViewValue.text = "${value}m/s"

        } else {
            switchView(SEPERATOR, holder)
        }
    }

    private fun checkValue(value: String) =
        value.run {
            try {
                ceil(this.toFloat()).toInt()
            } catch (e: NumberFormatException) {
                0
            }
        }

    private fun switchView(code: Int, holder: WindViewHolder) =
        if (code == ITEM) {
            holder.container.visibility = View.VISIBLE
            holder.seperator.visibility = View.GONE
        } else {
            holder.container.visibility = View.GONE
            holder.seperator.visibility = View.VISIBLE
        }

    private fun calculateWindDirection(
        ns: Int,
        ew: Int,
        holder: WindViewHolder
    ): Array<out Any> {

        return when {
            // 북 : N
            ns > 0 && ew == 0 -> setViewItems(holder, WindDirection.N)

            // 북동 : NE
            ns > 0 && ew > 0 -> setViewItems(holder, WindDirection.NE)

            // 동 : E
            ns == 0 && ew > 0 -> setViewItems(holder, WindDirection.E)

            // 남동 : SE
            ns < 0 && ew > 0 -> setViewItems(holder, WindDirection.SE)

            // 남 : S
            ns < 0 && ew == 0 -> setViewItems(holder, WindDirection.S)

            // 남서 : SW
            ns < 0 && ew < 0 -> setViewItems(holder, WindDirection.SW)

            // 서 : W
            ns == 0 && ew < 0 -> setViewItems(holder, WindDirection.W)

            // 북서 : NW :
            ns > 0 && ew < 0 -> setViewItems(holder, WindDirection.NW)

            else -> {
               setViewItems(holder, WindDirection.NE)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setViewItems(holder: WindViewHolder, direction: WindDirection) =
        arrayOf(direction.DEGREE, direction.text)


    override fun getItemCount(): Int {
        return data.size
    }

    fun setAdapterData(data: List<SimplePanelDTO?>) {
        this.data = data
        notifyDataSetChanged()
    }
}

