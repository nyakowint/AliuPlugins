package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Constants
import com.aliucord.Utils
import com.aliucord.utils.DimenUtils.dpToPx
import com.aliucord.wrappers.ChannelWrapper.Companion.id
import com.discord.models.user.User
import com.discord.stores.StoreStream
import com.discord.utilities.images.MGImages
import com.discord.widgets.user.usersheet.WidgetUserSheet
import com.facebook.drawee.view.SimpleDraweeView
import com.lytefast.flexinput.R

@SuppressLint("ViewConstructor")
class MemberView(ctx: Context, user: User, guildId: Long) : LinearLayout(ctx) {
    val name: TextView
    val image: SimpleDraweeView

    init {
        val channel = StoreStream.getChannelsSelected().selectedChannel.id
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        val guildMember = StoreStream.getGuilds().getMember(guildId, user.id)
        name = TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
            text =
                if (guildMember?.nick != null) "${guildMember.nick} (${user.username})" else user.username
            setOnClickListener {
                WidgetUserSheet.show(
                    user.id, channel,
                    Utils.appActivity.supportFragmentManager, guildId
                )
            }
        }
        name.typeface = ctx.let { ResourcesCompat.getFont(it, Constants.Fonts.whitney_semibold) }
        image = SimpleDraweeView(ctx).apply {
            layoutParams = LayoutParams(128, 128)
            MGImages.setRoundingParams(this, 20f, false, null, null, 0f)
            MGImages.setImage(
                this,
                if (guildMember != null && guildMember.hasAvatar()) "https://cdn.discordapp.com/guilds/${guildId}/users/${user.id}/avatars/${guildMember.avatarHash}.png" else "https://cdn.discordapp.com/avatars/${user.id}/${user.avatar}.png"
            )
            setOnClickListener {
                WidgetUserSheet.show(
                    user.id, channel,
                    Utils.appActivity.supportFragmentManager, guildId
                )
            }
            setOnLongClickListener {
                Utils.setClipboard("User ID", user.id.toString())
                Utils.showToast("Copied ID to clipboard!")
                return@setOnLongClickListener true
            }
        }
        val buttons = LinearLayout(ctx)
        buttons.orientation = HORIZONTAL
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        params.marginEnd = dpToPx(16)
        buttons.layoutParams = params
        addView(image)
        addView(name)
    }
}