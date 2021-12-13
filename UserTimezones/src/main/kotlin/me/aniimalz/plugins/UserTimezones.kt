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
import com.aliucord.fragments.SelectDialog
import com.aliucord.patcher.Hook
import com.aliucord.utils.DimenUtils
import com.discord.widgets.user.usersheet.WidgetUserSheet
import com.discord.widgets.user.usersheet.WidgetUserSheetViewModel
import com.lytefast.flexinput.R
import java.time.*
import java.util.*

@AliucordPlugin
class UserTimezones : Plugin() {
    init {
        settingsTab = SettingsTab(
            PluginSettings::class.java,
            SettingsTab.Type.PAGE
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

                    return@Hook
                }
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
                                    Utils.showToast("UTC${timezones[tz]} selected")
                                    val timeInUtc = OffsetDateTime.of(
                                        LocalDateTime.now(), ZoneOffset.of(
                                            timezones[tz]
                                        )
                                    )
                                    text = "${timeInUtc.hour}:${timeInUtc.minute} (UTC${timezones[tz]})"
                                }
                                show(Utils.appActivity.supportFragmentManager, "uhhhhhhhh")
                            }
                        }
                        layout.addView(this, layout.indexOfChild(header))
                    }
                tzView.text = "Click to set a timezone"

            })
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}