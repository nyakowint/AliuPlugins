package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Constants
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.utils.DimenUtils
import com.discord.widgets.user.usersheet.WidgetUserSheet
import com.discord.widgets.user.usersheet.WidgetUserSheetViewModel
import com.lytefast.flexinput.R

@AliucordPlugin
class UserTimezones : Plugin() {
    init {
        settingsTab = SettingsTab(
            PluginSettings::class.java,
            SettingsTab.Type.BOTTOM_SHEET
        ).withArgs(settings)
    }

    lateinit var pluginIcon: Drawable

    override fun load(context: Context) {
        pluginIcon = ContextCompat.getDrawable(context, R.e.ic_clock_24dp)!!
    }

    private val tzId = View.generateViewId()

    @SuppressLint("SetTextI18n")
    override fun start(ctx: Context) {
        patcher.patch(
            WidgetUserSheet::class.java.getDeclaredMethod(
                "configureNote",
                WidgetUserSheetViewModel.ViewState.Loaded::class.java
            ), Hook {
                val loaded = it.args[0] as WidgetUserSheetViewModel.ViewState.Loaded
                val user = loaded.user
                if (user == null || user.isBot) return@Hook
                val binding =
                    WidgetUserSheet.`access$getBinding$p`(it.thisObject as WidgetUserSheet)
                val headerId =
                    Utils.getResId("user_sheet_note_header", "id")// just above the note sheet
                val header = binding.a.findViewById<TextView>(headerId)
                val connId = Utils.getResId("user_sheet_connections_header", "id")
                val connHeader = binding.a.findViewById<TextView>(connId)
                val layout = header.parent as LinearLayout

                var tzView = layout.findViewById<TextView>(tzId)
                if (tzView == null) {
                    tzView =
                        TextView(layout.context, null, 0, R.i.UserProfile_Section_Header).apply {
                            id = tzId
                            typeface =
                                ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_semibold)
                            val dp = DimenUtils.defaultPadding
                            setPadding(dp, dp, dp, dp)
                            layout.addView(this, layout.indexOfChild(connHeader))
                        }
                }
                tzView.text = "Timezone: idk (04:20 PM)"

            })
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}