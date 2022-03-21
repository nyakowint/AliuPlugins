package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.patcher.instead
import com.discord.R
import com.discord.databinding.WidgetSettingsBinding
import com.discord.databinding.WidgetUserStatusUpdateBinding
import com.discord.widgets.settings.WidgetSettings
import com.discord.widgets.tabs.WidgetTabsHost
import com.discord.widgets.user.WidgetUserStatusSheet


@AliucordPlugin
class TestPlugin : Plugin() {
    @SuppressLint("SetTextI18n")
    override fun start(ctx: Context) {
        // i hate android stop being idiot when
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}