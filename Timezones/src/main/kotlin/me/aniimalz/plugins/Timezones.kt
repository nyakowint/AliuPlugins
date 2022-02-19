package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Constants
import com.aliucord.Http
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.fragments.SelectDialog
import com.aliucord.patcher.after
import com.aliucord.utils.DimenUtils
import com.discord.models.message.Message
import com.discord.models.user.CoreUser
import com.discord.models.user.User
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import com.discord.widgets.user.usersheet.WidgetUserSheet
import com.discord.widgets.user.usersheet.WidgetUserSheetViewModel
import com.google.gson.reflect.TypeToken
import com.lytefast.flexinput.R
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.*
import java.util.*


@SuppressLint("SimpleDateFormat")
val format12 = SimpleDateFormat("hh:mm a")

@SuppressLint("SimpleDateFormat")
val format24 = SimpleDateFormat("HH:mm")

@AliucordPlugin
class Timezones : Plugin() {
    init {
        settingsTab = SettingsTab(
                PluginSettings::class.java,
                SettingsTab.Type.PAGE
        ).withArgs(settings)
    }

    private val tzId = View.generateViewId()
    var pluginIcon: Drawable? = null

    companion object {
        var usersList: HashMap<Long, String> = HashMap<Long, String>()
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun start(context: Context) {
        pluginIcon = ContextCompat.getDrawable(Utils.appContext, R.e.ic_clock_24dp)
        val clock = ContextCompat.getDrawable(Utils.appContext, R.e.ic_archived_clock_dark)?.apply {
            mutate()
        }
        usersList = settings.getObject(
                "usersList", HashMap<Long, String>(), // userid, timezone
                TypeToken.getParameterized(
                        HashMap::class.java,
                        Long::class.javaObjectType,
                        String::class.javaObjectType
                ).type
        )

        patcher.after<WidgetUserSheet>(
                "configureNote",
                WidgetUserSheetViewModel.ViewState.Loaded::class.java
        ) {
            val loaded = it.args[0] as WidgetUserSheetViewModel.ViewState.Loaded
            val user = loaded.user
            if (user == null || user.isBot) return@after
            val binding =
                    WidgetUserSheet.`access$getBinding$p`(it.thisObject as WidgetUserSheet)
            val headerId = Utils.getResId("about_me_header_container", "id")
            val header = binding.a.findViewById<ViewGroup>(headerId)
            val layout = header.parent as LinearLayout
            val userSheet = it.thisObject as WidgetUserSheet

            val tzView = layout.findViewById<TextView>(tzId)
            if (tzView != null) {
                setOnClick(tzView, userSheet, loaded)
                setUserSheetTime(
                        user,
                        tzView
                )
                return@after
            }
            TextView(layout.context, null, 0, R.i.UserProfile_Section_Header).apply {
                id = tzId
                typeface =
                        ResourcesCompat.getFont(Utils.appContext, Constants.Fonts.whitney_semibold)
                val dp = DimenUtils.defaultPadding
                compoundDrawablePadding = DimenUtils.dpToPx(8)
                setUserSheetTime(
                        user,
                        this
                )
                setPadding(dp, dp, dp, dp)
                setCompoundDrawablesRelativeWithIntrinsicBounds(clock, null, null, null)
                layout.addView(this, layout.indexOfChild(header))
                setOnClick(this, userSheet, loaded)
            }
        }

        setMsgHeaderTime()
    }

    private fun getTimezone(id: Long): String? {
        if (usersList.containsKey(id)) return usersList[id] else {
            try {
                val response = JSONObject(Http.simpleGet("$apiUrl/api/user/$id"))
                if (response.has("timezone")) return response.getString("timezone")
            } catch (e: Exception) {
                /* trolley */
            }
        }
        return null
    }

    @SuppressLint("SetTextI18n")
    private fun setUserSheetTime(
            user: User,
            tzView: TextView
    ) {
        tzView.text = "Loading..."

        Utils.threadPool.execute {
            val timezone = getTimezone(user.id)
            if (timezone != null) {
                Utils.mainThread.post {
                    try {
                        tzView.text = formatTimeText(timezone)
                    } catch (e: Exception) {
                        logger.error(e)
                        tzView.text =
                                "An Error Occured,Try deleting timezone for this user from settings"
                    }
                }
            } else {
                tzView.text = "Click to set a timezone"
            }
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun setOnClick(
            tzView: TextView,
            userSheet: WidgetUserSheet,
            loaded: WidgetUserSheetViewModel.ViewState.Loaded
    ) {
        with(tzView) {
            val user = loaded.user
            setOnClickListener {
                SelectDialog().apply {
                    title = "Set timezone (UTC)"
                    items = timezones
                    onResultListener = cringe@{ tz ->
                        addUser(user.id, timezones[tz])
                        setUserSheetTime(
                                user,
                                tzView
                        )
                    }
                    show(userSheet.parentFragmentManager, "timezone_selector")
                }
            }
        }
    }

    private fun addUser(userid: Long, timezone: String) {
        usersList[userid] = timezone
        saveSettings()
    }

    private fun saveSettings() {
        settings.setObject("usersList", usersList)
    }

    @SuppressLint("SetTextI18n")
    private fun setMsgHeaderTime() {
        val timestampField =
                WidgetChatListAdapterItemMessage::class.java.getDeclaredField("itemTimestamp")
                        .apply { isAccessible = true }

        patcher.after<WidgetChatListAdapterItemMessage>(
                "configureItemTag",
                Message::class.java
        ) {
            if (!settings.getBool("timeInHeader", false)) return@after
            val msg = it.args[0] as Message
            Utils.threadPool.execute {
                val timezone = getTimezone(CoreUser(msg.author).id) ?: return@execute
                val timestamp = timestampField[this] as TextView? ?: return@execute
                if (timestamp.text.contains("| LT")) return@execute
                Utils.appActivity.runOnUiThread {
                    timestamp.text = "${timestamp.text} | LT ${calculateTime(timezone)})"
                }
            }
        }
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
    }
}