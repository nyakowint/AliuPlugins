package me.aniimalz.plugins

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PreHook
import com.aliucord.patcher.after
import com.aliucord.utils.ReflectUtils
import com.discord.models.member.GuildMember
import com.discord.stores.StoreStream
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemBlocked
import com.discord.widgets.chat.list.entries.ChatListEntry
import com.discord.widgets.chat.overlay.`ChatTypingModel$Companion$getTypingUsers$1$1`
import com.lytefast.flexinput.R
import java.util.stream.Collectors

@AliucordPlugin
class HideBlockedMessages : Plugin() {
    override fun start(ctx: Context) {
        Int::class.javaPrimitiveType?.let {
            patcher.after<WidgetChatListAdapterItemBlocked>(
                "onConfigure",
                it,
                ChatListEntry::class.java
            ) {
                try {
                    (ReflectUtils.getField(this, "binding") as View).apply {
                        visibility = View.GONE
                        layoutParams = ViewGroup.LayoutParams(0, 0)
                    }
                } catch (ignored: Throwable) {
                    // wow no one cares
                }
            }
        }
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}