package me.aniimalz.plugins

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

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
    "+14:00",
    "Custom (may not work)"
)

@RequiresApi(Build.VERSION_CODES.O)
fun calculateTime(timezone: String?, use24Hour: Boolean): String {
    val timeInUtc = ZonedDateTime.ofInstant(
        Instant.now(), ZoneOffset.of(
           timezone
        )
    )
    val timeAmPm =
        format12.format(format24.parse("${timeInUtc.hour}:${timeInUtc.minute}")!!)
    return if (use24Hour) "${timeInUtc.hour}:${timeInUtc.minute}" else timeAmPm
}