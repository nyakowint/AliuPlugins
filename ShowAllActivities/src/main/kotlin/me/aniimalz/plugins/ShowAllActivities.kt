package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.views.Button
import com.discord.databinding.WidgetUserSheetBinding
import com.discord.widgets.user.usersheet.WidgetUserSheet
import com.discord.widgets.user.usersheet.WidgetUserSheetViewModel
import com.facebook.drawee.view.SimpleDraweeView
import com.lytefast.flexinput.R

@AliucordPlugin
class ShowAllActivities : Plugin() {
    init {
        settingsTab = SettingsTab(
            PluginSettings::class.java,
            SettingsTab.Type.PAGE
        ).withArgs(settings)
    }

    var pluginIcon: Drawable? = null

    @SuppressLint("SetTextI18n")
    override fun start(ctx: Context) {
        pluginIcon = ContextCompat.getDrawable(Utils.appContext, R.e.ic_controller_24dp)
        patcher.after<WidgetUserSheet>(
            "configureNote",
            WidgetUserSheetViewModel.ViewState.Loaded::class.java
        ) {
            val vs = it.args[0] as WidgetUserSheetViewModel.ViewState.Loaded
            val binding =
                this.javaClass.getDeclaredMethod("getBinding").apply { isAccessible = true }
                    .invoke(this) as WidgetUserSheetBinding
            val layout = binding.root.findViewById<SimpleDraweeView>(
                Utils.getResId(
                    "rich_presence_image_large",
                    "id"
                )
            ).parent.parent.parent.parent.parent as LinearLayout
            layout.addView(Button(ctx).apply {
                text = "View all Activities"
                setOnClickListener {
                    Utils.openPageWithProxy(
                        Utils.appActivity,
                        UserActivities(vs.user, vs.presence.activities)
                    )
                }
            })

        }
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}