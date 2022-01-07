package me.aniimalz.plugins

import android.content.Context
import android.view.MenuItem
import android.widget.Button
import androidx.fragment.app.FragmentManager
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.fragments.ConfirmDialog
import com.aliucord.patcher.*
import com.aliucord.wrappers.GuildRoleWrapper.Companion.name
import com.discord.app.AppComponent
import com.discord.app.AppPermissionsRequests
import com.discord.databinding.WidgetUserSheetBinding
import com.discord.widgets.servers.`WidgetServerSettingsEditRole$setupMenu$1`
import com.discord.widgets.user.calls.PrivateCallLauncher
import com.discord.widgets.user.usersheet.WidgetUserSheet
import com.discord.widgets.user.usersheet.WidgetUserSheetViewModel
import com.discord.widgets.voice.call.PrivateCallLaunchUtilsKt
import de.robv.android.xposed.XposedBridge

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
        patchCallButtons()
        patchUserSheet()
        patchRoleContext()
    }

    private fun patchRoleContext() {
        // confirmation for deleting roles
        if (!settings.getBool("deleteRoleConfirm", true)) return
        val args = arrayOf(MenuItem::class.java, Context::class.java)
        patcher.before<`WidgetServerSettingsEditRole$setupMenu$1`<MenuItem, Context>>(
            "call",
            *args
        ) {
            val uh = this.`$data`.role
            val cf = ConfirmDialog().apply {
                setTitle("Delete Role")
                setDescription("Do you want to delete ${uh.name}?")
                setOnOkListener { _ ->
                    XposedBridge.invokeOriginalMethod(
                        `WidgetServerSettingsEditRole$setupMenu$1`::class.java.getDeclaredMethod(
                            "call",
                            *args
                        ),
                        it.thisObject,
                        arrayOf(it.args[0], it.args[1])
                    )
                }
            }
            cf.show(Utils.appActivity.supportFragmentManager, "confirm_delete_role")
            it.result = null
        }
    }

    private fun patchCallButtons() {
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
                        callUser(Utils.appContext, userId, false)
                    }
                    setDescription("Do you really want to call this user?")
                    setOnCancelListener { dismiss() }
                }
                confirmCall.show(callFragManager!!, "call_confirmation")
                @Suppress("UsePropertyAccessSyntax")
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
                        callUser(Utils.appContext, userId, true)
                    }
                    setDescription("Do you really want to video call this user?")
                    setOnCancelListener { dismiss() }
                }
                confirmCall.show(callFragManager!!, "call_confirmation")
                @Suppress("UsePropertyAccessSyntax")
                it.setResult(null)
            })
    }

    private fun patchUserSheet() {
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
                    setOnOkListener {
                        dismiss()
                        val friend = userSheet.javaClass.getDeclaredMethod(
                            "addFriend",
                            String::class.javaObjectType
                        )
                        friend.apply { isAccessible = true }
                        friend.invoke(
                            userSheet,
                            "${loaded.user.username}#${loaded.user.discriminator}"
                        )
                    }
                    setDescription("Do you really want to friend this user?")
                    setOnCancelListener { _ ->
                        dismiss()
                        it.result = null
                    }
                }
                confirmBtn.setOnClickListener {
                    if (!settings.getBool("friendConfirm", true)) {
                        val friend = userSheet.javaClass.getDeclaredMethod(
                            "addFriend",
                            String::class.javaObjectType
                        )
                        friend.apply { isAccessible = true }
                        friend.invoke(
                            userSheet,
                            "${loaded.user.username}#${loaded.user.discriminator}"
                        )
                        return@setOnClickListener
                    }
                    confirmAddFriend.show(
                        userSheet.parentFragmentManager,
                        "friend_confirmation"
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

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}