package me.aniimalz.plugins

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.aliucord.utils.DimenUtils.defaultPadding
import com.aliucord.utils.DimenUtils.dpToPx
import com.discord.utilities.images.MGImages
import com.facebook.drawee.view.SimpleDraweeView
import com.lytefast.flexinput.R

@Suppress("Deprecation")
class UserTZCard(ctx: Context?) : LinearLayout(ctx) {
    val name: TextView
    val delete: com.aliucord.views.ToolbarButton
    val image: SimpleDraweeView

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        defaultPadding.let { setPadding(it, it / 2, it, it / 2) }
        name = TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon).apply {

        }
        image = SimpleDraweeView(ctx).apply {
            layoutParams = LayoutParams(128, 128).apply {
                gravity = Gravity.CENTER
            }
            MGImages.setRoundingParams(this, 20f, false, null, null, 0f)
        }
        val buttons = LinearLayout(ctx)
        buttons.orientation = HORIZONTAL
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        params.marginEnd = dpToPx(16)
        buttons.layoutParams = params
        buttons.setHorizontalGravity(Gravity.END)
        buttons.setVerticalGravity(Gravity.CENTER_VERTICAL)
        delete = com.aliucord.views.ToolbarButton(ctx)
        delete.setImageDrawable(ContextCompat.getDrawable(ctx!!, R.e.ic_close_primary_200_24dp))
        buttons.addView(delete)
        addView(image)
        addView(name)
        addView(buttons)
    }
}