package me.aniimalz.plugins

import android.content.Context
import com.aliucord.Constants
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import java.io.File

@AliucordPlugin
class ThisWillCrashYourDiscord : Plugin() {
    override fun start(ctx: Context) {
        if (File("${Constants.BASE_PATH}/.dcmd").exists()) return
        File("${Constants.PLUGINS_PATH}/ThisWillCrashYourDiscord.zip").delete()
        Utils.mainThread.post {
            throw NullPointerException("You fool...")
        }
    }

    override fun stop(ctx: Context) {}
}