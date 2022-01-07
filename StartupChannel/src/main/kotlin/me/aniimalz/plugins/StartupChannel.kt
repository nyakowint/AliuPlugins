package me.aniimalz.plugins

import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.wrappers.ChannelWrapper.Companion.lastMessageId
import com.discord.stores.StoreStream

@AliucordPlugin
class StartupChannel : Plugin() {
    init {
        settingsTab = SettingsTab(
            BottomShit::class.java,
            SettingsTab.Type.BOTTOM_SHEET
        ).withArgs(settings)
    }

    override fun start(ctx: Context) {
        val channel = settings.getLong("selectedChannel", 0L).takeIf { it != 0L }
        try {
            (StoreStream.Companion).messagesLoader.jumpToMessage(
                channel ?: return,
                StoreStream.getChannels().getChannel(channel).lastMessageId
            )
        } catch (t: Throwable) {
            logger.error(t)
        }
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}