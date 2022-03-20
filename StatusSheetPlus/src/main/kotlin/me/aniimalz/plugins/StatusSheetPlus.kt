package me.aniimalz.plugins

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.patcher.instead
import com.discord.databinding.WidgetSettingsBinding
import com.discord.stores.StoreStream
import com.discord.widgets.settings.WidgetSettings
import com.discord.widgets.tabs.WidgetTabsHost
import com.lytefast.flexinput.R

@AliucordPlugin
class StatusSheetPlus : Plugin() {
    override fun start(ctx: Context) {

        // patch settings status button
        patcher.after<WidgetSettings>("onViewBound", View::class.java) {
            val bind = this.javaClass.getDeclaredMethod("getBinding").let { m ->
                m.isAccessible = true
                m.invoke(this) as WidgetSettingsBinding
            }.root
            val setStatus =
                bind.findViewById<LinearLayout>(Utils.getResId("set_status_container", "id"))
            setStatus.setOnClickListener {
                StatusSheet(logger).show(this.parentFragmentManager, "Status Sheet")
            }
        }

        // patch tabs long press
        patcher.instead<WidgetTabsHost>("onSettingsLongPress") {
            StatusSheet(logger).show(this.parentFragmentManager, "Status Sheet")
        }
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}