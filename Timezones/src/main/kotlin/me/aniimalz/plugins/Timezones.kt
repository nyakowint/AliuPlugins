package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Constants
import com.aliucord.Http
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.fragments.InputDialog
import com.aliucord.fragments.SelectDialog
import com.aliucord.patcher.Hook
import com.aliucord.utils.DimenUtils
import com.discord.models.user.User
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

    companion object {
        var usersList: HashMap<Long, String> = HashMap<Long, String>()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun start(context: Context) {
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

        patcher.patch(
            WidgetUserSheet::class.java.getDeclaredMethod(
                "configureNote",
                WidgetUserSheetViewModel.ViewState.Loaded::class.java
            ), Hook {
                val loaded = it.args[0] as WidgetUserSheetViewModel.ViewState.Loaded
                val user = loaded.user
                if (user == null || user.isBot) return@Hook
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
                    return@Hook
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
            })
    }

    private fun getTimezone(id: Long): String? {
        if (usersList.containsKey(id)) return usersList[id] else {
            try {
                val response = JSONObject(Http.simpleGet("$apiUrl/api/user/$id"))
                if (response.has("timezone")) return response.getString("timezone")
            } catch (e: Exception) {//OH MY GOD NOOOO AN EXCEPTION I SHOULD IMMEDIALTLY HANDLE IT IN RIGHT WAY
            }
        }
        return null
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUserSheetTime(
        user: User,
        tzView: TextView
    ) {
        val use24Hour = settings.getBool("24hourTime", false)
        tzView.text = "Loading..."

        Utils.threadPool.execute {
            val timezone = getTimezone(user.id)
            if (timezone != null) {
                Utils.mainThread.post {
                    try {
                        tzView.text = formatTimeText(timezone, use24Hour)
                    } catch (e: Exception) {
                        tzView.text =
                            "An Error Occured,Try deleting timezone for this user from settings"
                    }
                }
            } else {
                tzView.text = "Click to set a timezone"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
                        if (timezones[tz].contains("Custom")) {
                            InputDialog().run {
                                title = "Custom Offset"
                                setDescription("Type a custom UTC offset here (e.g +05:50 or -02:10)")
                                setPlaceholderText("Enter UTC Offset")
                                setOnOkListener {
                                    val input = this.input.toString().trim()
                                    if (!input.startsWith("+") && !input.startsWith("-")) Utils.showToast(
                                        "Put - or + to start of input"
                                    ) else {
                                        try {
                                            ZoneOffset.of( input ) //testing if input is valid
                                            addUser(user.id, input)
                                            setUserSheetTime(
                                                user,
                                                tzView
                                            )
                                            dismiss()
                                        } catch (t: Throwable) {
                                            Utils.showToast("An Error Occured,Check if your input is valid")
                                            logger.error(t)
                                        }
                                    }
                                }
                                show(userSheet.parentFragmentManager, "idiot_country_offsets")
                            }
                            return@cringe
                        }
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

    fun addUser(userid: Long, timezone: String) {
        usersList[userid] = timezone
        saveSettings()
    }

    private fun saveSettings() {
        settings.setObject("usersList", usersList)
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}