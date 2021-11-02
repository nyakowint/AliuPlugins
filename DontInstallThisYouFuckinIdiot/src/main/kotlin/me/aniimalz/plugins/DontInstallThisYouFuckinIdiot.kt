package me.aniimalz.plugins

import android.content.Context
import android.net.Uri
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.aliucord.Constants
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI
import com.aliucord.api.PatcherAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.patcher.InsteadHook
import com.aliucord.patcher.PreHook
import com.discord.models.message.Message
import com.discord.utilities.icon.IconUtils
import com.discord.widgets.channels.memberlist.adapter.ChannelMembersListAdapter
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Member

@AliucordPlugin
class DontInstallThisYouFuckinIdiot : Plugin() {
    override fun start(ctx: Context) {
        var isInSupport = false
        Utils.showToast("good job idiot, you installed it.")
        patcher.patch(
            Utils::class.java.getDeclaredMethod(
                "openPageWithProxy",
                Context::class.java,
                Fragment::class.java
            ), InsteadHook.DO_NOTHING
        )


        patcher.patch(
            TextView::class.java.getDeclaredMethod("setText", CharSequence::class.java),
            PreHook {
                if (isInSupport) return@PreHook
                try {
                    XposedBridge.invokeOriginalMethod(
                        it.method, it.thisObject,
                        arrayOf(String(StringBuilder("husk")))
                    )
                    it.result = null
                } catch (t: Throwable) {
                    //
                }
            })

        with(ImageView::class.java) {
            patcher.patch(getDeclaredMethod("setImageURI", Uri::class.java), PreHook {
                if (isInSupport) return@PreHook
                try {
                    XposedBridge.invokeOriginalMethod(
                        it.method,
                        it.thisObject,
                        arrayOf(Uri.parse("https://cdn.discordapp.com/emojis/859796756111294474.png"))
                    )
                    it.result = null
                } catch (t: Throwable) {
                    //
                }
            })


            patcher.patch(Message::class.java.getDeclaredMethod("getContent"), Hook {
                val msg = it.thisObject as Message
                if (msg.channelId == Constants.SUPPORT_CHANNEL_ID || msg.channelId == Constants.PLUGIN_SUPPORT_CHANNEL_ID) {
                    isInSupport = true
                    return@Hook
                }
                isInSupport = false
                it.result = "husk"
            })

            patcher.patch(
                ChannelMembersListAdapter.Item.Member::class.java.getDeclaredMethod("getName"),
                PreHook {
                    it.result = "husk"
                })

            patcher.patch(
                TextView::class.java.getDeclaredMethod("getTextSize"),
                InsteadHook.returnConstant(100f)
            )

            patcher.patch(
                TextView::class.java.getDeclaredMethod("getTypeface"),
                InsteadHook.returnConstant(
                    ResourcesCompat.getFont(
                        ctx,
                        Constants.Fonts.roboto_medium_numbers
                    )
                )
            )

            patcher.patch(
                TextView::class.java.getDeclaredMethod("getLetterSpacing"),
                InsteadHook.returnConstant(3f)
            )

            patcher.patch(
                TextView::class.java.getDeclaredMethod("isAllCaps"),
                InsteadHook.returnConstant(true)
            )

            patcher.patch(
                TextView::class.java.getDeclaredMethod("getGravity"),
                InsteadHook.returnConstant(Gravity.TOP)
            )

            with(IconUtils::class.java) {

                patcher.patch(
                    getDeclaredMethod(
                        "getForGuild",
                        Long::class.javaObjectType,
                        String::class.javaObjectType,
                        String::class.javaObjectType,
                        Boolean::class.java,
                        Int::class.javaObjectType
                    ), PreHook {
                        it.result = "https://cdn.discordapp.com/emojis/859796756111294474.png"
                    })

                patcher.patch(
                    getDeclaredMethod(
                        "getBannerForGuild",
                        Long::class.javaObjectType,
                        String::class.java,
                        Int::class.javaObjectType
                    ), PreHook {
                        it.result = "https://cdn.discordapp.com/emojis/859796756111294474.png"
                    })

                patcher.patch(
                    getDeclaredMethod(
                        "getForUser",
                        Long::class.javaObjectType,
                        String::class.javaObjectType,
                        Int::class.javaObjectType,
                        Boolean::class.java
                    ), PreHook {
                        it.result = "https://cdn.discordapp.com/emojis/859796756111294474.png"
                    })

                patcher.patch(
                    getDeclaredMethod(
                        "getForUserBanner",
                        Long::class.javaPrimitiveType,
                        String::class.javaObjectType,
                        Int::class.javaObjectType,
                        Boolean::class.javaPrimitiveType
                    ), PreHook {
                        it.result = "https://cdn.discordapp.com/emojis/859796756111294474.png"
                    })


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
                        img.setImageURI(Uri.parse("https://cdn.discordapp.com/emojis/859796756111294474.png"))
                    })

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
                        img.setImageURI(Uri.parse("https://cdn.discordapp.com/emojis/859796756111294474.png"))
                    })
                methods.forEach {
                    if (it.parameterTypes.contains(ImageView::class.java)) {
                        patcher.patch(
                            getDeclaredMethod(it.name, *it.parameterTypes),
                            InsteadHook { cf ->
                                val img = cf.args[0] as ImageView
                                img.setImageURI(Uri.parse("https://cdn.discordapp.com/emojis/859796756111294474.png"))
                            })
                    }
                }
            }



            patcher.patch(
                ImageView::class.java.getDeclaredMethod("setImageURI", Uri::class.java),
                InsteadHook {
                    it.result =
                        Uri.parse("https://cdn.discordapp.com/emojis/859796756111294474.png")
                    return@InsteadHook Uri.parse("https://cdn.discordapp.com/emojis/859796756111294474.png")
                })

            patcher.patch(
                PatcherAPI::class.java.getDeclaredMethod(
                    "patch",
                    String::class.java,
                    String::class.java,
                    arrayOf(Class::class.java)::class.java,
                    XC_MethodHook::class.java
                ), InsteadHook.DO_NOTHING
            )
            patcher.patch(
                PatcherAPI::class.java.getDeclaredMethod(
                    "patch",
                    Class::class.java,
                    String::class.java,
                    arrayOf(Class::class.java)::class.java,
                    XC_MethodHook::class.java
                ), InsteadHook.DO_NOTHING
            )
            patcher.patch(
                PatcherAPI::class.java.getDeclaredMethod(
                    "patch",
                    Member::class.java,
                    XC_MethodHook::class.java
                ), InsteadHook.DO_NOTHING
            )
            patcher.patch(
                PatcherAPI::class.java.getDeclaredMethod("unpatchAll"),
                InsteadHook.DO_NOTHING
            )
            patcher.patch(
                CommandsAPI::class.java.getDeclaredMethod(
                    "unregisterCommand",
                    String::class.java
                ), InsteadHook.DO_NOTHING
            )
            patcher.patch(
                CommandsAPI::class.java.getDeclaredMethod("unregisterAll"),
                InsteadHook.DO_NOTHING
            )
        }
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
    }
}