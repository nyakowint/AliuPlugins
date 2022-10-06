package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.aliucord.*
import com.aliucord.fragments.SettingsPage
import com.discord.utilities.analytics.AnalyticSuperProperties
import com.discord.utilities.rest.RestAPI.AppHeadersProvider
import com.lytefast.flexinput.R

class SessionsPage() : SettingsPage() {
    val guh = Logger("SessionsPage")

    @SuppressLint("SetTextI18n")
    override fun onViewBound(view: View?) {
        super.onViewBound(view)
        setActionBarTitle("Devices")
        val ctx = requireContext()

        TextView(ctx, null, 0, R.i.UiKit_Settings_Item_SubText).apply {
            text = """
                Here are all the devices that are currently logged in with your Discord account. You can log out of each one individually or all other devices.
                 
                If you see an entry you don't recognize, log out of that device and change your Discord account password immediately.
            """.trimIndent()
            isAllCaps = false
            typeface = ResourcesCompat.getFont(ctx, Constants.Fonts.ginto_regular)
            gravity = Gravity.CENTER
            addView(this)
        }

        TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header).apply {
            text = "Current Device"
            typeface = ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_semibold)
            gravity = Gravity.START
            addView(this)
        }

        TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header).apply {
            text = "Other Devices"
            typeface = ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_semibold)
            gravity = Gravity.START
            addView(this)
        }

        Utils.threadPool.execute {
            val req = Http.Request("https://discord.com/api/v9/auth/sessions", "GET").apply {
                setHeader("User-Agent", AppHeadersProvider.INSTANCE.userAgent)
                    .setHeader(
                        "X-Super-Properties",
                        "eyJvcyI6ImlPUyIsImJyb3dzZXIiOiJEaXNjb3JkIGlPUyIsImRldmljZSI6ImlQYWQ4LDkiLCJzeXN0ZW1fbG9jYWxlIjoiZW4tVVMiLCJjbGllbnRfdmVyc2lvbiI6IjE0MS4wIiwicmVsZWFzZV9jaGFubmVsIjoicHRiIiwiZGV2aWNlX3ZlbmRvcl9pZCI6Ijk5NzE5NERGLUVBNkEtNDE2Ri1CQTkyLUZBMDU4NEU1M0RGQiIsImJyb3dzZXJfdXNlcl9hZ2VudCI6IiIsImJyb3dzZXJfdmVyc2lvbiI6IiIsIm9zX3ZlcnNpb24iOiIxNS4yIiwiY2xpZW50X2J1aWxkX251bWJlciI6MzQ3MDcsImNsaWVudF9ldmVudF9zb3VyY2UiOm51bGx9"
                    )
                    .setHeader("Accept", "*/*")
                    .setHeader("Authorization", AppHeadersProvider.INSTANCE.authToken)
            }.execute()
            guh.info(req.text())
        }
    }
}