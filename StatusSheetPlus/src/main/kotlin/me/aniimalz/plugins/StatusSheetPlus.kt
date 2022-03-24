package me.aniimalz.plugins

import android.content.Context
import android.os.Bundle
import android.view.View
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.discord.widgets.user.WidgetUserStatusSheet

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