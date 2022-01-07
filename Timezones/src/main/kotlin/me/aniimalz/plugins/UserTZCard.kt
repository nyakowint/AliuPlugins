package me.aniimalz.plugins

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.aliucord.utils.DimenUtils.dpToPx
import com.aliucord.views.ToolbarButton
import com.facebook.drawee.view.SimpleDraweeView
import com.lytefast.flexinput.R




class UserTZCard(ctx: Context?) : LinearLayout(ctx) {
    val name: TextView
    val delete: ToolbarButton
    val image: SimpleDraweeView

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        name = TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
            
        }
        image = SimpleDraweeView(ctx).apply {
            layoutParams = LayoutParams(128, 128).apply {
                gravity = Gravity.CENTER
                clipToOutline
                setMargins(0, 0, dpToPx(6), 0)
            }
        }

        background = ShapeDrawable(OvalShape()).apply { paint.color = Color.TRANSPARENT }
        val buttons = LinearLayout(ctx)
        buttons.orientation = HORIZONTAL
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        params.marginEnd = dpToPx(16)
        buttons.layoutParams = params
        buttons.setHorizontalGravity(Gravity.END)
        buttons.setVerticalGravity(Gravity.CENTER_VERTICAL)
        delete = ToolbarButton(ctx)
        delete.setImageDrawable(ContextCompat.getDrawable(ctx!!, R.e.ic_close_primary_200_24dp))
        buttons.addView(delete)
        addView(image)
        addView(name)
        addView(buttons)
    }
}