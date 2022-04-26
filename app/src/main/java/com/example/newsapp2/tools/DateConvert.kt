package com.example.newsapp2.tools

import com.example.newsapp2.data.network.TypeNewsUrl
import java.text.SimpleDateFormat
import java.util.*

val deviceFullDateFormat = SimpleDateFormat("d MMMM, HH:mm", Locale.getDefault())
val deviceDateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
val defaultDate1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT)
val defaultDate2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'", Locale.ROOT)
val defaultDate3 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
val currentTimeZone = GregorianCalendar().timeZone.rawOffset


fun convertToDeviceDate(date: Long, timeZone: Int = currentTimeZone): String {
    return deviceFullDateFormat.format(date.plus(timeZone))
}
fun convertToAPIDate(date: Long): String {
    return apiDateFormat.format(date)+"T00:00:01"
}

fun convertDateToMillis(date: String, typeNewsUrl: TypeNewsUrl): Long{
    return when(typeNewsUrl){
        TypeNewsUrl.NewsApi -> {
            defaultDate1.parse(date)?.time?.plus(currentTimeZone) ?: 0
        }
        TypeNewsUrl.BingNews -> {
            defaultDate2.parse(date)?.time?.plus(currentTimeZone) ?: 0
        }
        TypeNewsUrl.Newscatcher -> {
            defaultDate3.parse(date)?.time?.plus(currentTimeZone) ?: 0
        }
    }
}