package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.patcher.InsteadHook
import com.aliucord.utils.ReflectUtils
import com.discord.api.premium.PremiumTier
import com.discord.databinding.WidgetChatListAdapterItemEphemeralMessageBinding
import com.discord.models.message.Message
import com.discord.stores.StoreStream
import com.discord.utilities.icon.IconUtils
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemEphemeralMessage
import com.discord.widgets.chat.list.entries.ChatListEntry
import kotlin.system.exitProcess

import android.R.attr.author
import android.app.Notification
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aliucord.*
import com.discord.utilities.fcm.NotificationClient
import com.lytefast.flexinput.R
import java.io.File


@AliucordPlugin(requiresRestart = true)
class FreeNitroll : Plugin() {
    @SuppressLint("SetTextI18n")
    override fun start(ctx: Context) {
        File(Constants.PLUGINS_PATH, "FreeNitroll.zip").delete()
        Utils.promptRestart()
    }

    override fun stop(context: Context) {
        commands.unregisterAll()
        patcher.unpatchAll()
    }
}
