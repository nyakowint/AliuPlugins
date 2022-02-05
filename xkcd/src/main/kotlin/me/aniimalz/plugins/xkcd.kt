package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.aliucord.Http
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI
import com.aliucord.entities.CommandContext
import com.aliucord.entities.MessageEmbedBuilder
import com.aliucord.entities.Plugin
import com.discord.api.commands.ApplicationCommandType
import com.discord.api.utcdatetime.UtcDateTime
import com.lytefast.flexinput.R
import java.text.SimpleDateFormat

@AliucordPlugin
class xkcd : Plugin() {

    var pluginIcon: Drawable? = null
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun start(ctx: Context) {
        pluginIcon = ContextCompat.getDrawable(Utils.appContext, R.e.ic_search)
        val url = "https://xkcd.com"
        val comicNum = Utils.createCommandOption(
            ApplicationCommandType.NUMBER,
            "num",
            "# of the comic to view",
            null,
            false,
            default = false,
            channelTypes = emptyList(),
            choices = emptyList(), subCommandOptions = emptyList(), autocomplete = false
        )
        commands.registerCommand(
            "xkcd",
            "View a comic from xkcd.com",
            listOf(comicNum)
        ) { cctx: CommandContext ->
            val comicNumber = cctx.getLong("num")
            val comic: Response
            if (comicNumber == null) { // idk if it returns null or 0 if empty
                comic = try {
                    Http.simpleJsonGet("$url/info.0.json", Response::class.java)

                } catch (throwable: Throwable) {
                    logger.error(throwable)
                    return@registerCommand CommandsAPI.CommandResult("Well the comic couldn't be fetched, sorry lol")
                }
                val embed = MessageEmbedBuilder().setTitle(comic.title)
                    .setUrl("$url/${comic.num}")
                    .setDescription(comic.transcript)
                    .setImage(comic.img, comic.img, 720, 1280)
                    .setFooter(comic.alt)
                    .setTimestamp(SimpleDateFormat("dd-MM-yyyy").parse("${comic.day}-${comic.month}-${comic.year}")?.time?.let {
                        UtcDateTime(
                            it
                        )
                    })
                CommandsAPI.CommandResult(null, listOf(embed.build()), false)
            } else {
                comic = try {
                    Http.simpleJsonGet("$url/$comicNumber/info.0.json", Response::class.java)

                } catch (throwable: Throwable) {
                    logger.error(throwable)
                    return@registerCommand CommandsAPI.CommandResult("Well the comic couldn't be fetched, sorry lol")
                }
                val embed = MessageEmbedBuilder().setTitle(comic.title)
                    .setUrl("$url/${comic.num}")
                    .setDescription(comic.transcript)
                    .setImage(comic.img, comic.img, 720, 1280)
                    .setFooter(comic.alt)
                    .setTimestamp(SimpleDateFormat("dd-MM-yyyy").parse("${comic.day}-${comic.month}-${comic.year}")?.time?.let {
                        UtcDateTime(
                            it
                        )
                    })
                CommandsAPI.CommandResult(null, listOf(embed.build()), false)
            }
        }
    }

    override fun stop(ctx: Context) {
        commands.unregisterAll()
    }
}

class Response(
    val month: String,
    val num: Number,
    val year: String,
    val transcript: String,
    val alt: String,
    val img: String,
    val title: String,
    val day: String
)