package com.alejandroelv.myfitnesstrack

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TimeUtils {
    fun getTodayDate(): String {
        val c = Calendar.getInstance()

        val year = c.get(Calendar.YEAR).toString()
        var month = (c.get(Calendar.MONTH) + 1).toString()
        var day = c.get(Calendar.DAY_OF_MONTH).toString()

        if(month.toInt() < 10){ month = "0$month" }
        if(day.toInt() < 10){ day = "0$day" }

        return "$year-$month-$day"
    }

    fun calculateDate(date: String, daysToAdd: Int): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val c = Calendar.getInstance()
        try {
            c.time = sdf.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        c.add(Calendar.DATE, daysToAdd)

        return sdf.format(c.time)
    }
}