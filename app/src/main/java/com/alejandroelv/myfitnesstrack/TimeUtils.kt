package com.alejandroelv.myfitnesstrack

import android.util.Log
import java.util.*

class TimeUtils {
    public fun getTodayDate() : String{
        val c = Calendar.getInstance()

        val year = c.get(Calendar.YEAR).toString()
        val month  = (c.get(Calendar.MONTH)+1).toString()
        val day = c.get(Calendar.DAY_OF_MONTH).toString()

        val formattedDate = "$year-$month-$day"
        return formattedDate
    }
}