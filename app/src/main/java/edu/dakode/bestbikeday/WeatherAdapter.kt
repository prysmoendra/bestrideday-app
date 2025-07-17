package edu.dakode.bestbikeday

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

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
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvTemp: TextView = itemView.findViewById(R.id.tvTemp)
        private val tvPrecip: TextView = itemView.findViewById(R.id.tvPrecip)
        private val tvWind: TextView = itemView.findViewById(R.id.tvWind)
        private val tvScore: TextView = itemView.findViewById(R.id.tvScore)
        private val card: CardView = itemView as CardView

        fun bind(day: WeatherDay) {
            tvDate.text = day.date
            tvTemp.text = "Max: ${day.maxTemp}°C, Min: ${day.minTemp}°C"
            tvPrecip.text = "Precipitation: ${day.precipitation} mm"
            tvWind.text = "Wind: ${day.wind} km/h"
            tvScore.text = "Bike Ride Score: ${day.score}%"
            card.setCardBackgroundColor(scoreToColor(day.score))
        }

        // Interpolate color from red (0%) to yellow (50%) to green (100%)
        private fun scoreToColor(score: Int): Int {
            return when {
                score <= 50 -> interpolateColor(Color.RED, Color.YELLOW, score / 50f)
                else -> interpolateColor(Color.YELLOW, Color.GREEN, (score - 50) / 50f)
            }
        }

        private fun interpolateColor(colorStart: Int, colorEnd: Int, fraction: Float): Int {
            val r = Color.red(colorStart) + ((Color.red(colorEnd) - Color.red(colorStart)) * fraction).toInt()
            val g = Color.green(colorStart) + ((Color.green(colorEnd) - Color.green(colorStart)) * fraction).toInt()
            val b = Color.blue(colorStart) + ((Color.blue(colorEnd) - Color.blue(colorStart)) * fraction).toInt()
            return Color.rgb(r, g, b)
        }
    }
} 