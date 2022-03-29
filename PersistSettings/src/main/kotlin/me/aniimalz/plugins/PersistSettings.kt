package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import com.aliucord.Constants
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.InsteadHook
import com.aliucord.patcher.instead
import com.aliucord.widgets.BottomSheet
import com.discord.stores.StoreAuthentication
import com.discord.stores.StoreEmoji
import com.discord.stores.StoreNux
import com.discord.stores.StoreStickers
import com.discord.utilities.persister.Persister
import com.discord.views.CheckedSetting


@AliucordPlugin
class PersistSettings : Plugin() {
    init {
        settingsTab = SettingsTab(
            PluginSettings::class.java,
            SettingsTab.Type.BOTTOM_SHEET
        ).withArgs(settings)
    }

    @SuppressLint("SetTextI18n")
    override fun start(ctx: Context) {
        val authToken = StoreAuthentication::class.java.getDeclaredField("authToken").apply {
            isAccessible = true
        }

        patcher.instead<StoreAuthentication>(
            "handleAuthToken$${Constants.RELEASE_SUFFIX}",
            String::class.java
        ) {
            val token = it.args[0] as String
            try {
                authToken.set(this, token)
                this.prefs.edit().putString("STORE_AUTHED_TOKEN", token).apply()
                if (!settings.getBool("persistSettings", true)) {
                    Persister.reset()
                    val edit = this.prefs.edit()
                    edit?.let { p ->
                        p.clear()
                        p.apply()
                    }
                }
            } catch (t: Throwable) {
                logger.error(t)
            }
        }

        patcher.instead<StoreEmoji>("handlePreLogout") { InsteadHook.DO_NOTHING }
        patcher.instead<StoreStickers>("handlePreLogout") { InsteadHook.DO_NOTHING }
        patcher.instead<StoreStickers>("handlePreLogout") { InsteadHook.DO_NOTHING }
        patcher.patch(
            StoreNux::class.java.getDeclaredMethod(
                "setFirstOpen",
                Boolean::class.javaPrimitiveType
            ), InsteadHook.DO_NOTHING
        )
    }

    override fun stop(ctx: Context) = patcher.unpatchAll()
}

class PluginSettings(private val settings: SettingsAPI) : BottomSheet() {
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        val ctx = requireContext()

        addView(
            createSetting(
                ctx,
                "Save settings",
                "Save emotes, stickers, and your login",
                "persistSettings"
            )
        )
    }

    private fun createSetting(
        ctx: Context,
        title: String,
        subtitle: String = "",
        setting: String,
        checked: Boolean = true
    ): CheckedSetting {
        return Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.SWITCH, title, subtitle)
            .apply {
                isChecked = settings.getBool(setting, checked)
                setOnCheckedListener {
                    settings.setBool(setting, it)
                    PluginManager.remountPlugin("PersistSettings")
                }
            }
    }
}