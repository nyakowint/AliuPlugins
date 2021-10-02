package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import com.aliucord.Http
import com.aliucord.Main.logger
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI
import com.aliucord.entities.CommandContext
import com.aliucord.entities.MessageEmbedBuilder
import com.aliucord.entities.Plugin
import com.discord.api.commands.ApplicationCommandType
import com.discord.api.utcdatetime.UtcDateTime
import com.discord.models.commands.ApplicationCommandOption
import java.text.SimpleDateFormat

@AliucordPlugin
class xkcd : Plugin() {
    @SuppressLint("SetTextI18n")
    override fun start(context: Context?) {
        val url = "https://xkcd.com"
        val comicNum = ApplicationCommandOption(
            ApplicationCommandType.NUMBER,
            "num",
            "# of the comic to view",
            null,
            false,
            false,
            null,
            null
        )
        commands.registerCommand(
            "xkcd",
            "View a comic from xkcd.com",
            listOf(comicNum)
        ) { ctx: CommandContext ->
            val comicNumber = ctx.getLong("num")
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
                    .setTimestamp(UtcDateTime(SimpleDateFormat("dd-MM-yyyy").parse("${comic.day}-${comic.month}-${comic.year}").time))
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
                    .setTimestamp(UtcDateTime(SimpleDateFormat("dd-MM-yyyy").parse("${comic.day}-${comic.month}-${comic.year}").time))
                CommandsAPI.CommandResult(null, listOf(embed.build()), false)
            }
        }
    }

    override fun stop(context: Context?) {
        commands.unregisterAll()
    }
}

class Response(
    val month: String,
    val num: Number,
    val link: String,
    val year: String,
    val news: String,
    val safe_title: String,
    val transcript: String,
    val alt: String,
    val img: String,
    val title: String,
    val day: String
)