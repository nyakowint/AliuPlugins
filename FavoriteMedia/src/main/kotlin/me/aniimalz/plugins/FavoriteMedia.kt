package me.aniimalz.plugins

import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.discord.widgets.chat.input.MessageDraftsRepo
import com.discord.widgets.chat.input.WidgetChatInputEditText
import com.lytefast.flexinput.R
import com.lytefast.flexinput.widget.FlexEditText

import android.annotation.SuppressLint
import android.view.View
import android.widget.*

import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import com.aliucord.Utils
import com.aliucord.patcher.after

import com.aliucord.utils.ReflectUtils


@AliucordPlugin
class FavoriteMedia : Plugin() {

    @SuppressLint("SetTextI18n")
    override fun start(context: Context) {
        val ctx = Utils.appContext

        val pickerBtnId = View.generateViewId()
        val star = Utils.tintToTheme(ContextCompat.getDrawable(ctx, R.e.ic_star_24dp)?.apply { mutate() })
        patcher.after<WidgetChatInputEditText>(FlexEditText::class.java, MessageDraftsRepo::class.java) {
            try {
                val editText = ReflectUtils.getField(this, "editText") as FlexEditText?
                val group = editText!!.parent as LinearLayout
                val emoteBtn = group.findViewById<AppCompatImageButton>(Utils.getResId("expression_btn", "id"))
                emoteBtn.setOnLongClickListener {
                    Utils.openPageWithProxy(Utils.appActivity, BottomShit())
                    return@setOnLongClickListener true
                }
            } catch (e: Throwable) {
                logger.error(e)
            }
        }

    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}