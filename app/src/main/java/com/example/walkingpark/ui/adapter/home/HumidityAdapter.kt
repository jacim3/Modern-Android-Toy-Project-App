package com.example.walkingpark.ui.adapter.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.walkingpark.R
import com.example.walkingpark.data.model.dto.simple_panel.SimplePanelDTO
import com.example.walkingpark.ui.viewmodels.getCalendarFromItem
import com.example.walkingpark.ui.viewmodels.returnAmPmAfterCheck
import java.util.*


class HumidityAdapter :
    RecyclerView.Adapter<HumidityAdapter.HumidityViewHolder>() {

    private lateinit var graphDecorator: RecyclerView.ItemDecoration

    var data = emptyList<SimplePanelDTO?>()
    private var prevDate: Calendar = Calendar.getInstance().apply {
        set(1990, 1, 1)
    }

    class HumidityViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val imageViewIcon: AppCompatImageView = itemView.findViewById(R.id.imageViewHumidityIcon)
        val textViewTime: AppCompatTextView = itemView.findViewById(R.id.textViewHumidityTime)

        private val innerView: View = itemView.findViewById(R.id.includeGraph)
        val textViewValue: AppCompatTextView =
            innerView.findViewById(R.id.textViewValue)
        val viewMover: ConstraintLayout = innerView.findViewById(R.id.viewMover)
        val dotPointer: AppCompatImageView = innerView.findViewById(R.id.imageViewDot)

        val container: ConstraintLayout = itemView.findViewById(R.id.humidityItem)
        val seperator: LinearLayoutCompat = itemView.findViewById(R.id.humiditySeperator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HumidityViewHolder {
        return HumidityViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.home_recyclerview_item_humidity, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HumidityViewHolder, position: Int) {
        val item = data[position]

        item?.let {
            switchView(ITEM, holder)

            if (position == 0) holder.dotPointer.setImageResource(R.drawable.home_adapter_point_dot_big)
            else holder.dotPointer.setImageResource(R.drawable.home_adapter_point_dot)

            val dateTime = getCalendarFromItem(item)
            holder.imageViewIcon.setImageResource(getCalculatedHumidityIcon(item.humidity))
            holder.textViewTime.text =
                if (position == 0) " 지금 " else returnAmPmAfterCheck(
                    dateTime.get(Calendar.HOUR_OF_DAY),
                    dateTime.get(Calendar.HOUR)
                )
            holder.textViewValue.text = item.humidity + "%"

            (holder.viewMover.layoutParams as ConstraintLayout.LayoutParams).let {
                it.matchConstraintPercentHeight =
                    item.humidity.run {
                        try {
                            this.toFloat()/100f
                        } catch (e: java.lang.NumberFormatException) {
                            0f
                        }
                    }
                holder.viewMover.layoutParams = it
            }

        } ?: switchView(SEPERATOR, holder)
    }

    private fun switchView(code: Int, holder: HumidityViewHolder) =
        if (code == ITEM) {
            holder.container.visibility = View.VISIBLE
            holder.seperator.visibility = View.GONE
        } else {
            holder.container.visibility = View.GONE
            holder.seperator.visibility = View.VISIBLE
        }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setAdapterData(data: List<SimplePanelDTO?>) {
        this.data = data
        notifyDataSetChanged()
    }
}

fun getCalculatedHumidityIcon(value: String) =

    value.run {
        try {
            this.toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }.run {
        when (this) {
            in 5..10 -> R.drawable.ic_humidity_1
            in 11..20 -> R.drawable.ic_humidity_2
            in 21..30 -> R.drawable.ic_humidity_3
            in 31..40 -> R.drawable.ic_humidity_4
            in 41..50 -> R.drawable.ic_humidity_5
            in 51..65 -> R.drawable.ic_humidity_6
            in 66..79 -> R.drawable.ic_humidity_7
            in 80..100 -> R.drawable.ic_humidity_8
            else -> R.drawable.ic_humidity_0
        }
    }