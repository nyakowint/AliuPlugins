package me.aniimalz.plugins

import android.content.Context
import com.aliucord.Constants
import com.aliucord.Logger
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.patcher.after
import com.aliucord.utils.ReflectUtils
import com.discord.api.commands.ApplicationCommandType
import com.discord.models.message.Message
import com.discord.models.user.CoreUser
import com.discord.stores.StoreStream

@AliucordPlugin
class NoKyzas : Plugin() {
    init {
        settingsTab =
            SettingsTab(KyzaSettings::class.java, SettingsTab.Type.PAGE).withArgs(settings)
    }

    private val logger: Logger = Logger("NoKyzas")

    override fun start(ctx: Context) {

        val option = Utils.createCommandOption(
            ApplicationCommandType.BOOLEAN,
            "enable",
            "whether to enable kyza mode or not (true/false)",
            null,
            true
        )

        commands.registerCommand("kyzamode", "disable kyza mode", option) {
            if (it.containsArg("enable")) {
                PluginManager.remountPlugin("NoKyzas")
                return@registerCommand CommandsAPI.CommandResult("done", null, false)
            }
            patcher.unpatchAll()
            CommandsAPI.CommandResult("done", null, false)
        }

        patcher.after<Message>("getContent") {
            if (!settings.getBool("uncap", true)) {
                return@after
            }
            val rMsg = it.thisObject as Message
            if (rMsg.channelId == Constants.PLUGIN_LINKS_CHANNEL_ID || rMsg.channelId == Constants.PLUGIN_LINKS_UPDATES_CHANNEL_ID) return@after
            try {
                val msg = (ReflectUtils.getField(rMsg, "content") as String)
                if (msg.contains("https?://".toRegex()) || msg.contains("discord.gg/")) return@after
                val author = CoreUser(rMsg.author)
                if (author.id == StoreStream.getUsers().me.id && !settings.getBool(
                        "replaceSelf",
                        false
                    )
                ) return@after
                if (author.isBot || rMsg.isWebhook && settings.getBool(
                        "ignoreBots",
                        true
                    )
                ) return@after
                val result = msg.lowercase()
                it.result = result.dropLastWhile { c -> c == '.' || c == '​' }
            } catch (h: Exception) {
                logger.warn("something went derp, oh well: ", h)
            }
        }
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}