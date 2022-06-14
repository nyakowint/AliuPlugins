package me.aniimalz.plugins

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.view.View
import com.aliucord.Main
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.after
import com.aliucord.utils.RxUtils.onBackpressureBuffer
import com.aliucord.utils.RxUtils.subscribe
import com.discord.api.message.reaction.MessageReactionUpdate
import com.discord.models.domain.ModelUserSettings
import com.discord.models.message.Message
import com.discord.stores.StoreMessageReactions
import com.discord.stores.StoreStream
import com.discord.widgets.settings.WidgetSettingsAppearance
import com.discord.widgets.settings.`WidgetSettingsAppearance$updateTheme$1`
import rx.Subscription
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.system.exitProcess

@AliucordPlugin
class Moyai : Plugin() {
    private var observable: Subscription? = null
    override fun start(ctx: Context) {
        patcher.after<StoreMessageReactions>(
            "handleReactionAdd",
            MessageReactionUpdate::class.java
        ) {
            val r = it.args[0] as MessageReactionUpdate
            if (r.b().d() != "\uD83D\uDDFF") return@after
            if (r.a() != StoreStream.getChannelsSelected().id) return@after
            funny()
        }
        observable = StoreStream.getGatewaySocket().messageCreate.onBackpressureBuffer().subscribe {
            if (this == null) return@subscribe
            val message = Message(this)
            val content = message.content.lowercase()
            if (message.channelId != StoreStream.getChannelsSelected().id) return@subscribe
            if (content.contains("🗿") || content.contains("vine boom")) funny()
        }

    }

    private fun funny() {
        try {
            Utils.threadPool.execute {
                MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource("https://raw.githubusercontent.com/ItzOnlyAnimal/AliuPlugins/main/boom.ogg")
                    prepare()
                    start()
                }
            }
        } catch (ignored: Throwable) {
            // nop nop nop nop
        }
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
        observable?.unsubscribe()
    }
}
