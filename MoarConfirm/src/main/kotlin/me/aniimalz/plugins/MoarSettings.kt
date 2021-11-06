package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.fragments.SettingsPage
import com.discord.views.CheckedSetting

class MoarSettings(private val settings: SettingsAPI): SettingsPage() {

    @SuppressLint("SetTextI18n")
    override fun onViewBound(view: View?) {
        super.onViewBound(view)
        setActionBarTitle("MOAR confirm")

        addView(createCheckedSetting(requireContext(), "All confirms", "With this off, all of the below will be disabled", "confirmsEnabled", true))

        addView(Utils.createCheckedSetting(requireContext(), CheckedSetting.ViewType.CHECK, "Enable Plugin.", "Disable Kyza Mode.").apply {
            isChecked = settings.getBool("uncap", true)
            setOnCheckedListener { settings.setBool("uncap", it) }
        })
        addView(Utils.createCheckedSetting(requireContext(), CheckedSetting.ViewType.CHECK, "Replace Your Own Messages.", "Replace your own messages.").apply {
            isChecked = settings.getBool("replaceSelf", false)
            setOnCheckedListener { settings.setBool("replaceSelf", it) }
        })
        addView(Utils.createCheckedSetting(requireContext(), CheckedSetting.ViewType.CHECK, "Ignore Bots.", "Don't touch messages from bots and webhooks.").apply {
            isChecked = settings.getBool("ignoreBots", true)
            setOnCheckedListener { settings.setBool("ignoreBots", it) }
        })



    }

    fun SettingsAPI.enabled(): Boolean {
        return getBool("confirmsEnabled", true)
    }

    fun SettingsAPI.call(): Boolean {
        return getBool("callConfirm", true)
    }

    fun SettingsAPI.video(): Boolean {
        return getBool("videoConfirm", true)
    }
    fun SettingsAPI.addFriend(): Boolean {
        return getBool("friendConfirm", true)
    }


    private fun createCheckedSetting(context: Context, name: String, desc: String, key: String, default: Boolean): View {
        return Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, name, desc).apply {
            isChecked = settings.getBool(key, default)
            setOnCheckedListener { settings.setBool(key, it) }
        }
    }
}