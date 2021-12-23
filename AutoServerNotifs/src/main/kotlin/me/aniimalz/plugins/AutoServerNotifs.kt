package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import com.adjust.sdk.Util
import com.aliucord.Constants
import com.aliucord.Logger
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.patcher.after
import com.aliucord.patcher.before
import com.aliucord.utils.DimenUtils
import com.aliucord.utils.ReflectUtils
import com.aliucord.utils.RxUtils.subscribe
import com.aliucord.wrappers.GuildWrapper
import com.discord.api.guild.Guild
import com.discord.databinding.WidgetGuildProfileSheetBinding
import com.discord.restapi.RestAPIParams
import com.discord.stores.StoreGuilds
import com.discord.stores.StoreStream
import com.discord.utilities.rest.RestAPI
import com.discord.widgets.guilds.profile.WidgetGuildProfileSheet
import com.discord.widgets.guilds.profile.WidgetGuildProfileSheetViewModel.TabItems
import com.lytefast.flexinput.R
import me.aniimalz.plugins.PluginSettings.Companion.notifFrequency
import rx.Subscription
import java.lang.reflect.InvocationTargetException
import java.text.SimpleDateFormat

@AliucordPlugin
class AutoServerNotifs : Plugin() {

    init {
        settingsTab = SettingsTab(
            PluginSettings::class.java,
            SettingsTab.Type.PAGE
        ).withArgs(settings)
    }

    private val id = View.generateViewId()

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun start(ctx: Context) {
        bSettings = settings
        patcher.patch(
            WidgetGuildProfileSheet::class.java.getDeclaredMethod(
                "configureTabItems",
                Long::class.javaPrimitiveType,
                TabItems::class.java,
                Boolean::class.javaPrimitiveType
            ), Hook {
                try {
                    val getBinding =
                        ReflectUtils.getMethodByArgs(
                            WidgetGuildProfileSheet::class.java, "getBinding"
                        )
                    val binding =
                        getBinding.invoke(it.thisObject) as WidgetGuildProfileSheetBinding
                    val layout = binding.f.rootView as ViewGroup
                    val actionsId = layout.findViewById(
                        Utils.getResId(
                            "guild_profile_sheet_secondary_actions",
                            "id"
                        )
                    ) as CardView
                    val linearLayout =
                        actionsId.getChildAt(0) as LinearLayout
                    if (linearLayout.findViewById<View>(id) != null) {
                        return@Hook
                    }
                    val button = TextView(
                        layout.context,
                        null,
                        0,
                        R.i.UiKit_Settings_Item_Icon
                    )
                    DimenUtils.defaultPadding.let { pd -> button.setPadding(pd, pd, pd, pd) }
                    button.typeface = ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_semibold)
                    button.id = id
                    button.text = "Auto Server Notifications"
                    button.setOnClickListener {
                        Utils.openPageWithProxy(
                            Utils.appActivity,
                            PluginSettings(bSettings)
                        )
                    }
                    button.layoutParams = linearLayout.getChildAt(0).layoutParams
                    linearLayout.addView(button)
                } catch (e: NoSuchMethodException) {
                    logger.error(e)
                }
            })

        patcher.before<StoreGuilds>("handleGuildAdd", Guild::class.java) {
            if (!settings.getBool("applyAss", true)) return@before
            val guild = GuildWrapper(it.args[0] as Guild)
            if (guilds.containsKey(guild.id)) return@before

            apply(guild)
        }

    }

    private fun apply(guild: GuildWrapper) {
        Utils.showToast("Applying for ${guild.name}. If you're seeing this without joining a server first, please report it to Animal")
        logger.info("${guild.name} (${guild.id}) joined, applying notification settings")
        logger.info("Suppress @everyone and @here: ${getBool("suppressEveryone")}")
        logger.info("Suppress Role Mentions: ${getBool("suppressRoles")}")
        logger.info("Mute ${guild.name}: ${getBool("Mute Guild")}")
        logger.info("Mobile Push notifs: ${getBool("mobilePushNotifs")}")
        logger.info("Notification frequency: ${bSettings.notifFrequency.name}")

        Utils.threadPool.execute {
            sub = RestAPI.api.updateUserGuildSettings(
                guild.id,
                RestAPIParams.UserGuildSettings(
                    getBool("suppressEveryone"),
                    getBool("suppressRoles"),
                    getBool("Mute Guild"),
                    null,
                    getBool("mobilePushNotifs", true),
                    bSettings.notifFrequency.value,
                    null
                )
            ).subscribe {}
        }
    }

    private var sub: Subscription? = null
    private fun getBool(setting: String, default: Boolean = false): Boolean {
        return bSettings.getBool(setting, default)
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        sub?.unsubscribe()
    }

    companion object {
        lateinit var bSettings: SettingsAPI
    }
}

