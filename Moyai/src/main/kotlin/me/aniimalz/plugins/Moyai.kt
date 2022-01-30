package me.aniimalz.plugins

import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.lytefast.flexinput.R

@AliucordPlugin
class Moyai : Plugin() {
    init {
        settingsTab = SettingsTab(
            PluginSettings::class.java,
            SettingsTab.Type.PAGE
        ).withArgs(settings)
    }

    override fun start(ctx: Context) {

    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}