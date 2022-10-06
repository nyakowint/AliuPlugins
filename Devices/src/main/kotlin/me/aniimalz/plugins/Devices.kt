package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import com.aliucord.Constants
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.discord.utilities.color.ColorCompat
import com.discord.widgets.settings.WidgetSettings
import com.lytefast.flexinput.R

@AliucordPlugin
class Devices : Plugin() {
    init {
        settingsTab = SettingsTab(
            SessionsPage::class.java,
            SettingsTab.Type.PAGE
        )
    }

    @SuppressLint("SetTextI18n")
    override fun start(ctx: Context) {
        commands.registerCommand("sessions", "View your devices logged in") {
            Utils.openPageWithProxy(Utils.appActivity, SessionsPage())
            CommandsAPI.CommandResult()
        }

        // i love reusing old code :trolley:
        patcher.after<WidgetSettings>("onViewBound", View::class.java) {
            val context = requireContext()
            val root = it.args[0] as CoordinatorLayout
            val view = (root.getChildAt(1) as NestedScrollView).getChildAt(0) as LinearLayoutCompat
            val baseIndex = view.getChildAt(7) as TextView
            val font = ResourcesCompat.getFont(context, Constants.Fonts.whitney_medium)
            val icon = ContextCompat.getDrawable(context, R.e.ic_phone_24dp)
                ?.apply {
                    mutate()
                    Utils.tintToTheme(this)
                }
            val bcs = TextView(context, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
                text = "Devices"
                typeface = font
                setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null)
            }
            view.addView(bcs, view.indexOfChild(baseIndex))
            view.setOnClickListener {
                Utils.openPageWithProxy(Utils.appActivity, SessionsPage())
            }
        }
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}