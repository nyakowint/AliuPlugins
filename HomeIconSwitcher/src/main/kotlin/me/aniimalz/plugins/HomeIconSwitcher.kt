package me.aniimalz.plugins

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.discord.databinding.TabsHostBottomNavigationViewBinding
import com.discord.views.user.UserAvatarPresenceView
import com.discord.widgets.guilds.list.GuildListItem
import com.discord.widgets.guilds.list.GuildListViewHolder
import com.facebook.drawee.view.SimpleDraweeView
import com.lytefast.flexinput.R
import c.f.g.a.a.b as bruh


@AliucordPlugin
class HomeIconSwitcher : Plugin() {
    init {
        settingsTab = SettingsTab(
            PluginSettings::class.java,
            SettingsTab.Type.BOTTOM_SHEET
        ).withArgs(settings)
    }

    private lateinit var homeView: AppCompatImageView

    override fun start(ctx: Context) {
        val ctx = Utils.appContext
        patcher.after<GuildListViewHolder.FriendsViewHolder>(
            "configure",
            GuildListItem.FriendsItem::class.java
        ) {
            homeView = itemView.findViewById(
                Utils.getResId(
                    "guilds_item_profile_avatar",
                    "id"
                )
            )
            val viewId = View.generateViewId()
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
                if (homeView.findViewById<SimpleDraweeView>(viewId) != null) return@after
                SimpleDraweeView(ctx).apply {
                    id = viewId
                    imageTintList = null
                    setImageURI(homeIcon)
                    (homeView.parent as FrameLayout).addView(this)
                    homeView.setImageDrawable(null)
                    layoutParams = FrameLayout.LayoutParams(128, 128).apply {
                        gravity = Gravity.CENTER
                    }
                    clipToOutline = true
                    background =
                        ShapeDrawable(OvalShape()).apply { paint.color = Color.TRANSPARENT }
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