package edu.dakode.bestbikeday

import android.graphics.Color
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import edu.dakode.bestbikeday.CirclePercentView

class WeatherAdapter(private val items: List<WeatherDay>) : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weather_card, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDayName: TextView = itemView.findViewById(R.id.tvDayName)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvTemp: TextView = itemView.findViewById(R.id.tvTemp)
        private val tvPrecip: TextView = itemView.findViewById(R.id.tvPrecip)
        private val tvWind: TextView = itemView.findViewById(R.id.tvWind)
        private val circlePercent: View = itemView.findViewById(R.id.circlePercent)
        private val card: CardView = itemView as CardView

        fun bind(day: WeatherDay) {
            tvDayName.text = getDayName(day.date)
            tvDate.text = day.date
            tvTemp.text = "Temperature: ${day.maxTemp}° / ${day.minTemp}°"
            tvPrecip.text = "Rain Chance: ${day.precipitation}%"
            tvWind.text = "Wind Speed: ${day.wind} km/h"
            (circlePercent as? CirclePercentView)?.setPercent(day.score)
            card.setCardBackgroundColor(scoreToSoftColor(day.score))
        }

        private fun getDayName(dateStr: String): String {
            return try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = sdf.parse(dateStr)
                val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                dayFormat.format(date ?: Date())
            } catch (e: Exception) {
                dateStr
            }
        }

        // Interpolate soft color from red (0%) to yellow (50%) to green (100%)
        private fun scoreToSoftColor(score: Int): Int {
            val softRed = android.graphics.Color.parseColor("#FFB3B3")
            val softYellow = android.graphics.Color.parseColor("#FFF7B2")
            val softGreen = android.graphics.Color.parseColor("#B3FFD8")
            return when {
                score <= 50 -> interpolateColor(softRed, softYellow, score / 50f)
                else -> interpolateColor(softYellow, softGreen, (score - 50) / 50f)
            }
        }
        private fun interpolateColor(colorStart: Int, colorEnd: Int, fraction: Float): Int {
            val r = android.graphics.Color.red(colorStart) + ((android.graphics.Color.red(colorEnd) - android.graphics.Color.red(colorStart)) * fraction).toInt()
            val g = android.graphics.Color.green(colorStart) + ((android.graphics.Color.green(colorEnd) - android.graphics.Color.green(colorStart)) * fraction).toInt()
            val b = android.graphics.Color.blue(colorStart) + ((android.graphics.Color.blue(colorEnd) - android.graphics.Color.blue(colorStart)) * fraction).toInt()
            return android.graphics.Color.rgb(r, g, b)
        }
    }
} 