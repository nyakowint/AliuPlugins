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


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun start(ctx: Context) {
        val clock = ContextCompat.getDrawable(ctx, R.e.ic_archived_clock_dark)?.apply {
            mutate()
        }

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
                        settings.getBool("24hourTime", false),
                        tzView
                    )
                    return@Hook
                }
                TextView(layout.context, null, 0, R.i.UserProfile_Section_Header).apply {
                    id = tzId
                    typeface =
                        ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_semibold)
                    val dp = DimenUtils.defaultPadding
                    compoundDrawablePadding = DimenUtils.dpToPx(8)
                    text = if (user.timezone != null) setUserSheetTime(
                        user,
                        settings.getBool("24hourTime", false),
                        this
                    ) else "Click to set a timezone"
                    setPadding(dp, dp, dp, dp)
                    setCompoundDrawablesRelativeWithIntrinsicBounds(clock, null, null, null)
                    layout.addView(this, layout.indexOfChild(header))
                    setOnClick(this, userSheet, loaded)
                }

            })
        val u24hr = settings.getBool("24hourTime", false)

        /*// make it show next to timestamp on message - this didnt work idfk lmao
        with(WidgetChatListAdapterItemMessage::class.java) {
            val timestampField = getDeclaredField(
                "itemTimestamp"
            ).apply { isAccessible = true }
            patcher.patch(
                getDeclaredMethod(
                    "configureItemTag",
                    Message::class.java
                ), Hook { cf: XC_MethodHook.MethodHookParam ->
                    try {
                        val msg = cf.args[0] as Message
                        val user = CoreUser(msg.author)
                        val timestamp = timestampField.get(cf.thisObject) as TextView
                        if (user.timezone != null && settings.getBool("showOnMessages", false)) {
                            timestamp.text =
                                "${timestamp.text} (T: ${calculateTime(user.timezone, u24hr)})"
                        } //.
                    } catch (t: Throwable) {
                        logger.error(t)
                    }
                })
        }*/
    }

    private val User.timezone
        get() = settings.getObject(
            "usersList", HashMap<Long, String>(), // userid, timezone
            TypeToken.getParameterized(
                HashMap::class.java,
                Long::class.javaObjectType,
                String::class.javaObjectType
            ).type
        )[id]

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUserSheetTime(
        user: User,
        use24Hour: Boolean,
        tzView: TextView
    ): CharSequence {
        val list = settings.getObject(
            "usersList",
            HashMap<Long, String>(), // userid, timezone
            TypeToken.getParameterized(
                HashMap::class.java,
                Long::class.javaObjectType,
                String::class.javaObjectType
            ).type
        )
        if (list.containsKey(user.id)) {
            tzView.text = "${calculateTime(user.timezone, use24Hour)} (UTC${user.timezone})"
            return "${calculateTime(user.timezone, use24Hour)} (UTC${user.timezone})"
        }
        return "No timezone set"
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
                        val userList = settings.getObject(
                            "usersList",
                            HashMap<Long, String>(), // userid, timezone
                            TypeToken.getParameterized(
                                HashMap::class.java,
                                Long::class.javaObjectType,
                                String::class.javaObjectType
                            ).type
                        )
                        if (timezones[tz].contains("Custom")) {
                            InputDialog().apply {
                                title = "Custom Offset"
                                setDescription("Type a custom UTC offset here (e.g +05:50 or -02:10)")
                                setOnOkListener {
                                    try {
                                        userList[user.id] = text as String
                                        settings.setObject("usersList", userList)
                                        setUserSheetTime(
                                            user,
                                            settings.getBool("24hourTime", false),
                                            tzView
                                        )
                                    } catch (t: Throwable) {
                                        logger.error(t)
                                    }
                                }
                                show(userSheet.parentFragmentManager, "idiot_country_offsets")
                            }
                            return@cringe
                        }
                        userList[user.id] = timezones[tz]
                        settings.setObject("usersList", userList)
                        setUserSheetTime(
                            user,
                            settings.getBool("24hourTime", false),
                            tzView
                        )
                    }
                    show(userSheet.parentFragmentManager, "timezone_selector")
                }
            }
        }
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }
}