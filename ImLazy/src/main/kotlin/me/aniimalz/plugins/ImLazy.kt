package me.aniimalz.plugins

import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin

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