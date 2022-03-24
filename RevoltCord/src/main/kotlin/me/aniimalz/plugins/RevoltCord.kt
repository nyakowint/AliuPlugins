package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.aliucord.Utils
import com.aliucord.Utils.openPageWithProxy
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.Plugin
import com.aliucord.fragments.SettingsPage
import com.aliucord.views.Divider
import com.aliucord.views.TextInput
import com.discord.views.CheckedSetting

@AliucordPlugin
class RevoltCord : Plugin() {
    init {
        settingsTab = SettingsTab(RevoltCordSettings::class.java, SettingsTab.Type.PAGE)
    }

    @SuppressLint("SetJavaScriptEnabled")
    inner class Trolley(context: Context) : WebView(context) {
        @SuppressLint("ClickableViewAccessibility")
        override fun onTouchEvent(event: MotionEvent): Boolean {
            requestDisallowInterceptTouchEvent(true)
            return super.onTouchEvent(event)
        }

        init {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    return false
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                }
            }
        }
    }

    lateinit var revolt: Trolley

    inner class RevoltPage(private val instanceUrl: String) : SettingsPage() {
        override fun onViewBound(view: View) {
            super.onViewBound(view)
            headerBar.visibility = View.GONE
            setPadding(0)
            revolt = Trolley(view.context)
            revolt.loadUrl(instanceUrl)
            revolt.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            linearLayout.addView(revolt)
        }
    }

    override fun start(context: Context) {
        commands.registerCommand("revolt", "Revolt in Discord lol") {
            openPageWithProxy(
                Utils.appActivity,
                RevoltPage(
                    settings.getString("instance", "https://app.revolt.chat")
                        ?: "https://app.revolt.chat"
                )
            )
            CommandsAPI.CommandResult()
        }
        if (settings.getBool("startup", false)) {
            openPageWithProxy(
                Utils.appActivity,
                RevoltPage(
                    settings.getString("instance", "https://app.revolt.chat")
                        ?: "https://app.revolt.chat"
                )
            )
        }
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}

class RevoltCordSettings(private val settings: SettingsAPI) : SettingsPage() {
    override fun onViewBound(view: View) {
        super.onViewBound(view)
        val ctx = Utils.appContext
        addView(Utils.createCheckedSetting(
            ctx, CheckedSetting.ViewType.SWITCH, "Launch on startup", null
        ).apply {
            isChecked = settings.getBool("startup", false)
            setOnCheckedListener { settings.setBool("startup", it) }
        })
        addView(Divider(ctx))

        val instance = settings.getString("instance", "https://app.revolt.chat")

        addView(TextInput(ctx, "Revolt instance (leave blank for main)",
                instance?.let { instance.toString() } ?: "https://app.revolt.chat",
                object : TextWatcher { override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int) {
                    }
                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int) {
                    }
                    override fun afterTextChanged(s: Editable?) {
                        val txt = s.toString()
                        try {
                            settings.setString("instance", txt)
                        } catch (e: Throwable) {
                            Utils.showToast("Invalid URL")
                        }
                    }
                })
        )
    }
}
// h
