package com.aliucord.plugins

import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI
import com.aliucord.entities.Plugin
import com.discord.api.commands.ApplicationCommandType
import com.discord.models.commands.ApplicationCommandOption
import com.discord.utilities.rest.RestAPI

@AliucordPlugin
class Fart : Plugin() {
    override fun start(context: Context?) {
        // p1: name p2: description p3:
        val options = listOf(
            ApplicationCommandOption(
                ApplicationCommandType.USER,
                "fartee", "The user that MUST fart (bonus points if ven)", null, false,
                true, null, null
            )
        )

        // hi ven :3
        commands.registerCommand("fart", "Order someone to fart, effective immediately", options) {
            if (it.mentionedUsers.count() > 0) {
                CommandsAPI.CommandResult("<@${it.mentionedUsers[0].id}> Fart", null, true)
            } else {
                CommandsAPI.CommandResult("fart", null, true)
            }
        }
    }

    override fun stop(context: Context?) {
        commands.unregisterAll()
    }
}