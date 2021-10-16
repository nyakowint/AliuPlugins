package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.aliucord.Logger
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.utils.ReflectUtils
import com.aliucord.widgets.BottomSheet
import com.discord.models.message.Message
import com.discord.models.user.CoreUser
import com.discord.stores.StoreStream
import com.discord.views.CheckedSetting
import com.lytefast.flexinput.R

@AliucordPlugin
class NoKyzas : Plugin() {
    init {
        settingsTab =
            SettingsTab(Settings::class.java, SettingsTab.Type.BOTTOM_SHEET).withArgs(settings)
    }

    private val logger: Logger = Logger("NoKyzas")
    private var pluginIcon: Drawable? = null

    override fun load(ctx: Context) {
        pluginIcon = ContextCompat.getDrawable(ctx, R.d.ic_warning_circle_24dp)
    }

    override fun start(ctx: Context) {
        patcher.patch(Message::class.java.getDeclaredMethod("getContent"), Hook {
            if (!settings.getBool("uncap", true)) {
                return@Hook
            }
            val msg = (ReflectUtils.getField(it.thisObject as Message, "content") as String)
            if (CoreUser((it.thisObject as Message).author).id == StoreStream.getUsers().me.id && !settings.getBool("replaceSelf", false)) return@Hook
            val result = msg.replaceFirstChar { w -> w.lowercase() }
            it.result = result.dropLastWhile { c -> c == '.' || c == '​' }
        })
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}

class Settings(private val settings: SettingsAPI) : BottomSheet() {
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        addView(Utils.createCheckedSetting(requireContext(), CheckedSetting.ViewType.SWITCH, "Enable plugin", "Disable kyza mode").apply {
            isChecked = settings.getBool("uncap", true)
            setOnCheckedListener { settings.setBool("uncap", it) }
        })
        addView(Utils.createCheckedSetting(requireContext(), CheckedSetting.ViewType.SWITCH, "Replace own messages", "Replace your own messages.").apply {
            isChecked = settings.getBool("replaceSelf", false)
            setOnCheckedListener { settings.setBool("replaceSelf", it) }
        })
    }
}