package me.aniimalz.plugins

import android.content.Context
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI
import com.aliucord.entities.Plugin
import com.discord.stores.StoreStream

@AliucordPlugin
class ReadAllGuilds : Plugin() {
    override fun start(ctx: Context) {
        commands.registerCommand("readallguilds", "Mark all of your guilds as read. Will wait 5 seconds between actions to not spam api") {
            Utils.threadPool.execute {
                StoreStream.getGuilds().guilds.keys.forEach {
                    StoreStream.getMessageAck().ackGuild(ctx, it) {
                        Thread.sleep(5000)
                    }
                }
            }
            CommandsAPI.CommandResult("Marking all guilds as read. This can take a while if you're in a lot of servers", null, false)
        }
    }

    override fun stop(ctx: Context) {
        commands.unregisterAll()
    }
}