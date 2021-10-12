package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import com.aliucord.Http
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.patcher.InsteadHook
import com.discord.api.role.GuildRole
import com.discord.models.message.Message
import com.discord.models.user.CoreUser
import com.discord.stores.StoreStream
import com.discord.stores.StoreStream.*
import com.discord.utilities.guildmember.GuildMemberUtilsKt
import com.discord.utilities.guilds.RoleUtils
import com.discord.utilities.icon.IconUtils
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import external.org.apache.commons.lang3.reflect.MemberUtils

@AliucordPlugin
class Vector : Plugin() {
    @SuppressLint("SetTextI18n")
    override fun start(ctx: Context) {
        patcher.patch(
            Message::class.java.getDeclaredMethod("getTts"),
            InsteadHook.returnConstant(true)
        )

        with(IconUtils::class.java) {
            patcher.patch(getDeclaredMethod("setIcon", ImageView::class.java, String::class.java, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, Boolean::class.javaPrimitiveType), InsteadHook {
                val img = it.args[0] as ImageView
                img.setImageURI(Uri.parse("https://cdn.discordapp.com/attachments/811255667469582420/897530531632267264/1634058346931.png"))
            })
            methods.forEach {
                if (it.parameterTypes.contains(ImageView::class.java)) {
                    patcher.patch(getDeclaredMethod(it.name, *it.parameterTypes), InsteadHook { cf ->
                        val img = cf.args[0] as ImageView
                        img.setImageURI(Uri.parse("https://cdn.discordapp.com/attachments/811255667469582420/897530531632267264/1634058346931.png"))
                    })
                }
            }
        }
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}