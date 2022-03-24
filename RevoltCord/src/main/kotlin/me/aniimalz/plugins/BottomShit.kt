package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.views.Divider
import com.aliucord.views.TextInput
import com.aliucord.widgets.BottomSheet
import com.discord.views.CheckedSetting

class BottomShit(private val settings: SettingsAPI) : BottomSheet() {

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        val ctx = requireContext()
        addView(Utils.createCheckedSetting(
            ctx, CheckedSetting.ViewType.SWITCH, "Launch on startup", null
        ).apply {
            isChecked = settings.getBool("startup", false)
            setOnCheckedListener { settings.setBool("startup", it) }
        })
        addView(Divider(ctx))

        val instance = settings.getString("instance", "https://app.revolt.chat")

        addView(TextInput(ctx, "Revolt instance",
            instance?.let { instance.toString() } ?: "https://app.revolt.chat",
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {
                    val txt = s.toString()
                    try {
                        if (txt.isBlank()) {
                            Utils.showToast("Blank URL, setting to default")
                            settings.setString("instance", "https://app.revolt.chat")
                        }
                        settings.setString("instance", txt)
                    } catch (e: Throwable) {
                        Utils.showToast("Invalid URL")
                    }
                }
            })
        )
    }
}