package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.view.View
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.fragments.SettingsPage
import com.discord.views.CheckedSetting

class KyzaSettings(private val settings: SettingsAPI): SettingsPage() {

    @SuppressLint("SetTextI18n")
    override fun onViewBound(view: View?) {
        super.onViewBound(view)
        setActionBarTitle("NoKyzas")
        setActionBarSubtitle("...or whatever you wanna call it")

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
}