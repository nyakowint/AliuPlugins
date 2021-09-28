package com.aliucord.plugins

import android.annotation.SuppressLint
import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI
import com.aliucord.entities.Plugin
import com.discord.api.commands.ApplicationCommandType
import com.discord.models.commands.ApplicationCommandOption
import com.discord.utilities.rest.RestAPI

@AliucordPlugin
class Fart : Plugin() {
    @SuppressLint("SetTextI18n")
    override fun start(context: Context?) {
        val options = listOf(
            ApplicationCommandOption(
                ApplicationCommandType.USER,
                "user", "The farting user, or fartee (bonus points if ven)", null, false,
                true, null, null
            )
        )

        // hi ven :3
        commands.registerCommand("fart", "Tell someone to fart", options) {
            if (it.mentionedUsers.count() > 0) {
                CommandsAPI.CommandResult("<@${it.mentionedUsers[0].id}> fart", null, true)
            } else {
                CommandsAPI.CommandResult("fart", null, true)
            }
        }
    }

    override fun stop(context: Context?) {
        commands.unregisterAll()
    }
}