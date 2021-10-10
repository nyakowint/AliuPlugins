package me.aniimalz.plugins

import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI
import com.aliucord.entities.Plugin
import com.discord.api.premium.PremiumTier
import com.discord.stores.StoreStream

@AliucordPlugin
class FreeNitro : Plugin() {
    override fun start(ctx: Context) {
        commands.registerCommand("nitro", "Free nitro! Totally not a scam 10% legit") {
            if (StoreStream.getUsers().me.premiumTier == PremiumTier.TIER_1 || StoreStream.getUsers().me.premiumTier == PremiumTier.TIER_2) {
                CommandsAPI.CommandResult("I installed the FreeNitro plugin and it worked exactly as advertised! Everything worked great without a hitch and I'm quite satisfied! Thanks Aliucord discord server! You guys are okay in my book! ☺ ❤", null, true)
            } else {
                CommandsAPI.CommandResult(
                    "I installed the FreeNitro plugin probably expecting nitro features for free, but now know that such things are not possible! What an epic realization <:posttroll:859798445870809108> <:iloveccp:892176962934689822><:iloveccp:892176962934689822> \uD83C\uDDE8\uD83C\uDDF3",
                    null,
                    true
                )
            }
        }
    }

    override fun stop(context: Context) {
        commands.unregisterAll()
    }
}
