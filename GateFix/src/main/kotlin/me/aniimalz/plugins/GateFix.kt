package me.aniimalz.plugins

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.discord.databinding.WidgetChatInputBinding
import com.discord.widgets.chat.input.ChatInputViewModel
import com.discord.widgets.chat.input.WidgetChatInput

@AliucordPlugin
class GateFix : Plugin() {
    override fun start(ctx: Context) {

        val bindingMethod = WidgetChatInput::class.java.getDeclaredMethod("getBinding")
            .apply { isAccessible = true }
        patcher.patch(
            WidgetChatInput::class.java.getDeclaredMethod(
                "configureChatGuard",
                ChatInputViewModel.ViewState.Loaded::class.java
            ), Hook {

                val loaded = it.args[0] as ChatInputViewModel.ViewState.Loaded

                if (!loaded.shouldShowVerificationGate || !loaded.shouldShowFollow) {
                    val binding = bindingMethod(it.thisObject) as WidgetChatInputBinding

                    val chatWrap = binding.root.findViewById<LinearLayout>(
                        Utils.getResId(
                            "chat_input_wrap", "id"
                        )
                    )
                    val gateButtonLayout = binding.root.findViewById<RelativeLayout>(
                        Utils.getResId(
                            "guard_member_verification", "id"
                        )
                    )

                    gateButtonLayout.visibility = View.GONE
                    chatWrap.visibility = View.VISIBLE
                }
            })
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}