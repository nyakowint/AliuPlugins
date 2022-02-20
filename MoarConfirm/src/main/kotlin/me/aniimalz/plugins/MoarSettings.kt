package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.view.View
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.fragments.SettingsPage
import com.discord.views.CheckedSetting

class MoarSettings(private val settings: SettingsAPI) : SettingsPage() {

    @SuppressLint("SetTextI18n")
    override fun onViewBound(view: View?) {
        super.onViewBound(view)
        setActionBarTitle("MoarConfirm")

        addView(
            Utils.createCheckedSetting(
                requireContext(),
                CheckedSetting.ViewType.SWITCH,
                "Call confirmation",
                "Confirm before voice/video calling someone"
            ).apply {
                isChecked = settings.getBool("callConfirm", true)
                setOnCheckedListener { settings.setBool("callConfirm", it) }
            })
        addView(
            Utils.createCheckedSetting(
                requireContext(),
                CheckedSetting.ViewType.SWITCH,
                "Friend confirmation",
                "Confirm before friending someone"
            ).apply {
                isChecked = settings.getBool("friendConfirm", true)
                setOnCheckedListener { settings.setBool("friendConfirm", it) }
            })

        addView(
            Utils.createCheckedSetting(
                requireContext(),
                CheckedSetting.ViewType.SWITCH,
                "Role delete confirmation",
                "Confirm before deleting a role"
            ).apply {
                isChecked = settings.getBool("deleteRoleConfirm", true)
                setOnCheckedListener { settings.setBool("deleteRoleConfirm", it) }
            })

    }
}