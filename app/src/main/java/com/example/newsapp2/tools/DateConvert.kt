package com.example.newsapp2.tools

import com.example.newsapp2.data.network.TypeNewsUrl
import java.text.SimpleDateFormat
import java.util.*

val deviceFullDateFormat = SimpleDateFormat("d MMMM, HH:mm", Locale.getDefault())
val deviceDateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
val dateFormat1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT)
val dateFormat2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'", Locale.ROOT)
val dateFormat3 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
val currentTimeZone = GregorianCalendar().timeZone.rawOffset


fun convertToDeviceDate(date: Long): String {
    return deviceFullDateFormat.format(date)
}

fun convertToDeviceDateFilter(date: Long): String {
    return deviceDateFormat.format(date)
}

fun convertToAPIDate(date: Long): String {
    return apiDateFormat.format(date) + "T00:00:01"
}

fun convertDateToMillis(date: String, typeNewsUrl: TypeNewsUrl): Long {
    return when (typeNewsUrl) {
        TypeNewsUrl.NewsApi -> {
            dateFormat1.parse(date)?.time?.plus(currentTimeZone) ?: 0
        }
        TypeNewsUrl.BingNews -> {
            dateFormat2.parse(date)?.time?.plus(currentTimeZone) ?: 0
        }
        TypeNewsUrl.Newscatcher -> {
            dateFormat3.parse(date)?.time?.plus(currentTimeZone) ?: 0
        }
        TypeNewsUrl.StopGame -> {
            dateFormat3.parse(date)?.time?.plus(currentTimeZone) ?: 0
        }
    }
}