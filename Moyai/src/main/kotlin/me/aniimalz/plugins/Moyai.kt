package me.aniimalz.plugins

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.utils.RxUtils.onBackpressureBuffer
import com.aliucord.utils.RxUtils.subscribe
import com.discord.models.message.Message
import com.discord.stores.StoreStream
import rx.Subscription

@AliucordPlugin
class Moyai : Plugin() {
    private var observable: Subscription? = null
    override fun start(ctx: Context) {
        observable = StoreStream.getGatewaySocket().messageCreate.onBackpressureBuffer().subscribe {
            if (this == null) return@subscribe
            val message = Message(this)
            val content = message.content.lowercase()
            if (message.channelId != StoreStream.getChannelsSelected().id) return@subscribe
            if (content.contains("🗿") || content.contains("vine boom")
            ) {
                try {
                    MediaPlayer().apply {
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                        )
                        setDataSource("https://github.com/ItzOnlyAnimal/AliuPlugins/raw/main/boom.ogg")
                        prepare()
                        start()
                    }
                } catch (ignored: Throwable) {
                    // nop nop nop nop
                }
            }
        }
    }

    override fun stop(ctx: Context) {
        observable?.unsubscribe()
    }
}