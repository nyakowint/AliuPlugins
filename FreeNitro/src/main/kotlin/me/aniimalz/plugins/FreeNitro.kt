package me.aniimalz.plugins

import android.content.Context
import com.aliucord.api.CommandsAPI
import com.aliucord.entities.Plugin

class FreeNitro : Plugin() {
    override fun start(ctx: Context) {
        commands.registerCommand("nitro", "Free nitro! Totally not a scam 10% legit") {
            CommandsAPI.CommandResult(
                "I installed the FreeNitro plugin probably expecting free nitro features, but now know that such things are not possible! What an epic realization :posttroll: <:iloveccp:892176962934689822><:iloveccp:892176962934689822> \uD83C\uDDE8\uD83C\uDDF3",
                null,
                true
            )
        }
    }

    override fun stop(context: Context) {
        commands.unregisterAll()
    }
}