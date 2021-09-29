package me.aniimalz.quoter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import com.aliucord.Constants
import com.aliucord.Main.logger
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PinePatchFn
import com.aliucord.wrappers.ChannelWrapper.Companion.id
import com.discord.databinding.WidgetChatListActionsBinding
import com.discord.stores.StoreStream
import com.discord.utilities.color.ColorCompat
import com.discord.utilities.permissions.PermissionUtils
import com.discord.utilities.user.UserUtils
import com.discord.widgets.chat.input.AppFlexInputViewModel

import com.discord.widgets.chat.list.actions.WidgetChatListActions
import com.lytefast.flexinput.R
import com.lytefast.flexinput.fragment.`FlexInputFragment$c`
import com.lytefast.flexinput.widget.FlexEditText
import top.canyie.pine.Pine

@AliucordPlugin
class Quoter : Plugin() {
    private var textInput: FlexEditText? = null
    private var textBox: AppFlexInputViewModel? = null

    @SuppressLint("SetTextI18n")
    override fun start(context: Context) {
        // thanks zt and ven
        patcher.patch(
            `FlexInputFragment$c`::class.java.getDeclaredMethod(
                "invoke",
                Object::class.java
            ), PinePatchFn {
                textInput = (it.result as c.b.a.e.a).root.findViewById(R.e.text_input)
            })
        val icon = ContextCompat.getDrawable(context, R.d.ic_quote_white_a60_24dp)
        val quoteId = View.generateViewId()

        with(WidgetChatListActions::class.java, {
            val getBinding = getDeclaredMethod("getBinding").apply { isAccessible = true }

            patcher.patch(
                getDeclaredMethod("configureUI", WidgetChatListActions.Model::class.java),
                PinePatchFn { yes: Pine.CallFrame ->
                    try {
                        val msg = (yes.args[0] as WidgetChatListActions.Model).message
                        val binding =
                            getBinding.invoke(yes.thisObject) as WidgetChatListActionsBinding
                        val channel = (yes.args[0] as WidgetChatListActions.Model).channel
                        val quoteButton =
                            binding.a.findViewById<TextView>(quoteId).apply {
                                visibility = if (PermissionUtils.INSTANCE.hasAccessWrite(
                                        channel,
                                        StoreStream.getPermissions().permissionsByChannel[channel.id]
                                    )
                                ) View.VISIBLE else View.GONE
                            }
                        quoteButton.setOnClickListener {
                            (yes.thisObject as WidgetChatListActions).dismiss()
                            try {
                                if (textInput == null) {
                                    logger.error(
                                        context,
                                        "FlexEditText textInput == null, sad"
                                    )

                                    return@setOnClickListener Utils.showToast(
                                        context,
                                        "Guess your device doesn't like quoting :shrug:"
                                    )
                                }
                                textBox?.focus()
                                val inputBox = textInput as FlexEditText
                                if (msg.content.contains("\n")) {
                                    inputBox.setText(
                                        "> ${
                                            msg.content.replace(
                                                "\n",
                                                "\n> "
                                            )
                                        }\n@${msg.author.r()}#${msg.author.f()}"
                                    )
                                    textInput!!.text?.let { it1 -> inputBox.setSelection(it1.lastIndex) }
                                } else {
                                    inputBox.setText("> ${msg.content}\n@${msg.author.r()}#${msg.author.f()}")
                                    textInput!!.text?.let { it1 -> inputBox.setSelection(it1.lastIndex) }
                                }
                            } catch (bruh: Throwable) {
                                logger.error(bruh)
                            }
                        }
                    } catch (ignore: Throwable) {
                        Utils.showToast(context, ignore.message)
                        logger.error(ignore)
                    }
                })
            patcher.patch(
                getDeclaredMethod("onViewCreated", View::class.java, Bundle::class.java),
                PinePatchFn { yes: Pine.CallFrame ->
                    val linearLayout =
                        (yes.args[0] as NestedScrollView).getChildAt(0) as LinearLayout
                    val ctx = linearLayout.context

                    icon?.setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal))

                    val quote = TextView(ctx, null, 0, R.h.UiKit_Settings_Item_Icon).apply {
                        text = "Quote"
                        id = quoteId
                        setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null)
                        typeface = ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_semibold)
                    }

                    linearLayout.addView(quote, 1)
                })

            patcher.patch(
                AppFlexInputViewModel::class.java.getDeclaredMethod(
                    "onInputTextChanged",
                    String::class.java,
                    Boolean::class.javaObjectType
                ), PinePatchFn {
                    textBox = it.thisObject as AppFlexInputViewModel
                })

        })
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
    }

}