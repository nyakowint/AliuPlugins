package me.aniimalz.plugins

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.fragments.ConfirmDialog
import com.aliucord.patcher.PreHook
import com.discord.app.AppComponent
import com.discord.app.AppPermissionsRequests
import com.discord.widgets.user.calls.PrivateCallLauncher
import com.discord.widgets.voice.call.PrivateCallLaunchUtilsKt

@AliucordPlugin
class MoarConfirm : Plugin() {
    init {
        settingsTab =
            SettingsTab(MoarSettings::class.java, SettingsTab.Type.PAGE).withArgs(settings)
    }

    private var permRequests: AppPermissionsRequests? = null
    private var appComponent: AppComponent? = null
    private var fragManager: FragmentManager? = null

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
                fragManager = fragManangerField.get(it.thisObject) as FragmentManager
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
                confirmCall.show(fragManager!!, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
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
                fragManager = fragManangerField.get(it.thisObject) as FragmentManager
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
                confirmCall.show(fragManager!!, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                @Suppress("UsePropertyAccessSyntax") // go fuck yourself thanks
                it.setResult(null)
            })
    }

    private fun callUser(ctx: Context, user: Long, isVideo: Boolean) {
        PrivateCallLaunchUtilsKt.callAndLaunch(user, isVideo, permRequests, ctx, appComponent, fragManager)
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}