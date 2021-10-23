package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.aliucord.Logger
import com.aliucord.PluginManager
import com.aliucord.Utils
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
import kotlin.system.exitProcess


@AliucordPlugin
class FreeNitroll : Plugin() {

    private var tortureMe = false

    private val logger: Logger = Logger("FreeNitro")

    @SuppressLint("SetTextI18n")
    override fun start(ctx: Context) {
        commands.registerCommand("nitro", "Free nitro! Totally not a scam 10% legit") {
            if (StoreStream.getUsers().me.premiumTier != PremiumTier.NONE) {
                patcher.unpatchAll()
                CommandsAPI.CommandResult(
                    "The FreeNitro plugin works!!!! Look at my profile badge for proof ;) Thanks Aliucord discord server! You guys are okay in my book! ☺ ❤",
                    null,
                    true
                )
            } else {
                patcher.unpatchAll()
                Utils.openPage(Utils.appActivity, WidgetFreeNitro::class.java)
                CommandsAPI.CommandResult(
                    "Im extremely goofy for installing freenitro lol, i am not epic haxor i canot get nitro fo free... What an epic realization! <:posttroll:859798445870809108> <:iloveccp:892176962934689822><:iloveccp:892176962934689822> \uD83C\uDDE8\uD83C\uDDF3",
                    null,
                    true
                )
            }
        }

        commands.registerCommand("tortureme", "Use this to turn the free nitro shit back on") {
            patcher.unpatchAll()
            tortureMe = true
            epicTrolling()
            Utils.showToast("You fool...")
            Utils.mainThread.run {
                try {
                    ContextCompat.startActivity(
                        it.context,
                        Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:vIefYWT7jNo")),
                        null
                    )
                } catch (ex: ActivityNotFoundException) {
                    ContextCompat.startActivity(
                        it.context,
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/watch?v=vIefYWT7jNo")
                        ),
                        null
                    )
                }
                exitProcess(0)
            }
        }
        epicTrolling()
    }

    @SuppressLint("SetTextI18n")
    private fun epicTrolling() {
        var dont = false
        if (StoreStream.getUsers().me.premiumTier == PremiumTier.NONE) {
            if (dont && !tortureMe || listOf(118437263754395652L).contains(
                    StoreStream.getUsers().me.id
                ) && !tortureMe
            )
                Utils.openPageWithProxy(
                    Utils.appActivity,
                    ReflectUtils.invokeConstructorWithArgs(WidgetFreeNitro::class.java)
                )
        }
        patcher.patch(Message::class.java.getDeclaredMethod("getContent"), Hook {
            if (dont && !tortureMe || listOf(118437263754395652L).contains(
                    StoreStream.getUsers().me.id
                ) && !tortureMe
            ) {
                patcher.unpatchAll()
                dont = true
                return@Hook
            }
            val msg = it.thisObject as Message
            val msgContent = (ReflectUtils.getField(msg, "content") as String)
            it.result =
                if (msgContent.lowercase().contains("sussy")) msgContent else "$msgContent sussy"
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
                if (dont && !tortureMe || listOf(118437263754395652L).contains(
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
            patcher.patch(
                getDeclaredMethod(
                    "setIcon",
                    ImageView::class.java,
                    String::class.java,
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    Boolean::class.javaPrimitiveType
                ), InsteadHook {
                    val img = it.args[0] as ImageView
                    img.setImageURI(Uri.parse("https://cdn.discordapp.com/attachments/811255667469582420/897530531632267264/1634058346931.png"))
                })
            methods.forEach {
                if (it.parameterTypes.contains(ImageView::class.java)) {
                    patcher.patch(
                        getDeclaredMethod(it.name, *it.parameterTypes),
                        InsteadHook { cf ->
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
