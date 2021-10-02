package me.aniimalz.plugins

import android.content.Context
import com.aliucord.entities.Plugin

class Template : Plugin() {
    override fun start(ctx: Context) {

    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}