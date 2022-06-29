package me.aniimalz.plugins

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.fragments.SettingsPage
import com.aliucord.views.Divider
import com.discord.views.CheckedSetting
import me.aniimalz.plugins.Timezones.Companion.usersList
import java.util.*

class PluginSettings(private val plugin: Timezones) : SettingsPage() {
    override fun onViewBound(view: View) {
        super.onViewBound(view)
        val ctx = requireContext()
        setActionBarTitle("Timezones")

        val recycler = RecyclerView(ctx).apply {
            layoutManager = LinearLayoutManager(ctx)
            adapter = SettingsTZAdapter(this@PluginSettings, usersList)
        }

        addView(
                addSetting(
                        ctx,
                        "Time in message header",
                        "Show user's time in their timezone. Will look bad if the user has a long name, or if you have too many plugins using the message header",
                        "timeInHeader"
                )
        )
        addView(Divider(ctx))
        addView(recycler)

    }

    private fun addSetting(
            ctx: Context,
            title: String,
            subtitle: String = "",
            setting: String,
            checked: Boolean = false
    ): CheckedSetting {
        return Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.SWITCH, title, subtitle)
                .apply {
                    isChecked = plugin.settings.getBool(setting, checked)
                    setOnCheckedListener {
                        plugin.timeInHeader = it
                        plugin.settings.setBool(setting, it)
                        Utils.promptRestart()
                    }
                }
    }
}