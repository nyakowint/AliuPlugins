package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import c.b.a.e.a
import com.aliucord.Constants
import com.aliucord.Logger
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.widgets.BottomSheet
import com.aliucord.wrappers.ChannelWrapper.Companion.id
import com.discord.databinding.WidgetChatListActionsBinding
import com.discord.models.message.Message
import com.discord.stores.StoreStream
import com.discord.utilities.color.ColorCompat
import com.discord.utilities.permissions.PermissionUtils
import com.discord.views.CheckedSetting
import com.discord.widgets.chat.input.AppFlexInputViewModel
import com.discord.widgets.chat.list.actions.WidgetChatListActions
import com.lytefast.flexinput.R
import com.lytefast.flexinput.fragment.`FlexInputFragment$c`
import com.lytefast.flexinput.widget.FlexEditText
import de.robv.android.xposed.XC_MethodHook


@AliucordPlugin
class Quoter : Plugin() {
    init {
        settingsTab = SettingsTab(
            PluginSettings::class.java,
            SettingsTab.Type.BOTTOM_SHEET
        ).withArgs(settings)
    }

    private val logger: Logger = Logger("Quoter")

    private var textInput: FlexEditText? = null
    private var textBox: AppFlexInputViewModel? = null

    private var quoteIcon: Drawable? = null


    @SuppressLint("SetTextI18n")
    override fun start(context: Context) {
        quoteIcon = ContextCompat.getDrawable(context, R.e.ic_quote_white_a60_24dp)
        // thanks zt and ven
        patcher.patch(
            FlexEditText::class.java.getDeclaredMethod(
                "onCreateInputConnection",
                EditorInfo::class.java
            ), Hook {
                textInput = it.thisObject as FlexEditText
            })
        patcher.patch(
            `FlexInputFragment$c`::class.java.getDeclaredMethod(
                "invoke",
                Object::class.java
            ), Hook {
                textInput = (it.result as a).root.findViewById(R.f.text_input)
            })
        val quoteId = View.generateViewId()

        with(WidgetChatListActions::class.java, {
            val getBinding = getDeclaredMethod("getBinding").apply { isAccessible = true }

            patcher.patch(
                getDeclaredMethod("configureUI", WidgetChatListActions.Model::class.java),
                Hook { yes: XC_MethodHook.MethodHookParam ->
                    try {
                        val msg = (yes.args[0] as WidgetChatListActions.Model).message
                        val binding =
                            getBinding.invoke(yes.thisObject) as WidgetChatListActionsBinding
                        val channel = (yes.args[0] as WidgetChatListActions.Model).channel
                        val quoteButton =
                            binding.root.findViewById<TextView>(quoteId).apply {
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
                                    return@setOnClickListener logger.error(
                                        context,
                                        "Couldn't get text box. Redownloading the plugin/reinstalling may fix it. (This is a known issue lol)"
                                    )
                                }
                                textBox?.focus()
                                val inputBox = textInput as FlexEditText
                                if (settings.getBool("append", true)) {
                                    quoteAppend(inputBox, msg)
                                } else {
                                    quoteNormal(inputBox, msg)
                                }
                            } catch (bruh: Throwable) {
                                logger.error(bruh)
                            }
                        }
                    } catch (ignore: Throwable) {
                        logger.error(ignore)
                    }
                })
            patcher.patch(
                getDeclaredMethod("onViewCreated", View::class.java, Bundle::class.java),
                Hook { yes: XC_MethodHook.MethodHookParam ->
                    val linearLayout =
                        (yes.args[0] as NestedScrollView).getChildAt(0) as LinearLayout
                    val ctx = linearLayout.context

                    quoteIcon?.setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal))
                    Utils.tintToTheme(quoteIcon)

                    val quote = TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
                        text = "Quote"
                        id = quoteId
                        setCompoundDrawablesRelativeWithIntrinsicBounds(
                            quoteIcon,
                            null,
                            null,
                            null
                        )
                        typeface = ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_medium)
                    }

                    linearLayout.addView(quote, 2)
                })

            patcher.patch(
                AppFlexInputViewModel::class.java.getDeclaredMethod(
                    "onInputTextChanged",
                    String::class.java,
                    Boolean::class.javaObjectType
                ), Hook {
                    textBox = it.thisObject as AppFlexInputViewModel
                })

        })
    }

    @SuppressLint("SetTextI18n")
    private fun quoteNormal(inputBox: FlexEditText, msg: Message) {
        if (msg.content.contains("\n")) {
            inputBox.setText(
                "> ${
                    msg.content.replace(
                        "\n",
                        "\n> "
                    )
                }\n "
            )
            if (settings.getBool(
                    "mention",
                    true
                )
            ) inputBox.append("@${msg.author.r()}#${msg.author.f()} ")
            inputBox.text?.let { inputBox.setSelection(inputBox.selectionEnd) }
        } else {
            inputBox.setText("> ${msg.content}\n")
            if (settings.getBool(
                    "mention",
                    true
                )
            ) inputBox.append("@${msg.author.r()}#${msg.author.f()} ")
            inputBox.text?.let { inputBox.setSelection(inputBox.selectionEnd) }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun quoteAppend(inputBox: FlexEditText, msg: Message) {
        val quoteText = StringBuilder()
        if (msg.content.contains("\n")) quoteText.append(
            "\n> ${
                msg.content.replace(
                    "\n",
                    "\n> "
                )
            }\n"
        ) else quoteText.append("\n> ${msg.content}\n")
        if (settings.getBool(
                "mention",
                true
            )
        ) quoteText.append("@${msg.author.r()}#${msg.author.f()} ")
        textInput!!.text?.let { inputBox.setSelection(inputBox.selectionEnd) }
        inputBox.append(quoteText)
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
    }

}

class PluginSettings(private val settings: SettingsAPI) : BottomSheet() {
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        val ctx = requireContext()

        addView(createSetting(ctx, "Add mention to quote", "adds an author @ to the end, like old quote did", "mention"))
        addView(createSetting(ctx, "Add to end of message", "Add quote to end of the message instead of clearing the textbox", "append"))
    }

    private fun createSetting(
        ctx: Context,
        title: String,
        subtitle: String = "",
        setting: String,
        checked: Boolean = true
    ): CheckedSetting {
        return Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.SWITCH, title, subtitle).apply {
            isChecked = settings.getBool(setting, checked)
            setOnCheckedListener {
                settings.setBool(setting, it)
                PluginManager.remountPlugin("Quoter")
            }
        }
    }
}