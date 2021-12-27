package me.aniimalz.plugins

import android.content.Context
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.Utils.showToast
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.patcher.InsteadHook
import com.aliucord.patcher.instead
import com.aliucord.utils.ReflectUtils
import com.discord.api.role.GuildRole

@AliucordPlugin(requiresRestart = true)
class RoleMembers : Plugin() {

    override fun start(ctx: Context) {
        // Add compatibility with GuildProfiles roles page
        if (PluginManager.isPluginEnabled("GuildProfiles")) {
            val gpRolesAdapter =
                PluginManager.plugins["GuildProfiles"]?.javaClass?.classLoader?.loadClass("xyz.wingio.plugins.guildprofiles.pages.ServerRolesPage\$RolesAdapter")
            // i LOVE reimplementing other plugins logic lol :husk:
            patcher.patch(gpRolesAdapter!!.getDeclaredMethod("onRoleClicked", GuildRole::class.java), InsteadHook {
                val role = it.args[0] as GuildRole
                try {
                    val sp = PluginManager.plugins["ShowPerms"]
                    ReflectUtils.invokeMethod(sp!!.javaClass, sp, "openPermViewer", role, ctx)
                } catch (e: Throwable) {
                    showToast(role.g(), false)
                }
            })

        }
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}