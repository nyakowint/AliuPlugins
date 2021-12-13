package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Constants
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.fragments.InputDialog
import com.aliucord.fragments.SelectDialog
import com.aliucord.patcher.Hook
import com.aliucord.utils.DimenUtils
import com.discord.models.user.User
import com.discord.widgets.user.usersheet.WidgetUserSheet
import com.discord.widgets.user.usersheet.WidgetUserSheetViewModel
import com.lytefast.flexinput.R
import java.text.SimpleDateFormat
import java.time.*
import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.hours

@AliucordPlugin
class UserTimezones : Plugin() {
    init {
        settingsTab = SettingsTab(
            PluginSettings::class.java,
            SettingsTab.Type.BOTTOM_SHEET
        ).withArgs(settings)
    }

    private val tzId = View.generateViewId()

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun start(ctx: Context) {
        val clock = ContextCompat.getDrawable(ctx, R.e.ic_archived_clock_dark)?.apply {
            mutate()
        }

        patcher.patch(
            WidgetUserSheet::class.java.getDeclaredMethod(
                "configureNote",
                WidgetUserSheetViewModel.ViewState.Loaded::class.java
            ), Hook {
                val loaded = it.args[0] as WidgetUserSheetViewModel.ViewState.Loaded
                val user = loaded.user
                if (user == null || user.isBot) return@Hook
                val binding =
                    WidgetUserSheet.`access$getBinding$p`(it.thisObject as WidgetUserSheet)
                val headerId = Utils.getResId("about_me_header_container", "id")
                val header = binding.a.findViewById<ViewGroup>(headerId)
                val layout = header.parent as LinearLayout

                var tzView = layout.findViewById<TextView>(tzId)
                if (tzView != null) {
                    tzView.text = "Click to set a timezone"
                    setUserSheetTime(user, settings.getBool("24hourTime", false))
                    return@Hook
                }
                val format12 = SimpleDateFormat("hh:mm a")
                val format24 = SimpleDateFormat("HH:mm")
                tzView =
                    TextView(layout.context, null, 0, R.i.UserProfile_Section_Header).apply {
                        id = tzId
                        typeface =
                            ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_semibold)
                        val dp = DimenUtils.defaultPadding
                        compoundDrawablePadding = DimenUtils.dpToPx(8)
                        setPadding(dp, dp, dp, dp)
                        setCompoundDrawablesRelativeWithIntrinsicBounds(clock, null, null, null)
                        setOnClickListener {
                            SelectDialog().apply {
                                title = "Set timezone (UTC)"
                                items = timezones
                                onResultListener = { tz ->
                                    if (timezones[tz].contains("Custom")) {
                                        InputDialog().apply {
                                            title = "Custom Offset"
                                            setDescription("Type a custom UTC offset here (e.g +05:50 or -02:10)")
                                            setOnOkListener {

                                            }
                                            show(Utils.appActivity.supportFragmentManager, "idiot_country_offsets")
                                        }
                                    }
                                    Utils.showToast("UTC${timezones[tz]} selected")
                                    val timeInUtc = ZonedDateTime.ofInstant(
                                        Instant.now(), ZoneOffset.of(
                                            timezones[tz]
                                        )
                                    )
                                    val timeAmPm = format12.format(format24.parse("${timeInUtc.hour}:${timeInUtc.minute}")!!)
                                    text = "$timeAmPm (UTC${timezones[tz]})"
                                }
                                show(Utils.appActivity.supportFragmentManager, "timezone_selector")
                            }
                        }
                        layout.addView(this, layout.indexOfChild(header))
                    }
                tzView.text = "Click to set a timezone"

            })
    }

    fun setUserSheetTime(user: User, use24Hour: Boolean) {
        
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}