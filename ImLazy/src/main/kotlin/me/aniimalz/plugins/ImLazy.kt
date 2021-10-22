package me.aniimalz.plugins

import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.PluginManager
import com.aliucord.api.CommandsAPI
import com.aliucord.api.CommandsAPI.CommandResult

@AliucordPlugin
class ImLazy : Plugin() {
    override fun start(ctx: Context) {
        commands.registerCommand("rpl", "Restart all plugins", listOf()) {
            for (p: Plugin in PluginManager.plugins.values) {
                PluginManager.remountPlugin(p.name)
            }
            CommandsAPI.CommandResult("Restarted all plugins.", null, false)
        }
    }

    override fun stop(context: Context) {
        commands.unregisterAll()
    }
}