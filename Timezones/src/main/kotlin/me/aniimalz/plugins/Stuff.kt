package me.aniimalz.plugins

import android.text.format.DateFormat
import com.aliucord.Utils
import java.text.SimpleDateFormat
import java.util.*

const val apiUrl = "https://timezonedb.catvibers.me"

fun calculateTime(timezone: String?, date: Date? = null): String {
    val tz = TimeZone.getTimeZone("GMT$timezone")
    val cal = Calendar.getInstance(tz)

    val dateFormat = if (DateFormat.is24HourFormat(Utils.appContext)) {
        SimpleDateFormat("HH:mm", Locale.getDefault())
    } else {
        SimpleDateFormat("hh:mm a", Locale.getDefault())
    }

    dateFormat.timeZone = tz
    return dateFormat.format(date ?: cal.time)
}

fun formatTimeText(timezone: String?): String {
    return "${calculateTime(timezone)} (UTC${timezone})"
}

val timezones = arrayOf(
    "-12:00",
    "-11:00",
    "-10:00",
    "-09:30",
    "-09:00",
    "-08:00",
    "-07:00",
    "-06:00",
    "-05:00",
    "-04:00",
    "-03:30",
    "-03:00",
    "-02:00",
    "-01:00",
    "+00:00",
    "+01:00",
    "+02:00",
    "+03:00",
    "+03:30",
    "+04:00",
    "+04:30",
    "+05:00",
    "+05:30",
    "+05:45",
    "+06:00",
    "+06:30",
    "+07:00",
    "+08:00",
    "+08:45",
    "+09:00",
    "+09:30",
    "+10:00",
    "+10:30",
    "+11:00",
    "+12:00",
    "+12:45",
    "+13:00",
    "+14:00"
)