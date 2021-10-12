package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.aliucord.PluginManager
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.patcher.InsteadHook
import com.aliucord.utils.ReflectUtils
import com.discord.api.premium.PremiumTier
import com.discord.databinding.WidgetChatListAdapterItemEphemeralMessageBinding
import com.discord.models.message.Message
import com.discord.stores.StoreStream
import com.discord.utilities.icon.IconUtils
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemEphemeralMessage
import com.discord.widgets.chat.list.entries.ChatListEntry

@AliucordPlugin
class FreeNitro : Plugin() {

    private var tortureMe = false

    @SuppressLint("SetTextI18n")
    override fun start(ctx: Context) {

        commands.registerCommand("nitro", "Free nitro! Totally not a scam 10% legit") {
            if (StoreStream.getUsers().me.premiumTier != PremiumTier.NONE) {
                patcher.unpatchAll()
                CommandsAPI.CommandResult(
                    "I installed the FreeNitro plugin and it worked exactly as advertised! Everything worked great without a hitch and I'm quite satisfied! Thanks Aliucord discord server! You guys are okay in my book! ☺ ❤",
                    null,
                    true
                )
            } else {
                patcher.unpatchAll()
                CommandsAPI.CommandResult(
                    "I installed the FreeNitro plugin probably expecting nitro features for free, but now know that such things are not possible! What an epic realization <:posttroll:859798445870809108> <:iloveccp:892176962934689822><:iloveccp:892176962934689822> \uD83C\uDDE8\uD83C\uDDF3",
                    null,
                    true
                )
            }
        }

        commands.registerCommand("tortureme", "Use this to turn the free nitro shit back on") {
            patcher.unpatchAll()
            tortureMe = true
            epicTrolling()
            CommandsAPI.CommandResult("re-enabled the torture, you're welcome.", null, false)
        }
        epicTrolling()
    }

    @SuppressLint("SetTextI18n")
    private fun epicTrolling() {
        var dont = false
        patcher.patch(Message::class.java.getDeclaredMethod("getContent"), Hook {
            if (dont && !tortureMe || listOf(118437263754395652L, 289556910426816513L).contains(
                    StoreStream.getUsers().me.id
                ) && !tortureMe
            ) {
                patcher.unpatchAll()
                dont = true
                return@Hook
            }
            val msg = it.thisObject as Message
            val msgContent = (ReflectUtils.getField(msg, "content") as String)
            it.result = if (msgContent.lowercase().contains("sussy")) msgContent else "$msgContent sussy"
        })
        patcher.patch(
            Message::class.java.getDeclaredMethod("getMentionEveryone"),
            InsteadHook.returnConstant(true)
        )
        patcher.patch(
            Message::class.java.getDeclaredMethod("getTts"),
            InsteadHook.returnConstant(true)
        )

        val ephBinding =
            WidgetChatListAdapterItemEphemeralMessage::class.java.getDeclaredField("binding")
                .apply { isAccessible = true }

        patcher.patch(
            WidgetChatListAdapterItemEphemeralMessage::class.java.getDeclaredMethod(
                "onConfigure",
                Int::class.javaPrimitiveType,
                ChatListEntry::class.java
            ), Hook {
                if (dont && !tortureMe || listOf(118437263754395652L, 289556910426816513L).contains(
                        StoreStream.getUsers().me.id
                    ) && !tortureMe
                ) {
                    patcher.unpatchAll()
                    dont = true
                    return@Hook
                }
                val binding =
                    ephBinding.get(it.thisObject) as WidgetChatListAdapterItemEphemeralMessageBinding
                binding.e.text =
                    "you are incredibly stupid for installing the free nitro plugin. Did you really think you could get nitro for free? LMAO ok now shut up and go work or something idk (use /nitro to remove this)"
                if (StoreStream.getUsers().me.id == 587156686612201482L) binding.e.text = "Moulz"
            })
        patcher.patch(
            Message::class.java.getDeclaredMethod("isEphemeralMessage"),
            InsteadHook.returnConstant(true)
        )

        with(IconUtils::class.java) {
            if (PluginManager.isPluginEnabled("Vector")) return@with
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
        commands.unregisterAll()
        patcher.unpatchAll()
    }
}
