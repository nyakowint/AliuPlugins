package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.TextView
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.fragments.SettingsPage
import com.aliucord.views.Divider
import com.discord.views.CheckedSetting
import com.discord.views.RadioManager
import com.lytefast.flexinput.R

class PluginSettings(private val settings: SettingsAPI) : SettingsPage() {

    @SuppressLint("SetTextI18n")
    override fun onViewBound(view: View?) {
        super.onViewBound(view)
        setActionBarTitle("Auto Server Notifications")
        val ctx = requireContext()

        addView(
            createSetting(
                ctx, "Apply settings", "Basically whether the plugin is enabled/disabled",
                "applyAss", CheckedSetting.ViewType.SWITCH, true
            )
        )

        addView(
            createSetting(
                ctx, "Mute Guild", "Mute the guild on join",
                "Mute Guild", CheckedSetting.ViewType.SWITCH, false
            )
        )

        val tv = TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header)
        tv.text = "Notification Settings"
        addView(Divider(ctx))
        addView(tv)

        arrayListOf(
            Utils.createCheckedSetting(
                ctx,
                CheckedSetting.ViewType.RADIO,
                "All Messages",
                "You wont get mobile push notifications for non-@mentions in large servers."
            ),
            Utils.createCheckedSetting(
                ctx,
                CheckedSetting.ViewType.RADIO,
                "Only @mentions",
                "Get notified only when you are pinged"
            ),
            Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.RADIO, "Nothing", null),
        ).let { radios ->
            val manager = RadioManager(radios)
            manager.a(radios[AutoServerNotifs.bSettings.notifFrequency.value])
            for (i in 0 until radios.size) {
                val radio = radios[i]
                radio.e {
                    manager.a(radio)
                    AutoServerNotifs.bSettings.notifFrequency = GuildNotifFrequency.from(i)
                }
                addView(radio)
            }
        } // i sure hope i implemented this right lol
        addView(Divider(ctx))
        addView(
            createSetting(
                ctx,
                "Suppress @everyone and @here",
                "",
                "suppressEveryone",
                CheckedSetting.ViewType.SWITCH,
                false
            )
        )
        addView(
            createSetting(
                ctx,
                "Suppress All Role @mentions",
                "",
                "suppressRoles",
                CheckedSetting.ViewType.SWITCH,
                false
            )
        )
        addView(
            createSetting(
                ctx,
                "Mobile Push Notifications",
                "",
                "mobilePushNotifs",
                CheckedSetting.ViewType.SWITCH,
                true
            )
        )

    }

    companion object {
        var SettingsAPI.notifFrequency
            get() = GuildNotifFrequency.from(
                getInt(
                    "notifFrequency",
                    GuildNotifFrequency.FREQUENCY_MENTIONS.value
                )
            )
            set(v) = setInt("notifFrequency", v.value)
    }

    enum class GuildNotifFrequency(val value: Int) {
        FREQUENCY_ALL(0),
        FREQUENCY_MENTIONS(1),
        FREQUENCY_NOTHING(2);

        companion object {
            fun from(i: Int) = values().first { it.value == i }
        }
    }

    private fun createSetting(
        ctx: Context = requireContext(),
        title: String,
        subtitle: String = "",
        setting: String,
        type: CheckedSetting.ViewType = CheckedSetting.ViewType.SWITCH,
        checked: Boolean = true
    ): CheckedSetting {
        return Utils.createCheckedSetting(ctx, type, title, subtitle).apply {
            isChecked = AutoServerNotifs.bSettings.getBool(setting, checked)
            setOnCheckedListener {
                AutoServerNotifs.bSettings.setBool(setting, it)
            }
        }
    }
}