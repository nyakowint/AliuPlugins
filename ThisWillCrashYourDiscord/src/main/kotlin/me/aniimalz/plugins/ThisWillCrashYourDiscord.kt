package me.aniimalz.plugins

import android.content.Context
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.lytefast.flexinput.R
import java.lang.NullPointerException

@AliucordPlugin
class ThisWillCrashYourDiscord : Plugin() {
    override fun start(ctx: Context) {
        Utils.mainThread.post {
            throw NullPointerException("You fool...")
        }
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}