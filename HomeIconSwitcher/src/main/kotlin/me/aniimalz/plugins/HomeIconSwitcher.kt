package me.aniimalz.plugins

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.discord.utilities.images.MGImages
import com.discord.widgets.guilds.list.GuildListItem
import com.discord.widgets.guilds.list.GuildListViewHolder
import com.facebook.drawee.view.SimpleDraweeView
import com.lytefast.flexinput.R
import b.f.g.a.a.b as bruh


@AliucordPlugin
class HomeIconSwitcher : Plugin() {
    init {
        settingsTab = SettingsTab(
            PluginSettings::class.java,
            SettingsTab.Type.BOTTOM_SHEET
        ).withArgs(settings)
    }

    private lateinit var homeView: AppCompatImageView

    var pluginIcon: Drawable? = null

    override fun start(ctx: Context) {
        val viewId = View.generateViewId()
        pluginIcon = ContextCompat.getDrawable(Utils.appContext, R.e.ic_tab_home)
        patcher.after<GuildListViewHolder.FriendsViewHolder>(
            "configure",
            GuildListItem.FriendsItem::class.java
        ) {
            if (!settings.getBool("enabled", true)) return@after
            homeView = itemView.findViewById(
                Utils.getResId(
                    "guilds_item_profile_avatar",
                    "id"
                )
            )
            val layout = homeView.parent as FrameLayout
            val homeIcon = settings.getString("homeIcon", null).takeIf { it != null }
            try {
                if (homeIcon?.let { it1 -> Utils.getResId(it1, "drawable") } != 0) {
                    val field = homeIcon?.let { R.e::class.java.getField(homeIcon) }
                    val icon = homeIcon?.let {
                        field?.let { it1 ->
                            ContextCompat.getDrawable(ctx, it1.getInt(field))?.apply {
                                mutate()
                                Utils.tintToTheme(this)
                            }
                        }
                    }
                    homeView.setImageDrawable(icon)
                    return@after
                }
                if (settings.getBool("removeBg", false)) {
                    layout.apply {
                        backgroundTintList = null
                        setBackgroundColor(Color.TRANSPARENT)
                    }
                    homeView.apply {
                        backgroundTintList = null
                        imageTintList = null
                        setBackgroundColor(Color.TRANSPARENT)
                    }
                }
                if (layout.findViewById<ImageView>(viewId) != null) return@after
                SimpleDraweeView(ctx).apply {
                    id = viewId
                    imageTintList = null
                    clipToOutline = true
                    setImageURI(homeIcon)
                    layout.addView(this)
                    homeView.setImageDrawable(null)
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    ).apply {
                        gravity = Gravity.CENTER
                    }
                    background = ShapeDrawable(OvalShape()).apply { paint.color = Color.TRANSPARENT }
                    controller = bruh.a().run {
                        f(Uri.parse(homeIcon))
                        m = true
                        j = true
                        a()
                    }
                }
            } catch (t: Throwable) {
                logger.error(t)
            }
        }
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }

}