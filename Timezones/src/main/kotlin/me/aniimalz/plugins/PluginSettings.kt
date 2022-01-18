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

class PluginSettings(private val settings: SettingsAPI) : SettingsPage() {
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
                "Use 24-hour time",
                "Use 24 hour time instead of AM/PM",
                "24hourTime"
            )
        )
        addView(Divider(ctx))
        addView(
            addSetting(
                ctx,
                "Confirm removal",
                "protection from fat fingering the x",
                "confirmRemoval"
            )
        )
        addView(recycler)

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
                    PluginManager.remountPlugin("Timezones")
                }
            }
    }
}