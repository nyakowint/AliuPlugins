package me.aniimalz.plugins

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.aliucord.Utils
import com.discord.app.AppDialog

@SuppressWarnings("SetTextI18n")
class ConnectionDetails(private val platform: String, private val username: String, val icon: Drawable, val url: String?) : AppDialog(Utils.getResId("connected_account_actions_dialog", "layout")) {
    override fun onViewBound(view: View) {
        super.onViewBound(view)
        val header = view.findViewById(Utils.getResId("connected_account_actions_dialog_header", "id")) as TextView
        val layout = header.parent as LinearLayout

        header.text = "$username ($platform)"
        header.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null)

        val copy = view.findViewById(Utils.getResId("connected_account_actions_dialog_copy_username", "id")) as TextView
        val open = view.findViewById(Utils.getResId("connected_account_actions_dialog_open_in_browser", "id")) as TextView


        if (url != null) {
            open.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    view.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    dismiss()
                }

            }
        }


        copy.apply {
            setOnClickListener {
                Utils.setClipboard(platform, username)
                Utils.showToast("$platform username copied!")
                dismiss()
            }
        }

    }

}
