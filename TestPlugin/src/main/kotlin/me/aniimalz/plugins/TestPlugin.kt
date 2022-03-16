package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.discord.databinding.WidgetUserStatusUpdateBinding
import com.discord.widgets.settings.WidgetSettings
import com.discord.widgets.tabs.WidgetTabsHost
import com.discord.widgets.user.WidgetUserStatusSheet

@AliucordPlugin
class TestPlugin : Plugin() {
    @SuppressLint("SetTextI18n")
    override fun start(ctx: Context) {
        // i hate ven

/*        // patch settings status button
        patcher.after<WidgetSettings>("onViewBound", View::class.java) {
            val bind = this.javaClass.getDeclaredMethod("getBinding").let { m ->
                m.isAccessible = true
                m.invoke(this) as WidgetSettingsBinding
            }.root
            val setStatus =
                bind.findViewById<LinearLayout>(Utils.getResId("set_status_container", "id"))
            setStatus.setOnClickListener {
                StatusSheetPlus().show(parentFragmentManager, "Status Sheet")
            }
        }

        // patch tabs long press
        patcher.instead<WidgetTabsHost>("onSettingsLongPress") {
            StatusSheetPlus().show(parentFragmentManager, "Status Sheet")
        }*/

        patcher.after<WidgetUserStatusSheet>("onViewCreated", View::class.java, Bundle::class.java) {
            val bind = this.javaClass.getDeclaredMethod("getBinding").let { m ->
                m.isAccessible = true
                m.invoke(this) as WidgetUserStatusUpdateBinding
            }.root

            val header = bind.findView<TextView>()
        }
    }


/*    private fun <T: View> View.findView(id: String) {
        return findViewById<T>(Utils.getResId(id, "id"))
    }*/

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}