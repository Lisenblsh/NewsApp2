package com.example.newsapp2.tools

import java.text.SimpleDateFormat
import java.util.*

val deviceFullDateFormat = SimpleDateFormat("d MMMM, HH:mm", Locale.getDefault())
val deviceDateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
val defaultDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT)
val currentTimeZone = GregorianCalendar().timeZone.rawOffset


fun convertToDeviceDate(date: String): String {
    return deviceFullDateFormat.format(Date((defaultDate.parse(date)?.time ?: 0) + currentTimeZone))
}

fun convertToDeviceDate(date: Long): String {
    return deviceDateFormat.format(date)
}

fun convertToAPIDate(date: Long): String {
    return apiDateFormat.format(date)+"T00:00:01"
}