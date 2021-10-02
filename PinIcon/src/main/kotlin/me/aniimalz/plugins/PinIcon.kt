package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PinePatchFn
import com.discord.models.message.Message
import com.discord.models.user.User
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import top.canyie.pine.Pine

@AliucordPlugin
class PinIcon : Plugin() {
    @SuppressLint("SetTextI18n")
    override fun start(ctx: Context) {
        with(WidgetChatListAdapterItemMessage::class.java){
            patcher.patch(getDeclaredMethod("configureItemTag", Message::class.java), PinePatchFn {
                cf: Pine.CallFrame ->
                val msg = cf.args[0] as Message
                (msg.author as User).isBot
            })
        }
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
    }
}