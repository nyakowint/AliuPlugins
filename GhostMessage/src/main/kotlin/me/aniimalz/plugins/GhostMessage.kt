package me.aniimalz.plugins

import android.content.Context
import com.aliucord.Logger
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI
import com.aliucord.entities.Plugin
import com.aliucord.utils.RxUtils.createActionSubscriber
import com.aliucord.utils.RxUtils.onBackpressureBuffer
import com.aliucord.utils.RxUtils.subscribe
import com.discord.api.commands.ApplicationCommandType
import com.discord.models.message.Message
import com.discord.models.user.CoreUser
import com.discord.stores.StoreStream
import rx.Subscription
import rx.functions.Action1
import java.util.*

@AliucordPlugin
class GhostMessage : Plugin() {

    private val logger = Logger("GhostMessage")

    private var messagesSubscription: Subscription? = null

    init {
        settingsTab = SettingsTab(
            PluginSettings::class.java,
            SettingsTab.Type.BOTTOM_SHEET
        ).withArgs(settings)
    }

    override fun start(ctx: Context) {
        commands.registerCommand(
            "ghostmessage",
            "Toggle ghost message",
            Utils.createCommandOption(
                ApplicationCommandType.BOOLEAN,
                "enabled",
                null,
                null,
                true,
                default = false
            )
        ) {
            if (!it.getRequiredBool("enabled")) {
                settings.setBool("ghostMessages", false)
                logger.info("Disabled!")
                CommandsAPI.CommandResult("Disabled GhostMessage", null, false)
            } else {
                settings.setBool("ghostMessages", true)
                logger.info("Enabled!")
                CommandsAPI.CommandResult("Enabled GhostMessage", null, false)
            }
        }
        messagesSubscription =
            StoreStream.getGatewaySocket().messageCreate.onBackpressureBuffer().subscribe(
                createActionSubscriber(Action1 { msg ->
                    if (msg == null || !settings.getBool("ghostMessages", false)) return@Action1
                    val ref = Message(msg)
                    val user = CoreUser(ref.author)
                    if (ref.editedTimestamp == null && user.id == StoreStream.getUsers().me.id && StoreStream.getChannelsSelected().id == ref.channelId) {
                        StoreStream.getMessages().deleteMessage(ref)
                    }
                })
            )
    }

    override fun stop(context: Context) {
        commands.unregisterAll()
        messagesSubscription?.unsubscribe()
    }
}