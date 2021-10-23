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

class WidgetFreeNitro : SettingsPage() { // yes i kept up the name cause its funny
    @SuppressLint("SetTextI18n")
    override fun onViewBound(view: View) {
        super.onViewBound(view)
        val titleId = View.generateViewId()

        val ctx = view.context

        setActionBarTitle("Manage Subscription")
        setActionBarSubtitle("Discord Nitro - DN Injector v2.43")

        if (view.findViewById<TextView>(titleId) == null) {
            val title = TextView(ctx, null, 0, R.h.UiKit_Settings_Item_Header)
            title.id = titleId
            title.text = "FreeNitro injected successfully!"
            title.typeface = ResourcesCompat.getFont(ctx, Constants.Fonts.ginto_bold)
            title.gravity = Gravity.CENTER
            addView(title)

            val urStupid = TextView(ctx, null, 0, R.h.UiKit_TextView)
            urStupid.id = titleId + 1
            urStupid.text = "enjoy your free nitro ;)"
            urStupid.typeface = ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_semibold)
            urStupid.gravity = Gravity.CENTER - 100
            addView(urStupid)

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
            }
            addView(funny)
            funny.run {
                visibility = View.VISIBLE
                loadData(
                    """
                                <html>
                                    <body style="margin: 0; padding: 0;">
                                        <iframe
                                            src="https://youtube.com/embed/48rz8udZBmQ?autoplay=1"
                                            width="100%"
                                            height="60%"
                                            frameborder="0"
                                            allow="encrypted-media"
                                            allow="autoplay"
                                            allow="transparency"
                                            allow="fullscreen" 
                                            />
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
                        Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:vIefYWT7jNo")),
                        null
                    )
                } catch (ex: ActivityNotFoundException) {
                    ContextCompat.startActivity(
                        it.context,
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/watch?v=vIefYWT7jNo")
                        ),
                        null
                    )
                }
            }

        }


    }
}