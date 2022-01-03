package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.utils.DimenUtils
import com.aliucord.widgets.BottomSheet
import com.discord.views.CheckedSetting

class PluginSettings(private val settings: SettingsAPI): BottomSheet() {

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(p0: View, p1: Bundle?) {
        super.onViewCreated(p0, p1)

        setPadding(DimenUtils.defaultPadding)

        addView(Utils.createCheckedSetting(requireContext(), CheckedSetting.ViewType.SWITCH, "Enable GhostMessage", null).apply {
            isChecked = settings.getBool("ghostMessages", false)
            setOnCheckedListener { settings.setBool("ghostMessages", it) }
        })



    }
}