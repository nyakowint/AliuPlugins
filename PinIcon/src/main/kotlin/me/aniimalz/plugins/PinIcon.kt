package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.widgets.BottomSheet
import com.discord.models.message.Message
import com.discord.views.CheckedSetting
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import com.lytefast.flexinput.R
import de.robv.android.xposed.XC_MethodHook

@AliucordPlugin
class PinIcon : Plugin() {

    private var pinIcon: Drawable? = null

    init {
        settingsTab = SettingsTab(PS::class.java, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings)
    }

    @SuppressLint("SetTextI18n")
    override fun start(ctx: Context) {
        pinIcon = ContextCompat.getDrawable(ctx, R.e.ic_sidebar_pins_off_light_24dp)

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
                        if (msg.pinned && settings.getBool("show", true) == true) {
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
                        logger.error(t)
                    }
                })
        }
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
    }
}

class PS(private val settings: SettingsAPI) : BottomSheet() {
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        val ctx = requireContext()
        addView(
            Utils.createCheckedSetting(
                ctx,
                CheckedSetting.ViewType.SWITCH,
                "Show pin icon",
                null
            ).apply {
                isChecked = settings.getBool("show", true)
                setOnCheckedListener { settings.setBool("show", it) }
            })
    }
}
