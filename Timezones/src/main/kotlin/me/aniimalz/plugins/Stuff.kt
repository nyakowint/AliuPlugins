package me.aniimalz.plugins

import android.text.format.DateFormat
import com.aliucord.Utils
import java.util.*

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

const val apiUrl = "https://timezonedb.bigdumb.gq"

fun calculateTime(timezone: String?): String {
    val use24Hour = DateFormat.is24HourFormat(Utils.appContext);
    val tz = TimeZone.getTimeZone("GMT$timezone")
    val cal = Calendar.getInstance(tz)

    with(cal) {
        return when {
            use24Hour -> "%02d:%02d".format(get(Calendar.HOUR_OF_DAY), get(Calendar.MINUTE))
            else -> "%02d:%02d ${arrayOf("am", "pm")[get(Calendar.AM_PM)]}".format(get(Calendar.HOUR), get(Calendar.MINUTE))
        }
    }
}

fun formatTimeText(timezone: String?): String {
    return "${calculateTime(timezone)} (UTC${timezone})"
}