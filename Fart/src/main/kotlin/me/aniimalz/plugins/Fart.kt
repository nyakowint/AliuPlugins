package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI
import com.aliucord.entities.Plugin
import com.discord.api.commands.ApplicationCommandType
import com.discord.models.commands.ApplicationCommandOption

@AliucordPlugin
class Fart : Plugin() {
    @SuppressLint("SetTextI18n")
    override fun start(context: Context?) {
        val options = listOf(
            Utils.createCommandOption(
                ApplicationCommandType.USER,
                "user", "The farting user, or fartee (bonus points if ven)", null,
                required = false,
                default = true,
                channelTypes = emptyList(),
                choices = emptyList(),
                subCommandOptions = emptyList(),
                autocomplete = false
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

        commands.registerCommand(
            "supersecretcommandthatnoonewilleverfindunlessyoudidhi",
            "secret fart command part 2",
            options
        ) {
            if (it.mentionedUsers.count() > 0) {
                CommandsAPI.CommandResult(
                    "Hello <@${it.mentionedUsers[0].id}>, do you possibly think, that you could, potentially in the near future, fart? It would be monumental to everyone's experience on Aliucord™. Have a fart day!",
                    null,
                    true
                )
            } else {
                CommandsAPI.CommandResult(
                    "Hello <@343383572805058560>, do you possibly think, that you could, potentially in the near future, fart? It would be monumental to everyone's experience on Aliucord™. Have a fart day!",
                    null,
                    true
                )
            }
        }
        commands.registerCommand("shart", "shart", listOf()) {
            CommandsAPI.CommandResult("/shart", null, true)
        }

        commands.registerCommand("cock", "Send \"cock\"", options) {
            if (it.mentionedUsers.count() > 0) {
                CommandsAPI.CommandResult("<@${it.mentionedUsers[0].id}> cock", null, true)
            } else {
                CommandsAPI.CommandResult("cock", null, true)
            }
        }
    }

    override fun stop(context: Context?) {
        commands.unregisterAll()
    }
}