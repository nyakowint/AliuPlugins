package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.aliucord.Logger
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PinePatchFn
import com.discord.models.message.Message
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import com.lytefast.flexinput.R
import top.canyie.pine.Pine.CallFrame

@AliucordPlugin
class PinIcon : Plugin() {

    private val logger: Logger = Logger("PinIcon")

    private var pluginIcon: Drawable? = null

    override fun load(ctx: Context) {
        pluginIcon = ContextCompat.getDrawable(ctx, R.d.ic_sidebar_pins_off_dark_24dp)
    }

    @SuppressLint("SetTextI18n")
    override fun start(ctx: Context) {
        pluginIcon = ContextCompat.getDrawable(ctx, R.d.ic_sidebar_pins_off_light_24dp)

        val itemTimestampField = WidgetChatListAdapterItemMessage::class.java.getDeclaredField(
            "itemTimestamp"
        ).apply { isAccessible = true }

        with(WidgetChatListAdapterItemMessage::class.java) {
            patcher.patch(
                getDeclaredMethod(
                    "configureItemTag",
                    Message::class.java
                ), PinePatchFn { cf: CallFrame ->
                    try {
                        val msg = cf.args[0] as Message
                        if (msg.pinned) {
                            val textView = itemTimestampField.get(cf.thisObject) as TextView
                            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                pluginIcon,
                                null,
                                null,
                                null
                            )
                            textView.setOnClickListener {
                                Utils.showToast(textView.context, "This message is pinned")
                            }
                        } else {
                            val textView = itemTimestampField.get(cf.thisObject) as TextView
                            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                null,
                                null,
                                null,
                                null
                            )
                        }
                    } catch (t: Throwable) {
                        logger.error(t)
                    }
                })
        }
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
    }
}
