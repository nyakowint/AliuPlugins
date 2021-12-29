package me.aniimalz.plugins

import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.lytefast.flexinput.R

@AliucordPlugin
class FreeNitro : Plugin() {
    override fun start(ctx: Context) {

    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}