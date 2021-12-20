package me.aniimalz.plugins

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.fragments.SettingsPage
import com.aliucord.views.Divider
import com.discord.views.CheckedSetting
import com.google.gson.reflect.TypeToken
import java.util.*

class PluginSettings(private val settings: SettingsAPI) : SettingsPage() {
    val stzId = View.generateViewId()
    override fun onViewBound(view: View) {
        super.onViewBound(view)
        val ctx = requireContext()
        setActionBarTitle("Timezones")
        val usersList = settings.getObject(
            "usersList",
            HashMap<Long, String>(),
            TypeToken.getParameterized(
                HashMap::class.java,
                Long::class.javaObjectType,
                String::class.javaObjectType
            ).type
        )

        val recycler = RecyclerView(ctx).apply {
            layoutManager = LinearLayoutManager(ctx)
            adapter = SettingsTZAdapter(this@PluginSettings, usersList)
        }

        addView(
            addSetting(
                ctx,
                "Use 24-hour time",
                "Use 24 hour time instead of AM/PM",
                "24hourTime"
            )
        )
        addView(Divider(ctx))
        addView(
            addSetting(
                ctx,
                "Confirm removal",
                "protection from fat fingering the x",
                "confirmRemoval"
            )
        )
        addView(recycler)

        /*val padding = DimenUtils.defaultPadding

        val filesButton = ToolbarButton(ctx).apply {
            layoutParams = Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.END
                marginEnd = padding
            }
            setImageDrawable(ContextCompat.getDrawable(context, R.e.ic_guild_settings_24dp))
            id = stzId
            setOnClickListener { SelectDialog().apply {
                title = "Import/Export"
                items = arrayOf("Load user list", "Save user list")
                onResultListener = cringe@{ selected ->
                    val userList = settings.getObject(
                        "usersList",
                        HashMap<Long, String>(), // userid, timezone
                        TypeToken.getParameterized(
                            HashMap::class.java,
                            Long::class.javaObjectType,
                            String::class.javaObjectType
                        ).type
                    )
                    if (items[selected] == "Save user list") {

                        dismiss()
                        return@cringe
                    }

                }
                show(parentFragmentManager, "import_export_menu")
            } }
        }*/
    }

    private fun addSetting(
        ctx: Context,
        title: String,
        subtitle: String = "",
        setting: String,
        checked: Boolean = true
    ): CheckedSetting {
        return Utils.createCheckedSetting(ctx, CheckedSetting.ViewType.SWITCH, title, subtitle)
            .apply {
                isChecked = settings.getBool(setting, checked)
                setOnCheckedListener {
                    settings.setBool(setting, it)
                    PluginManager.remountPlugin("Timezones")
                }
            }
    }
}