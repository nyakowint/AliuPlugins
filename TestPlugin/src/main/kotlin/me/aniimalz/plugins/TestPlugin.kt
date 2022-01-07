package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import com.aliucord.Http
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.discord.stores.StoreStream
import com.discord.utilities.view.text.SimpleDraweeSpanTextView
import com.discord.widgets.chat.list.actions.WidgetChatListActions
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import com.discord.widgets.chat.list.entries.MessageEntry
import com.discord.widgets.guilds.WidgetGuildSelector

@AliucordPlugin
class TestPlugin : Plugin() {
    @SuppressLint("SetTextI18n")
    override fun start(ctx: Context) {
        // shut
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}


/* Open actions sheet on right click - only works in dex

        patcher.after<View>("onTouchEvent", MotionEvent::class.java) {
            val event = it.args[0] as MotionEvent
            if (event.action == MotionEvent.ACTION_DOWN && event.buttonState == MotionEvent.BUTTON_SECONDARY) {
                Utils.showToast("\$sendmsg benis")
                this.showContextMenu(event.x, event.y)
            }
        }

        patcher.after<WidgetChatListAdapterItemMessage>(
            "processMessageText",
            SimpleDraweeSpanTextView::class.java,
            MessageEntry::class.java
        ) {
            val tv = it.args[0] as View
            val msg = (it.args[1] as MessageEntry).message
            Utils.appActivity.runOnUiThread {
                tv.setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_DOWN && event.buttonState == MotionEvent.BUTTON_SECONDARY) {
                        (WidgetChatListActions.Companion).showForChat(
                            Utils.widgetChatList?.parentFragmentManager ?: Utils.appActivity.supportFragmentManager, msg.channelId, msg.id, "")
                        return@setOnTouchListener false
                    }
                    v.performClick()
                    return@setOnTouchListener false
                }
            }
        }
https://developer.android.com/training/gestures/movement
https://developer.samsung.com/samsung-dex/modify-optional.html
*/