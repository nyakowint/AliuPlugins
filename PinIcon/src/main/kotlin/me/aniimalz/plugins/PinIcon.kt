package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.discord.models.message.Message
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import com.lytefast.flexinput.R
import de.robv.android.xposed.XC_MethodHook

@AliucordPlugin
class PinIcon : Plugin() {
    private var pinIcon: Drawable? = null

    @SuppressLint("SetTextI18n")
    override fun start(ctx: Context) {
        pinIcon = ContextCompat.getDrawable(Utils.appContext, R.e.ic_sidebar_pins_off_light_24dp)

        val itemTimestampField = WidgetChatListAdapterItemMessage::class.java.getDeclaredField(
            "itemTimestamp"
        ).apply { isAccessible = true }

        with(WidgetChatListAdapterItemMessage::class.java) {
            patcher.patch(
                getDeclaredMethod(
                    "configureItemTag",
                    Message::class.java
                ), Hook { cf: XC_MethodHook.MethodHookParam ->
                    try {
                        val msg = cf.args[0] as Message
                        @Suppress("SENSELESS_COMPARISON") // kys android
                        if (msg != null && msg.pinned) {
                            val textView = itemTimestampField.get(cf.thisObject) as TextView
                            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                pinIcon,
                                null,
                                null,
                                null
                            )
                            textView.setOnClickListener {
                                Utils.showToast("This message is pinned")
                            }
                        } else {
                            val textView =
                                itemTimestampField.get(cf.thisObject) as TextView? ?: return@Hook
                            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                null,
                                null,
                                null,
                                null
                            )
                        }
                    } catch (t: Throwable) {
                        //
                    }
                })
        }
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
    }
}
