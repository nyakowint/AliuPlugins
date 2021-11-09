package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Constants
import com.aliucord.fragments.SettingsPage
import com.lytefast.flexinput.R


class Hamachi : SettingsPage() { // its funny
    @SuppressLint("SetTextI18n")
    override fun onViewBound(view: View) {
        super.onViewBound(view)
        val titleId = View.generateViewId()

        val ctx = view.context

        setActionBarTitle("")
        setActionBarSubtitle("")
        setActionBarTitleLayoutMinimumTappableArea()

        if (view.findViewById<TextView>(titleId) == null) {
            val title = TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header)
            title.id = titleId
            title.text = "Walciom to discord"
            title.typeface = ResourcesCompat.getFont(ctx, Constants.Fonts.ginto_bold)
            title.gravity = Gravity.CENTER
            addView(title)

            val rrId = View.generateViewId()

            @SuppressLint("SetJavaScriptEnabled")
            val funny = WebView(ctx).apply {
                id = rrId
                setBackgroundColor(Color.TRANSPARENT)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
            }
            addView(funny)
            funny.run {
                visibility = View.VISIBLE
                requestFocus()
                loadData(
                    """
                                <html>
                                    <body style="margin: 0; padding: 0;">
                                        <iframe src="https://www.youtube.com/embed/OjNpRbNdR7E?autoplay=0"
                                            width="100%" height="60%" frameborder="0"
                                            allowfullscreen allow="autoplay" />
                                    </body>
                                </html>
                            """,
                    "text/html",
                    "UTF-8"
                )
            }

            title.setOnClickListener {
                try {
                    ContextCompat.startActivity(
                        it.context,
                        Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:OjNpRbNdR7E")),
                        null
                    )
                } catch (ex: ActivityNotFoundException) {
                    ContextCompat.startActivity(
                        it.context,
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/watch?v=OjNpRbNdR7E")
                        ),
                        null
                    )
                }
            }

        }


    }
}