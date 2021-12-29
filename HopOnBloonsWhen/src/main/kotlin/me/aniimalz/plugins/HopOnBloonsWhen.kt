package me.aniimalz.plugins

import android.content.Context
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.utils.RxUtils.onBackpressureBuffer
import com.aliucord.utils.RxUtils.subscribe
import com.discord.models.message.Message
import com.discord.models.user.CoreUser
import com.discord.stores.StoreStream
import rx.Subscription
import java.util.*


@AliucordPlugin
class HopOnBloonsWhen : Plugin() {
    private var observable: Subscription? = null
    override fun start(ctx: Context) {
        observable = StoreStream.getGatewaySocket().messageCreate.onBackpressureBuffer().subscribe {
            if (this == null) return@subscribe
            val message = Message(this)
            if (CoreUser(message.author).id == StoreStream.getUsers().me.id) return@subscribe
            val content = message.content.lowercase()
            if (content.contains("hop on bloons") || content.contains("get on bloons") ||
                content.contains("com.ninjakiwi.bloonstd6")
            ) {
                try {
                    val bloons =
                        Utils.appContext.packageManager.getLaunchIntentForPackage("com.ninjakiwi.bloonstd6")
                    Utils.appContext.startActivity(bloons)
                } catch (ignored: Throwable) {
                    // nop nop nop nop
                }
            }
        }
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
        observable?.unsubscribe()
    }
}