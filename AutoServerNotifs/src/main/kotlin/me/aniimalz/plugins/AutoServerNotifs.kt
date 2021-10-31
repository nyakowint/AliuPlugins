package me.aniimalz.plugins

import android.content.Context
import com.aliucord.Logger
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.utils.RxUtils.subscribe
import com.aliucord.wrappers.GuildWrapper
import com.discord.api.guild.Guild
import com.discord.restapi.RestAPIParams
import com.discord.stores.StoreGuilds
import com.discord.utilities.rest.RestAPI

@AliucordPlugin
class AutoServerNotifs : Plugin() {
    val logger = Logger("AutoServerNotifs")
    init {
        settingsTab = SettingsTab(
            PluginSettings::class.java,
            SettingsTab.Type.PAGE
        ).withArgs(settings)
    }

    override fun start(ctx: Context) {
        pain = settings

        // RestAPI.ackGuild - mark as read
        patcher.patch(
            StoreGuilds::class.java.getDeclaredMethod(
                "handleGuildAdd",
                Guild::class.java
            ), Hook {
                Utils.showToast("handleGuildAdd")
                val guild = GuildWrapper(it.args[0] as Guild)
                Utils.showToast("${guild.name} (${guild.id}) joined")
                Utils.threadPool.execute {
                    RestAPI.api.updateUserGuildSettings(
                        guild.id,
                        RestAPIParams.UserGuildSettings(
                            getBool("suppressEveryone"),
                            getBool("suppressRoles"),
                            getBool("Mute Guild"),
                            null,
                            getBool("mobilePushNotifs", true),
                            1,
                            null
                        )
                    ).subscribe {}
                }

            })

    }

    private fun getBool(setting: String, default: Boolean = false): Boolean {
        return pain.getBool(setting, default)
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
    }

    companion object {
        lateinit var pain: SettingsAPI
    }
}

