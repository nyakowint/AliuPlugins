package me.aniimalz.plugins

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.NotificationsAPI
import com.aliucord.entities.NotificationData
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.utils.MDUtils
import com.aliucord.wrappers.messages.AttachmentWrapper.Companion.url
import com.discord.api.message.reaction.MessageReactionUpdate
import com.discord.models.user.CoreUser
import com.discord.stores.StoreMessageReactions
import com.discord.stores.StoreStream
import com.lytefast.flexinput.R

@AliucordPlugin
class ReactionNotifier : Plugin() {
    init {
        settingsTab = SettingsTab(
            PluginSettings::class.java,
            SettingsTab.Type.PAGE
        ).withArgs(settings)
    }

    var pluginIcon: Drawable? = null

    override fun start(ctx: Context) {
        pluginIcon = ContextCompat.getDrawable(Utils.appContext, R.e.ic_reaction_24dp)
        patcher.after<StoreMessageReactions>("handleReactionAdd", MessageReactionUpdate::class.java) {
            if (!settings.getBool("notifyAdd", true)) return@after
            handleReaction(Utils.appContext, (it.args[0] as MessageReactionUpdate), false)
        }

        patcher.after<StoreMessageReactions>("handleReactionRemove", MessageReactionUpdate::class.java) {
            if (!settings.getBool("notifyRemove", false)) return@after
            handleReaction(Utils.appContext, (it.args[0] as MessageReactionUpdate), true)
        }
    }

    private fun handleReaction(ctx: Context, reactionData: MessageReactionUpdate, removed: Boolean) {
        val msg = StoreStream.getMessages().getMessage(reactionData.a(), reactionData.c()) ?: return
        if (msg.channelId == StoreStream.getChannelsSelected().id) return
        if (StoreStream.getUsers().me.id != CoreUser(msg.author).id || StoreStream.getUsers().me.id == reactionData.d()) return
        val user = StoreStream.getUsers().users[reactionData.d()] ?: return
        if (user.isBot && settings.getBool("ignoreBots", false)) return

        val notif = NotificationData().apply {
            iconUrl = "https://cdn.discordapp.com/avatars/${user.id}/${user.avatar}.png"
            title = when (removed) {
                false -> MDUtils.render("${user.username} **reacted** with ${reactionData.b().d()}")
                true -> MDUtils.render("${user.username} **unreacted** to ${reactionData.b().d()}")
            }
            if (msg.hasAttachments()) attachmentUrl = msg.attachments.first().url
            body = MDUtils.render(msg.content)
            iconTopRight = ContextCompat.getDrawable(ctx, R.e.ic_guild_settings_24dp)
            setOnClickTopRightIcon { Utils.openPageWithProxy(Utils.appActivity, PluginSettings(settings)) }
            setOnClick {
                val comp = (StoreStream.Companion).messagesLoader
                comp.jumpToMessage(msg.channelId, msg.id)
            }
        }
        if (settings.getBool("notifyAdd", true) && !removed) NotificationsAPI.display(notif)
        else if (settings.getBool("notifyRemove", false) && removed) NotificationsAPI.display(notif)
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}