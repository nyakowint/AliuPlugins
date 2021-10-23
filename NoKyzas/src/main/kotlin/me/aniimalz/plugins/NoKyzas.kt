package me.aniimalz.plugins

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.aliucord.Constants
import com.aliucord.Logger
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.utils.ReflectUtils
import com.discord.api.commands.ApplicationCommandType
import com.discord.models.message.Message
import com.discord.models.user.CoreUser
import com.discord.stores.StoreStream
import com.lytefast.flexinput.R
import java.util.regex.Pattern

@AliucordPlugin
class NoKyzas : Plugin() {
    init {
        settingsTab =
            SettingsTab(KyzaSettings::class.java, SettingsTab.Type.PAGE).withArgs(settings)
    }

    private val logger: Logger = Logger("NoKyzas")
    private var pluginIcon: Drawable? = null

    override fun load(ctx: Context) {
        pluginIcon = ContextCompat.getDrawable(ctx, R.d.ic_warning_circle_24dp)
    }

    override fun start(ctx: Context) {
        // blatantly stolen regex from ven https://github.com/Vendicated/AliucordPlugins/blob/94d7cabc8cb8886cf458ada8617b190aa670c0c3/PluginDownloader/src/main/java/dev/vendicated/aliucordplugs/plugindownloader/PluginDownloader.java#L37
        val repoPattern =
            Pattern.compile("https?://github\\.com/([A-Za-z0-9\\-_.]+)/([A-Za-z0-9\\-_.]+)")
                .toRegex()
        val zipPattern =
            Pattern.compile("https?://(?:github|raw\\.githubusercontent)\\.com/([A-Za-z0-9\\-_.]+)/([A-Za-z0-9\\-_.]+)/(?:raw|blob)?/?\\w+/(\\w+).zip")
                .toRegex()

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

        patcher.patch(Message::class.java.getDeclaredMethod("getContent"), Hook {
            if (!settings.getBool("uncap", true)) {
                return@Hook
            }
            val rMsg = it.thisObject as Message
            if (rMsg.channelId == Constants.PLUGIN_LINKS_CHANNEL_ID || rMsg.channelId == Constants.PLUGIN_LINKS_UPDATES_CHANNEL_ID) return@Hook
            try {
                val msg = (ReflectUtils.getField(rMsg, "content") as String)
                if (msg.contains("https?://".toRegex()) || msg.contains("discord.gg/")) return@Hook
/*            if (repoPattern.containsMatchIn(msg)) {
                msg = msg.replace(repoPattern, "")
            }
            if (zipPattern.containsMatchIn(msg)) {
                msg = msg.replace(zipPattern, "")
            }*/
                val author = CoreUser(rMsg.author)
                if (author.id == StoreStream.getUsers().me.id && !settings.getBool(
                        "replaceSelf",
                        false
                    )
                ) return@Hook
                if (author.isBot || rMsg.isWebhook && settings.getBool("ignoreBots", true)) return@Hook
                val result = msg.lowercase()
                it.result = result.dropLastWhile { c -> c == '.' || c == '​' }
            } catch (h: Exception) {
                logger.warn("something went derp, oh well: ", h)
            }
        })
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}