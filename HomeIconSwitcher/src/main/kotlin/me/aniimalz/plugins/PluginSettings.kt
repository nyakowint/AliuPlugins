package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Constants
import com.aliucord.Logger
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.fragments.SettingsPage
import com.aliucord.utils.DimenUtils
import com.aliucord.views.Button
import com.aliucord.views.Divider
import com.aliucord.views.TextInput
import com.aliucord.widgets.BottomSheet
import com.aliucord.wrappers.ChannelWrapper.Companion.name
import com.discord.stores.StoreStream
import com.discord.views.CheckedSetting
import com.discord.widgets.guilds.WidgetGuildSelector
import com.facebook.drawee.view.SimpleDraweeView
import com.lytefast.flexinput.R
import org.w3c.dom.Text
import java.lang.NumberFormatException

class PluginSettings(private val settings: SettingsAPI) : BottomSheet() {

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        val ctx = requireContext()
        val logger = Logger("HomeIconSwitcher")

        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header).apply {
            typeface = ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_bold)
            text = "Home Icon Switcher"
            isAllCaps = true
            setPadding(DimenUtils.defaultPadding)
        })

        addView(Utils.createCheckedSetting(
            ctx, CheckedSetting.ViewType.SWITCH, "Enable plugin", null
        ).apply {
            isChecked = settings.getBool("enabled", true)
            setOnCheckedListener {
                settings.setBool("enabled", it)
                promptRestart()
            }
        })
        addView(Utils.createCheckedSetting(
            ctx, CheckedSetting.ViewType.SWITCH, "Remove colored background", "Remove discord's colored background behind the image"
        ).apply {
            isChecked = settings.getBool("removeBg", true)
            setOnCheckedListener {
                settings.setBool("removeBg", it)
                promptRestart()
            }
        })
        addView(Divider(ctx))

        val icon = settings.getString("homeIcon", null).takeIf { it != null }
        val roundAmount = settings.getFloat("roundAmount", -1f).takeIf { it != -1f }

        addView(TextInput(ctx, "Home icon URL (supports drawables)", icon?.let { icon } ?: "", object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                try {
                    val txt = s.toString()
                    settings.setString("homeIcon", txt)
                    promptRestart("Restart to apply icon change.")
                } catch (n: Throwable) {
                    logger.error(n)
                }
            }
        }))

        addView(TextInput(ctx, "Amount to round image (default 60)", roundAmount?.let { roundAmount.toString() } ?: "60", object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val txt: Float? = try {
                    s.toString().toFloat()
                } catch (n: NumberFormatException) {
                    60f
                }
                try {
                    settings.setFloat("roundAmount", txt ?: 60f)
                    promptRestart()
                } catch (n: Throwable) {
                    logger.error(n)
                }
            }
        }))

    }

    private fun promptRestart(t: String = "Restart to apply changes") {
        Utils.promptRestart(t)
    }
}