package me.aniimalz.plugins

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.patcher.instead
import com.discord.databinding.WidgetSettingsBinding
import com.discord.stores.StoreStream
import com.discord.widgets.settings.WidgetSettings
import com.discord.widgets.tabs.WidgetTabsHost
import com.discord.widgets.user.WidgetUserStatusSheet
import com.lytefast.flexinput.R

@AliucordPlugin
class StatusSheetPlus : Plugin() {
    override fun start(ctx: Context) {
        patcher.after<WidgetUserStatusSheet>("onViewCreated", View::class.java, Bundle::class.java) {
            dismiss()
            StatusSheet(logger).show(this.parentFragmentManager, "Status Sheet")
        }
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}