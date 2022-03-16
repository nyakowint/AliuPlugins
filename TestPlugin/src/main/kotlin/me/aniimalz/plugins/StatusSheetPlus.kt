package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.aliucord.Utils
import com.aliucord.widgets.BottomSheet
import com.discord.widgets.user.WidgetUserStatusSheet
import com.lytefast.flexinput.R

class StatusSheetPlus : BottomSheet() {
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        val ctx = requireContext()

        addView(TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
            text = "Open Original"
            setOnClickListener {
                (WidgetUserStatusSheet.Companion).show(this@StatusSheetPlus)
            }
        })
    }
}