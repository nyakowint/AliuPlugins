package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Constants
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.fragments.SettingsPage
import com.aliucord.utils.DimenUtils
import com.aliucord.views.Divider
import com.aliucord.views.TextInput
import com.aliucord.widgets.BottomSheet
import com.aliucord.wrappers.ChannelWrapper.Companion.name
import com.discord.stores.StoreStream
import com.discord.views.CheckedSetting
import com.discord.widgets.guilds.WidgetGuildSelector
import com.lytefast.flexinput.R
import org.w3c.dom.Text
import java.lang.NumberFormatException

class BottomShit(private val settings: SettingsAPI) : BottomSheet() {

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        val ctx = requireContext()
        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header).apply {
            typeface = ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_bold)
            text = "Startup Channel"
            isAllCaps = true
            setPadding(DimenUtils.defaultPadding)
        })

        addView(Utils.createCheckedSetting(
            ctx, CheckedSetting.ViewType.SWITCH, "Enable plugin", null
        ).apply {
            isChecked = settings.getBool("enabled", true)
            setOnCheckedListener { settings.setBool("enabled", it) }
        })
        addView(Divider(ctx))

        val channel = settings.getLong("selectedChannel", 0L).takeIf { it != 0L }

        addView(TextInput(ctx, "Channel to open: ${channel?.let { "#${StoreStream.getChannels().getChannel(it).name}" }}", channel?.let { channel.toString() } ?: "", object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val txt = s.toString()
                try {
                    settings.setLong("selectedChannel", txt.toLong())
                } catch (n: NumberFormatException) {
                    Utils.showToast("Not a channel id")
                }
            }
        }))
    }
}