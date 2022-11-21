package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import b.a.a.i
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.patcher.before
import com.discord.api.connectedaccounts.ConnectedAccount
import com.discord.databinding.WidgetUserSheetBinding
import com.discord.stores.StoreStream
import com.discord.utilities.platform.Platform
import com.discord.widgets.user.profile.UserProfileConnectionsView
import com.discord.widgets.user.profile.UserProfileConnectionsView.ConnectedAccountItem
import com.discord.widgets.user.usersheet.WidgetUserSheet
import com.discord.widgets.user.usersheet.WidgetUserSheetViewModel
import com.lytefast.flexinput.R
import com.lytefast.flexinput.R.h.account
import com.lytefast.flexinput.R.h.connect
import java.util.Locale

@AliucordPlugin
class UnknownConnectionIcons : Plugin() {
    init {
        needsResources = true
    }

    @SuppressLint("SetTextI18n")
    override fun start(ctx: Context) {

        lateinit var userSheetBinding: WidgetUserSheetBinding
        lateinit var fragment: FragmentManager
        patcher.after<WidgetUserSheet>(
            "configureNote",
            WidgetUserSheetViewModel.ViewState.Loaded::class.java
        ) {
            val loaded = it.args[0] as WidgetUserSheetViewModel.ViewState.Loaded
            val user = loaded.user
            if (user == null || user.isBot) return@after
            fragment = parentFragmentManager
            userSheetBinding =
                WidgetUserSheet.`access$getBinding$p`(it.thisObject as WidgetUserSheet)
        }

        patcher.before<UserProfileConnectionsView.ViewHolder>(
            "onConfigure",
            Int::class.java,
            ConnectedAccountItem::class.java
        ) {
            val connectedAccount = (it.args[1] as ConnectedAccountItem).connectedAccount
            val account = (Platform.Companion).from(connectedAccount)
            if (account != Platform.NONE) return@before

            val guh = this.itemView as AppCompatTextView

            val icon = determineTheme(ctx, connectedAccount)
            guh.text = connectedAccount.d() // name
            guh.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null)

            guh.setOnClickListener {
                ConnectionDetails(
                    connectedAccount.g(),
                    connectedAccount.d(),
                    icon,
                    getUrl(connectedAccount)
                ).show(fragment, "ConnectionDetails")
            }
            it.result = null;
        }
    }

    private fun determineTheme(ctx: Context, account: ConnectedAccount): Drawable {
        var result: Drawable? = null
        try {
            val icon = if (StoreStream.getUserSettingsSystem().theme == "dark") {
                ResourcesCompat.getDrawable(
                    resources,
                    resources.getIdentifier(account.g(), "drawable", "me.aniimalz.plugins"), null
                )
            } else {
                ResourcesCompat.getDrawable(
                    resources,
                    resources.getIdentifier("${account.g()}_light", "drawable", "me.aniimalz.plugins"),
                    null
                )
            }
            result = icon
        } catch (e: Exception) {
            if (e is Resources.NotFoundException) {
                return ContextCompat.getDrawable(ctx, R.e.ic_activity_status_24dp)!!.apply {
                    mutate()
                    Utils.tintToTheme(this)
                }
            }
            logger.error(e)
        }
        return result ?: ContextCompat.getDrawable(ctx, R.e.ic_activity_status_24dp)!!.apply {
            mutate()
            Utils.tintToTheme(this)
        }
    }

    private fun getUrl(account: ConnectedAccount): String? {
        return when (account.g()) {
            "ebay" -> "https://www.ebay.com/usr/${account.d()}"
            "instagram" -> "https://www.instagram.com/${account.d()}"
            "tiktok" -> "https://www.tiktok.com/@${account.d()}"
            else -> null
        }
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
    }
}