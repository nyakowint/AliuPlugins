package me.aniimalz.plugins

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.discord.widgets.user.usersheet.WidgetUserSheet
import com.discord.widgets.user.usersheet.WidgetUserSheetViewModel

@AliucordPlugin
class MoarConfirm : Plugin() {
    init {
        settingsTab =
            SettingsTab(MoarSettings::class.java, SettingsTab.Type.PAGE).withArgs(settings)
    }

    val handler = Handler(Looper.getMainLooper())
    override fun start(ctx: Context) {
        val userSheetCallButton = Utils.getResId("user_sheet_call_action_button", "id")
        val userSheetVideoButton = Utils.getResId("user_sheet_video_action_button", "id")
        val userSheetFriendButton = Utils.getResId("user_sheet_add_friend_action_button", "id")
        patcher.patch(
            WidgetUserSheet::class.java.getDeclaredMethod(
                "configureProfileActionButtons",
                WidgetUserSheetViewModel.ViewState.Loaded::class.java
            ), Hook {

            })
    }

    fun View.clickAgain() {

    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}