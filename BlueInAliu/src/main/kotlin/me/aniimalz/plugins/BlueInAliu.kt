package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import com.aliucord.Constants
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.discord.utilities.color.ColorCompat
import com.discord.widgets.settings.WidgetSettings
import com.lytefast.flexinput.R

import com.discord.widgets.debugging.WidgetDebugging

import com.aliucord.settings.Crashes

import androidx.appcompat.widget.LinearLayoutCompat
import com.aliucord.patcher.Hook

import com.aliucord.patcher.Patcher
import android.content.pm.ApplicationInfo
import com.aliucord.BuildConfig


@AliucordPlugin
class BlueInAliu : Plugin() {
    init {
        settingsTab = SettingsTab(
            PluginSettings::class.java,
            SettingsTab.Type.PAGE
        ).withArgs(settings)
    }

    @Suppress("SetTextI18n", "Deprecation") // cope
    override fun start(ctx: Context) {
        patcher.after<WidgetSettings>("onViewBound", View::class.java) {
            val context = requireContext()
            val root = it.args[0] as CoordinatorLayout
            val view = (root.getChildAt(1) as NestedScrollView).getChildAt(0) as LinearLayoutCompat
            val baseIndex = view.getChildAt(4) as TextView
            val font = ResourcesCompat.getFont(context, Constants.Fonts.whitney_medium)
            val iconColor = ColorCompat.getThemedColor(context, R.b.color_brand_400)
            val icon = ContextCompat.getDrawable(context, R.e.ic_behavior_24dp)
                ?.apply {
                    mutate()
                    setTint(iconColor)
                }
            val bcs = TextView(context, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
                text = "Bluecord Mods"
                typeface = font
                setTextColor(iconColor)
                setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null)
            }
            view.addView(bcs, view.indexOfChild(baseIndex))
            val version = view.findViewById<TextView>(Utils.getResId("app_info_header", "id"))
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            version.text = "Bluecord v2.0a - Based on Discord ${pInfo.versionName} (${pInfo.versionCode})\n" +
                    "~Made with love by Blue~"
        }
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}