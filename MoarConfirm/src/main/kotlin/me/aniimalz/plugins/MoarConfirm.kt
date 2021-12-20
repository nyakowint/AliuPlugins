package me.aniimalz.plugins

import android.content.Context
import android.widget.Button
import androidx.fragment.app.FragmentManager
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.fragments.ConfirmDialog
import com.aliucord.patcher.Hook
import com.aliucord.patcher.PreHook
import com.discord.app.AppComponent
import com.discord.app.AppPermissionsRequests
import com.discord.databinding.WidgetUserSheetBinding
import com.discord.stores.StoreStream
import com.discord.stores.StoreUser
import com.discord.stores.StoreUserRelationships
import com.discord.views.JoinVoiceChannelButton
import com.discord.widgets.user.calls.PrivateCallLauncher
import com.discord.widgets.user.usersheet.WidgetUserSheet
import com.discord.widgets.user.usersheet.WidgetUserSheetViewModel
import com.discord.widgets.voice.call.PrivateCallLaunchUtilsKt

@AliucordPlugin
class MoarConfirm : Plugin() {
    init {
        settingsTab =
            SettingsTab(MoarSettings::class.java, SettingsTab.Type.PAGE).withArgs(settings)
    }

    private var permRequests: AppPermissionsRequests? = null
    private var appComponent: AppComponent? = null
    private var callFragManager: FragmentManager? = null

    override fun start(ctx: Context) {
        val permReqField =
            PrivateCallLauncher::class.java.getDeclaredField("appPermissionsRequests")
                .apply { isAccessible = true }
        val appComponentField = PrivateCallLauncher::class.java.getDeclaredField("appComponent")
            .apply { isAccessible = true }
        val fragManangerField = PrivateCallLauncher::class.java.getDeclaredField("fragmentManager")
            .apply { isAccessible = true }
        patcher.patch(
            PrivateCallLauncher::class.java.getDeclaredMethod(
                "launchVoiceCall",
                Long::class.javaPrimitiveType
            ), PreHook {
                if (!settings.getBool("callConfirm", true)) return@PreHook
                permRequests = permReqField.get(it.thisObject) as AppPermissionsRequests
                appComponent = appComponentField.get(it.thisObject) as AppComponent
                callFragManager = fragManangerField.get(it.thisObject) as FragmentManager
                val userId = it.args[0] as Long
                val confirmCall = ConfirmDialog().apply {
                    setTitle("Confirm Call")
                    setOnOkListener {
                        dismiss()
                        callUser(ctx, userId, false)
                    }
                    setDescription("Do you really want to call this user?")
                    setOnCancelListener { dismiss() }
                }
                confirmCall.show(callFragManager!!, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                @Suppress("UsePropertyAccessSyntax") // go fuck yourself thanks
                it.setResult(null)
            })

        patcher.patch(
            PrivateCallLauncher::class.java.getDeclaredMethod(
                "launchVideoCall",
                Long::class.javaPrimitiveType
            ), PreHook {
                if (!settings.getBool("callConfirm", true)) return@PreHook
                permRequests = permReqField.get(it.thisObject) as AppPermissionsRequests
                appComponent = appComponentField.get(it.thisObject) as AppComponent
                callFragManager = fragManangerField.get(it.thisObject) as FragmentManager
                val userId = it.args[0] as Long
                val confirmCall = ConfirmDialog().apply {
                    setTitle("Confirm Video Call")
                    setOnOkListener {
                        dismiss()
                        callUser(ctx, userId, true)
                    }
                    setDescription("Do you really want to video call this user?")
                    setOnCancelListener { dismiss() }
                }
                confirmCall.show(callFragManager!!, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                @Suppress("UsePropertyAccessSyntax") // go fuck yourself thanks
                it.setResult(null)
            })

        with(WidgetUserSheet::class.java) {
            val actionButtons = getDeclaredMethod(
                "configureProfileActionButtons",
                WidgetUserSheetViewModel.ViewState.Loaded::class.java
            )
            actionButtons.apply { isAccessible = true }
            patcher.patch(actionButtons, Hook {

                val addFriendId = Utils.getResId("user_sheet_add_friend_action_button", "id")
                val userSheet = it.thisObject as WidgetUserSheet

                val root = userSheet.javaClass.getDeclaredMethod("getBinding").let { m ->
                    m.isAccessible = true
                    m.invoke(userSheet) as WidgetUserSheetBinding
                }.root
                val loaded = it.args[0] as WidgetUserSheetViewModel.ViewState.Loaded
                val confirmBtn = root.findViewById<Button>(addFriendId)
                val confirmAddFriend = ConfirmDialog().apply {
                    setTitle("Add Friend")
                    setOnOkListener { _ ->
                        dismiss()
                        val friend = userSheet.javaClass.getDeclaredMethod("addFriend", String::class.javaObjectType)
                        friend.apply { isAccessible = true }
                        friend.invoke(userSheet, "${loaded.user.username}#${loaded.user.discriminator}")
                    }
                    setDescription("Do you really want to friend this user?")
                    setOnCancelListener { _ ->
                        dismiss()
                        it.result = null
                    }
                }
                confirmBtn.setOnClickListener {
                    if (!settings.getBool("friendConfirm", true)) {
                        val friend = userSheet.javaClass.getDeclaredMethod("addFriend", String::class.javaObjectType)
                        friend.apply { isAccessible = true }
                        friend.invoke(userSheet, "${loaded.user.username}#${loaded.user.discriminator}")
                        return@setOnClickListener
                    }
                    confirmAddFriend.show(
                        userSheet.parentFragmentManager,
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                    )
                }
            })
        }
    }

    private fun callUser(ctx: Context, user: Long, isVideo: Boolean) {
        PrivateCallLaunchUtilsKt.callAndLaunch(
            user,
            isVideo,
            permRequests,
            ctx,
            appComponent,
            callFragManager
        )
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}