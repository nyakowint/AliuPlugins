package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import com.aliucord.Logger
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI
import com.aliucord.entities.Plugin
import com.discord.api.commands.ApplicationCommandType

@AliucordPlugin
class Fart : Plugin() {
    private val logger: Logger = Logger("Fart")

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
            if (it.containsArg("user")) {
                CommandsAPI.CommandResult("<@${it.getRequiredUser("user").id}> fart", null, true)
            } else {
                CommandsAPI.CommandResult("fart", null, true)
            }
        }

        commands.registerCommand(
            "betterfart",
            "fart but better",
            options
        ) {
            if (it.mentionedUsers.count() > 0) {
                CommandsAPI.CommandResult(
                    "Hello <@${it.getRequiredUser("user").id}>, do you possibly think, that you could, potentially in the near future, fart? It would be monumental to everyone's experience on Aliucord™. Have a fart day!",
                    null,
                    true
                )
            } else {
                CommandsAPI.CommandResult(
                    "Hello everyone, do you possibly think, that you could, potentially in the near future, fart? It would be monumental to your experience on Aliucord™. Have a fart day!",
                    null,
                    true
                )
            }
        }

        // epic troll command
        commands.registerCommand("shart", "If it doesnt work reinstall aliucord", listOf()) {
            CommandsAPI.CommandResult("/shart", null, true)
        }

        commands.registerCommand("cock", "Send \"cock\"", options) {
            if (it.mentionedUsers.count() > 0) {
                CommandsAPI.CommandResult("<@${it.getRequiredUser("user").id}> cock", null, true)
            } else {
                CommandsAPI.CommandResult("cock", null, true)
            }
        }
    }

    override fun stop(context: Context?) {
        commands.unregisterAll()
    }
}