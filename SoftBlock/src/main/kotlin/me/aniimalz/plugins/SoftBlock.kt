package me.aniimalz.plugins

import android.content.Context
import com.aliucord.Logger
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin

@AliucordPlugin
class SoftBlock : Plugin() {
    init {
        settingsTab = SettingsTab(SoftBlockSettings::class.java, SettingsTab.Type.PAGE).withArgs(settings)
    }

    private val logger: Logger = Logger("SoftBlock")

    override fun start(ctx: Context) {

    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}