package me.aniimalz.plugins

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI
import com.aliucord.entities.Plugin
import com.discord.api.commands.ApplicationCommandType
import com.discord.api.role.GuildRole
import com.lytefast.flexinput.R

@AliucordPlugin
class RoleMembers : Plugin() {

    var pluginIcon: Drawable? = null

    override fun start(ctx: Context) {
        pluginIcon = ContextCompat.getDrawable(Utils.appContext, R.e.ic_role_24dp)
        val roleOption = Utils.createCommandOption(
            ApplicationCommandType.ROLE, "role", "The role to list members from", null,
            required = true, default = true
        )
        commands.registerCommand("rolemembers", "show all members in a given role", roleOption) {
            val role = it.getRequiredRole("role")
            showRoleMembers(Utils.appActivity, role.raw(), it.currentChannel.guildId)
            CommandsAPI.CommandResult()
        }
    }

    fun showRoleMembers(context: Context, role: GuildRole, guildId: Long) {
        Utils.openPageWithProxy(context, RoleMembersPage(role, guildId))
    }

    override fun stop(ctx: Context) {
        rmSub?.unsubscribe()
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}