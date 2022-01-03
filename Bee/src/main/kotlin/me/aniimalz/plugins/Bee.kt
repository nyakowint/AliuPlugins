package me.aniimalz.plugins

import android.app.Notification
import android.content.Context
import android.graphics.drawable.Icon
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.discord.utilities.fcm.NotificationClient
import com.lytefast.flexinput.R

@AliucordPlugin
class Bee : Plugin() {
    override fun start(ctx: Context) {
        var ind = 0
        for (i: Int in bee.indices.reversed()) {
            val notif = NotificationCompat.Builder(ctx, NotificationClient.NOTIF_GENERAL_HIGH_PRIO).apply {
                setAutoCancel(false)
                setCategory(Notification.CATEGORY_ALARM)
                setContentTitle(bee[i])
                setSmallIcon(R.e.img_logo)
                ind++
                setContentText(bee[i])
                ind++
            }
            NotificationManagerCompat.from(ctx).notify(ind+30, notif.build())
        }
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}