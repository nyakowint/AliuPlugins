package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.core.content.ContextCompat
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI
import com.aliucord.entities.Plugin
import com.aliucord.utils.RxUtils.onBackpressureBuffer
import com.aliucord.utils.RxUtils.subscribe
import com.discord.api.commands.ApplicationCommandType
import com.discord.models.message.Message
import com.discord.models.user.CoreUser
import com.discord.models.user.User
import com.discord.stores.StoreStream
import com.lytefast.flexinput.R
import rx.Subscription
import kotlin.random.Random

@AliucordPlugin
class Fart : Plugin() {

    var pluginIcon: Drawable? = null
    private var observable: Subscription? = null
    @SuppressLint("SetTextI18n")
    override fun start(ctx: Context) {
        val options = listOf(
            Utils.createCommandOption(
                ApplicationCommandType.USER,
                "user", "The farting user, or fartee (bonus points if ven)", null,
                required = false,
                default = true,
                channelTypes = emptyList(),
                choices = emptyList(),
                subCommandOptions = emptyList(),
                autocomplete = false
            )
        )
        pluginIcon = ContextCompat.getDrawable(Utils.appContext, R.e.drawable_thumb_white)

        // hi ven :3
        commands.registerCommand("fart", "Tell someone to fart", options) {
            if (it.containsArg("user")) {
                fart()
                CommandsAPI.CommandResult("<@${it.getRequiredUser("user").id}> fart", null, true)
            } else {
                fart()
                CommandsAPI.CommandResult("fart", null, true)
            }
        }

        commands.registerCommand(
            "betterfart",
            "fart but better",
            options
        ) {
            if (it.mentionedUsers.count() > 0) {
                CommandsAPI.CommandResult(
                    "Hello <@${it.getRequiredUser("user").id}>, do you possibly think, that you could, potentially in the near future, fart? It would be monumental to everyone's experience on Aliucord™. Have a fart day!",
                    null,
                    true
                )
            } else {
                CommandsAPI.CommandResult(
                    "Hello everyone, do you possibly think, that you could, potentially in the near future, fart? It would be monumental to your experience on Aliucord™. Have a fart day!",
                    null,
                    true
                )
            }
        }

        commands.registerCommand("venmybeloved", "VEN HOW DO I GET FREE NITRO", listOf()) {
            CommandsAPI.CommandResult("VEN HOW DO I GET FREE NITRO", null, true)
        }

        observable = StoreStream.getGatewaySocket().messageCreate.onBackpressureBuffer().subscribe {
            if (this == null) return@subscribe
            val message = Message(this)
            val mentions = message.mentions
            if (message.channelId != StoreStream.getChannelsSelected().id) return@subscribe
            mentions.forEach {
                if (CoreUser(it).id == StoreStream.getUsers().me.id) fart()
            }
        }
    }

    private fun fart() {
        MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource("https://github.com/ItzOnlyAnimal/AliuPlugins/raw/main/fart.mp3")
            prepare()
            start()
        }
    }

    // bruh
    override fun stop(ctx: Context) {
        commands.unregisterAll()
        observable?.unsubscribe()
    }
}