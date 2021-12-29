package me.aniimalz.plugins

import android.content.Context
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI
import com.aliucord.entities.Plugin
import com.discord.api.commands.ApplicationCommandType

@AliucordPlugin
class RoleMembers : Plugin() {

    override fun start(ctx: Context) {

        val roleOption = Utils.createCommandOption(
            ApplicationCommandType.ROLE, "role", "The role to list members from", null,
            required = true, default = true
        )
        commands.registerCommand("rolemembers", "show all members in a given role", roleOption) {
            val role = it.getRequiredRole("role")
            Utils.openPageWithProxy(Utils.appActivity, RoleMembersPage(role.raw(), it.currentChannel.guildId))
            CommandsAPI.CommandResult()
        }
    }

    override fun stop(ctx: Context) {
        rmSub?.unsubscribe()
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}