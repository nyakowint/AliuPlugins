package me.aniimalz.plugins

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Constants
import com.aliucord.Utils
import com.aliucord.utils.DimenUtils.dpToPx
import com.discord.api.activity.Activity
import com.discord.utilities.images.MGImages
import com.discord.widgets.user.usersheet.WidgetUserSheet
import com.facebook.drawee.view.SimpleDraweeView
import com.lytefast.flexinput.R

class RichPresenceContainer(ctx: Context, activity: Activity) : ViewGroup(ctx) {
    val title: TextView
    val details: TextView
    val state: TextView
/*    val time: TextView*/
    val largeImage: SimpleDraweeView
    val smallImage: SimpleDraweeView

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        title = presenceText(ctx, Constants.Fonts.whitney_semibold).apply { text = activity.h() }
        largeImage = SimpleDraweeView(ctx).apply {
            layoutParams = LayoutParams(128, 128)
            if (activity.b().a() != null) {
                setImageURI(activity.b().a())
            }
            if (activity.b().b() != null) {
                setOnClickListener { Utils.showToast(activity.b().b()) }
            }
        }
        smallImage = SimpleDraweeView(ctx).apply {
            layoutParams = LayoutParams(64, 64)
            if (activity.b().c() != null) {
                setImageURI(activity.b().c())
            }
            if (activity.b().d() != null) {
                setOnClickListener { Utils.showToast(activity.b().d()) }
            }
        }
        details = presenceText(ctx, Constants.Fonts.ginto_regular).apply { text = activity.e() }
        state = presenceText(ctx, Constants.Fonts.ginto_regular).apply { text = activity.l() }
/*        val buttons = LinearLayout(ctx)
        buttons.orientation = HORIZONTAL
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        params.marginEnd = dpToPx(16)
        buttons.layoutParams = params*/
        addView(title)
        addView(largeImage)
        addView(smallImage)
        addView(details)
        addView(state)
    }

    private fun presenceText(ctx: Context, @FontRes font: Int): TextView {
        return TextView(ctx, null, 0).apply {
            typeface = ctx.let { ResourcesCompat.getFont(it, font) }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    }
}