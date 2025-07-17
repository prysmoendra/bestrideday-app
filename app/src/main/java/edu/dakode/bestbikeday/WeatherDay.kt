package edu.dakode.bestbikeday

data class WeatherDay(
    val date: String,
    val maxTemp: Double,
    val minTemp: Double,
    val precipitation: Double,
    val wind: Double,
    val score: Int
) 