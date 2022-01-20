package me.aniimalz.plugins

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.discord.utilities.icon.IconUtils
import com.discord.widgets.guilds.list.GuildListItem
import com.discord.widgets.guilds.list.GuildListViewHolder
import com.lytefast.flexinput.R
import android.graphics.BitmapFactory

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import com.facebook.drawee.view.SimpleDraweeView
import java.io.IOException
import java.net.URL


@AliucordPlugin
class HomeIconSwitcher : Plugin() {
    init {
        settingsTab = SettingsTab(
            PluginSettings::class.java,
            SettingsTab.Type.PAGE
        ).withArgs(settings)
    }

    override fun start(ctx: Context) {
        val ctx = Utils.appContext
        patcher.after<GuildListViewHolder.FriendsViewHolder>(
            "configure",
            GuildListItem.FriendsItem::class.java
        ) {
            val wing = SimpleDraweeView(ctx)
            val imgView = itemView.findViewById<AppCompatImageView>(
                Utils.getResId(
                    "guilds_item_profile_avatar",
                    "id"
                )
            )
            val lp = imgView.layoutParams
            imgView.visibility = View.GONE
            wing.apply {
                layoutParams = lp
            }
            val bgId = View.generateViewId()
/*            var image: Bitmap? = null
            try {
                val url =
                    URL("https://cdn.discordapp.com/attachments/930236756182302732/933005852288356442/image0-5-1.gif")
                image = BitmapFactory.decodeStream(url.openStream())
            } catch (t: Throwable) {
                logger.error(t)
            }*/
            SimpleDraweeView((imgView.parent as ViewGroup).context).run {
                this.id = bgId
                layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                controller = c.f.g.a.a.b.a().run /*  Fresco.newDraweeControllerBuilder() */ {
                    f(Uri.parse("https://cdn.discordapp.com/attachments/930236756182302732/933005852288356442/image0-5-1.gif")) // setUri(Uri)
                    m = true // mAutoPlayAnimations
                    a() // build()
                }
                (imgView.parent as ViewGroup).addView(this, 0)
            }
        }
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}