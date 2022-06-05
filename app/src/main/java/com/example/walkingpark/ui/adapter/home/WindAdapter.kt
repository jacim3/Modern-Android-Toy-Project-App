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

        private val innerView: View = itemView.findViewById(R.id.includeGraph)
        val textViewValue: AppCompatTextView =
            innerView.findViewById(R.id.textViewValue)
        val viewMover: ConstraintLayout = innerView.findViewById(R.id.viewMover)
        val dotPointer: AppCompatImageView = innerView.findViewById(R.id.imageViewDot)

        val container: ConstraintLayout = itemView.findViewById(R.id.windItem)
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

        item?.let {
            switchView(ITEM, holder)

            if (position == 0) holder.dotPointer.setImageResource(R.drawable.home_adapter_point_dot_big)
            else holder.dotPointer.setImageResource(R.drawable.home_adapter_point_dot)

            val dateTime = getCalendarFromItem(item)
            val value = checkValueWind(item.windSpeed)

            calculateWindDirection(
                checkValueWind(item.windNS),
                checkValueWind(item.windEW),
                holder
            ).let {
                holder.imageViewIcon.rotation = it[0] as Float
                holder.textViewDirection.text = "${it[1]}°"
            }

            holder.textViewTime.text = if (position == 0) " 지금 " else returnAmPmAfterCheck(
                dateTime.get(Calendar.HOUR_OF_DAY),
                dateTime.get(Calendar.HOUR)
            )

            (holder.viewMover.layoutParams as ConstraintLayout.LayoutParams).let {
                it.matchConstraintPercentHeight =
                    (checkValueWind(item.windSpeed))
                        .run {
                            this/10f
                        }.run {
                            if (this > 1f) 1f else this
                        }
                holder.viewMover.layoutParams = it
            }

            holder.textViewValue.text = "${value}m/s"
        } ?: switchView(SEPERATOR, holder)
    }

    private fun checkValueWind(value: String) =
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
            ns > 0 && ew == 0 -> setViewItems(WindDirection.N)

            // 북동 : NE
            ns > 0 && ew > 0 -> setViewItems( WindDirection.NE)

            // 동 : E
            ns == 0 && ew > 0 -> setViewItems(WindDirection.E)

            // 남동 : SE
            ns < 0 && ew > 0 -> setViewItems( WindDirection.SE)

            // 남 : S
            ns < 0 && ew == 0 -> setViewItems(WindDirection.S)

            // 남서 : SW
            ns < 0 && ew < 0 -> setViewItems( WindDirection.SW)

            // 서 : W
            ns == 0 && ew < 0 -> setViewItems(WindDirection.W)

            // 북서 : NW :
            ns > 0 && ew < 0 -> setViewItems( WindDirection.NW)

            // TODO Handle Else
            else -> {
                setViewItems(WindDirection.NE)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setViewItems(direction: WindDirection) =
        arrayOf(direction.DEGREE, direction.text)


    override fun getItemCount(): Int {
        return data.size
    }

    fun setAdapterData(data: List<SimplePanelDTO?>) {
        this.data = data
        notifyDataSetChanged()
    }
}

