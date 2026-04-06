package me.aniimalz.plugins

import android.text.format.DateFormat
import com.aliucord.Utils
import java.text.SimpleDateFormat
import java.util.*

const val apiUrl = "https://timezonedb.catvibers.me"

fun calculateTime(timezone: String?, date: Date? = null): String {
    if (timezone == null) return "00:00"
    
    val isOffset = timezone.matches(Regex("""^[+-]\d{1,2}(:?\d{2})?$"""))
    val tzId = if (isOffset) "GMT$timezone" else timezone

    val tz = TimeZone.getTimeZone(tzId)
    val cal = Calendar.getInstance(tz)
    if (date != null) cal.time = date

    val is24Hour = DateFormat.is24HourFormat(Utils.appContext)
    val pattern = if (is24Hour) "HH:mm" else "hh:mm a"
    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())

    dateFormat.timeZone = tz
    return dateFormat.format(cal.time)
}

fun formatTimeText(timezone: String?): String {
    return "${calculateTime(timezone)} (${timezone ?: "UTC"})"
}

val timezones = arrayOf(
    "America/New_York", "America/Chicago", "America/Denver", "America/Los_Angeles", 
    "America/Phoenix", "America/Anchorage", "America/Halifax", "America/St_Johns",
    "America/Mexico_City", "America/Panama", "America/Bogota", "America/Lima", 
    "America/Santiago", "America/Sao_Paulo", "America/Recife", "America/Argentina/Buenos_Aires",
    "Europe/London", "Europe/Dublin", "Europe/Paris", "Europe/Berlin", 
    "Europe/Rome", "Europe/Madrid", "Europe/Kyiv", "Europe/Warsaw", "Europe/Moscow",
    "Africa/Cairo", "Africa/Johannesburg", "Africa/Lagos", "Africa/Nairobi", "Africa/Casablanca",
    "Asia/Jerusalem", "Asia/Dubai", "Asia/Riyadh", "Asia/Tehran", "Asia/Kabul", 
    "Asia/Kolkata", "Asia/Kathmandu", "Asia/Bangkok", "Asia/Singapore", 
    "Asia/Shanghai", "Asia/Tokyo", "Asia/Seoul", "Asia/Jakarta",
    "Australia/Perth", "Australia/Adelaide", "Australia/Darwin", "Australia/Sydney", 
    "Australia/Brisbane", "Pacific/Auckland", "Pacific/Fiji", "Pacific/Honolulu",
	"-12:00", "-11:00", "-10:00", "-09:30", "-09:00", "-08:00", "-07:00", "-06:00",
	"-05:00", "-04:00", "-03:30", "-03:00", "-02:00", "-01:00", "+00:00", "+01:00",
	"+02:00", "+03:00", "+03:30", "+04:00", "+04:30", "+05:00", "+05:30", "+05:45",
	"+06:00", "+06:30", "+07:00", "+08:00", "+08:45", "+09:00", "+09:30", "+10:00",
	"+10:30", "+11:00", "+12:00", "+12:45", "+13:00", "+14:00"
)