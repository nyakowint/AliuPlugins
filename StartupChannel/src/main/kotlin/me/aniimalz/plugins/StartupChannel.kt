package me.aniimalz.plugins

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Constants
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.utils.ReflectUtils
import com.aliucord.wrappers.ChannelWrapper.Companion.id
import com.aliucord.wrappers.ChannelWrapper.Companion.lastMessageId
import com.aliucord.wrappers.ChannelWrapper.Companion.name
import com.discord.databinding.WidgetChannelsListItemActionsBinding
import com.discord.stores.StoreStream
import com.discord.utilities.color.ColorCompat
import com.discord.widgets.channels.list.WidgetChannelsListItemChannelActions
import com.lytefast.flexinput.R

@AliucordPlugin
class StartupChannel : Plugin() {
    init {
        settingsTab = SettingsTab(
            BottomShit::class.java,
            SettingsTab.Type.BOTTOM_SHEET
        ).withArgs(settings)
    }

    var pluginIcon: Drawable? = null

    override fun start(ctx: Context) {
        pluginIcon = ContextCompat.getDrawable(Utils.appContext, R.e.ic_channel_text)
        if (!settings.getBool("enabled", true)) return
        val icon = Utils.tintToTheme(ContextCompat.getDrawable(Utils.appContext, R.e.ic_star_24dp))
        val vid = View.generateViewId()
        val channel = settings.getLong("selectedChannel", 0L).takeIf { it != 0L }
        try {
            (StoreStream.Companion).messagesLoader.jumpToMessage(
                channel ?: return,
                StoreStream.getChannels().getChannel(channel).lastMessageId
            )
        } catch (t: Throwable) {
            logger.error(t)
        }

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
                            if (model.channel.id == settings.getLong("selectedChannel", 0L)) "Unset as Startup Channel" else "Set as Startup Channel"
                        setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
                        id = vid
                        typeface = ResourcesCompat.getFont(Utils.appContext, Constants.Fonts.whitney_medium)
                        setOnClickListener {
                            if (model.channel.id == settings.getLong("selectedChannel", 0L)) {
                                settings.setLong("selectedChannel", 0L)
                                actions.dismiss()
                                return@setOnClickListener
                            }
                            settings.setLong("selectedChannel", model.channel.id)
                            actions.dismiss()
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
                    logger.error("Error adding button to channel actions", e)
                }
            })
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
    }
}