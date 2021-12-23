package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.fragments.SettingsPage
import com.aliucord.views.Button
import com.aliucord.views.Divider
import com.discord.models.domain.emoji.Emoji
import com.discord.views.CheckedSetting
import com.discord.widgets.chat.input.emoji.EmojiPickerContextType
import com.discord.widgets.chat.input.emoji.EmojiPickerNavigator
import com.google.gson.reflect.TypeToken
import com.lytefast.flexinput.R
import java.util.*

class PluginSettings(private val settings: SettingsAPI) : SettingsPage() {
    @SuppressLint("SetTextI18n")
    override fun onViewBound(view: View) {
        super.onViewBound(view)
        val ctx = requireContext()
        setActionBarTitle("Reaction Notifier")

        addView(addSetting(ctx, "Notify on reaction add", "", "notifyAdd"))
        addView(addSetting(ctx, "Notify on reaction remove", "", "notifyRemove"))
        addView(addSetting(ctx, "Ignore bot reactions", "Don't notify for bot reactions", "ignoreBots"))
    }

    private fun addSetting(
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
                    PluginManager.remountPlugin("ReactionNotifier")
                }
            }
    }
}