package me.aniimalz.plugins

import android.content.Context
import com.aliucord.Logger
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.patcher.InsteadHook
import com.aliucord.patcher.before
import com.discord.utilities.textprocessing.node.SpoilerNode
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemAttachment
import com.discord.widgets.chat.list.entries.AttachmentEntry

@AliucordPlugin
class AutoShowSpoilers : Plugin() {
    private val logger = Logger("AutoShowSpoilers")
    override fun start(ctx: Context) {
        with(SpoilerNode::class.java) {
            patcher.patch(getDeclaredMethod("isRevealed"), Hook {
                try {
                    val spoilerNode = it.thisObject as SpoilerNode<*>
                    spoilerNode.apply { isRevealed = true }
                } catch (e: Throwable) {
                    logger.error(e)
                }
            })
        }

        with(WidgetChatListAdapterItemAttachment.Model::class.java) {
            val isRevealed = getDeclaredMethod("isSpoilerEmbedRevealed", AttachmentEntry::class.java)
            isRevealed.apply { isAccessible = true }
            patcher.patch(isRevealed, InsteadHook {
                return@InsteadHook true
            })
        }
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
    }
}