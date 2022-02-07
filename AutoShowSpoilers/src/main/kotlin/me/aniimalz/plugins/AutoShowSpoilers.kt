package me.aniimalz.plugins

import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.InsteadHook
import com.aliucord.patcher.after
import com.discord.utilities.textprocessing.node.SpoilerNode
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemAttachment
import com.discord.widgets.chat.list.entries.AttachmentEntry

@AliucordPlugin
class AutoShowSpoilers : Plugin() {
    override fun start(ctx: Context) {
        patcher.after<SpoilerNode<*>>("isRevealed") { this.isRevealed = true }

        // Embed spoiler patch
        with(WidgetChatListAdapterItemAttachment.Model::class.java) {
            val rev = getDeclaredMethod("isSpoilerEmbedRevealed", AttachmentEntry::class.java)
            rev.apply { isAccessible = true }
            patcher.patch(rev, InsteadHook { return@InsteadHook true })
        }
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
    }
}