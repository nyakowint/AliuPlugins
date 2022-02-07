package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Constants
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.patcher.InsteadHook
import com.aliucord.patcher.after
import com.aliucord.utils.DimenUtils
import com.aliucord.utils.ReflectUtils
import com.aliucord.wrappers.ChannelWrapper.Companion.id
import com.aliucord.wrappers.ChannelWrapper.Companion.name
import com.discord.databinding.WidgetChannelsListItemActionsBinding
import com.discord.databinding.WidgetChatInputBinding
import com.discord.utilities.color.ColorCompat
import com.discord.widgets.channels.list.WidgetChannelsListItemChannelActions
import com.discord.widgets.chat.input.ChatInputViewModel
import com.discord.widgets.chat.input.WidgetChatInput
import com.google.gson.reflect.TypeToken
import com.lytefast.flexinput.R
import java.util.*

@AliucordPlugin
class ChannelLocker : Plugin() {
    init {
        settingsTab = SettingsTab(
            PluginSettings::class.java,
            SettingsTab.Type.PAGE
        ).withArgs(settings)
    }


    @SuppressLint("SetTextI18n")
    override fun start(ctx: Context) {
        val icon = Utils.tintToTheme(
            ContextCompat.getDrawable(
                Utils.appContext,
                R.e.ic_lock_dark_a60_24dp
            )
        ) // diamong makes me husk
        val channelLockId = View.generateViewId()
        val channels = settings.getObject(
            "channels", HashMap<String, Long>(), TypeToken.getParameterized(
                HashMap::class.java, String::class.javaObjectType, Long::class.javaObjectType
            ).type
        )

        commands.registerCommand(
            "lockchannel",
            "Use this to lock the channel so you cannot type in it anymore"
        ) {
            if (channels.containsValue(it.channelId)) return@registerCommand CommandsAPI.CommandResult(
                "Channel is already locked! how did you do this lol",
                null,
                false
            )
            channels[it.currentChannel.name] = it.channelId
            settings.setObject("channels", channels)
            CommandsAPI.CommandResult("Channel has been locked!", null, false)
        }


        val bindingMethod = WidgetChatInput::class.java.getDeclaredMethod("getBinding")
            .apply { isAccessible = true }
        patcher.after<WidgetChatInput>(
            "configureChatGuard", ChatInputViewModel.ViewState.Loaded::class.java
        ) {
            val loaded = it.args[0] as ChatInputViewModel.ViewState.Loaded

            if (channels.containsValue(loaded.channelId) && !loaded.shouldShowVerificationGate) {
                val binding = bindingMethod(it.thisObject) as WidgetChatInputBinding

                val gateButtonText = binding.root.findViewById<TextView>(
                    Utils.getResId(
                        "chat_input_member_verification_guard_text",
                        "id"
                    )
                )
                val chatWrap = binding.root.findViewById<LinearLayout>(
                    Utils.getResId(
                        "chat_input_wrap",
                        "id"
                    )
                )
                val gateButtonImage = binding.root.findViewById<ImageView>(
                    Utils.getResId(
                        "chat_input_member_verification_guard_icon",
                        "id"
                    )
                )
                val gateButtonArrow = binding.root.findViewById<ImageView>(
                    Utils.getResId(
                        "chat_input_member_verification_guard_action",
                        "id"
                    )
                )
                val gateButtonLayout = binding.root.findViewById<RelativeLayout>(
                    Utils.getResId(
                        "guard_member_verification",
                        "id"
                    )
                )

                gateButtonLayout.visibility = View.VISIBLE
                chatWrap.visibility = View.GONE

                gateButtonImage.setImageResource(R.e.ic_channel_text_locked)
                gateButtonText.text =
                    "Channel is locked"
                gateButtonArrow.setImageResource(R.e.ic_check_white_24dp)
                val pd = DimenUtils.defaultPadding
                gateButtonArrow.setPadding(pd, pd, pd, pd)
                gateButtonArrow.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                gateButtonArrow.setOnLongClickListener {
                    channels.remove(loaded.channel.name)
                    settings.setObject("channels", channels)

                    gateButtonLayout.visibility = View.GONE
                    chatWrap.visibility = View.VISIBLE
                    return@setOnLongClickListener true
                }
                gateButtonArrow.setOnClickListener {
                    InsteadHook.DO_NOTHING
                }
                gateButtonArrow.visibility =
                    if (settings.getBool("showUnlock", true)) View.VISIBLE else View.GONE
            }
        }


        // just adding stupid buttons to channel settings and channel list actions

        patcher.patch(
            WidgetChannelsListItemChannelActions::class.java, "configureUI", arrayOf<Class<*>>(
                WidgetChannelsListItemChannelActions.Model::class.java
            ), Hook {
                val model = it.args[0] as WidgetChannelsListItemChannelActions.Model
                val actions = it.thisObject as WidgetChannelsListItemChannelActions
                try {
                    val binding = ReflectUtils.invokeMethod(
                        actions,
                        "getBinding"
                    ) as WidgetChannelsListItemActionsBinding?
                    val root = (binding?.root as ViewGroup).getChildAt(0) as ViewGroup
                    TextView(root.context, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
                        text =
                            if (!channels.containsValue(model.channel.id)) "Lock Channel" else "Unlock Channel"
                        setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
                        id = channelLockId
                        typeface = ResourcesCompat.getFont(
                            Utils.appContext,
                            Constants.Fonts.whitney_medium
                        )
                        setOnClickListener {
                            if (channels.containsValue(model.channel.id)) {
                                channels.remove(model.channel.name)
                                actions.dismiss()
                                Utils.showToast("Channel unlocked")
                                settings.setObject("channels", channels)
                                return@setOnClickListener
                            }
                            channels[model.channel.name] = model.channel.id
                            actions.dismiss()
                            Utils.showToast("Channel locked")
                            settings.setObject("channels", channels)
                        }
                        root.addView(this)
                    }
                    icon?.setTint(
                        ColorCompat.getThemedColor(
                            root.context,
                            R.b.colorInteractiveNormal
                        )
                    )
                } catch (e: Throwable) {
                    logger.error("Error adding lock button to channel actions", e)
                }
            })
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}