package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.fragments.InputDialog
import com.aliucord.fragments.SettingsPage
import com.aliucord.views.Button
import com.aliucord.views.Divider
import com.aliucord.wrappers.ChannelWrapper.Companion.name
import com.discord.stores.StoreStream
import com.discord.views.CheckedSetting
import com.google.gson.reflect.TypeToken
import java.util.*

class PluginSettings(private val settings: SettingsAPI) : SettingsPage() {
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onViewBound(view: View) {
        super.onViewBound(view)
        val ctx = view.context
        val layout = linearLayout
        setActionBarTitle("Channel Locker")
        val fm = parentFragmentManager

        val channels = settings.getObject(
            "channels",
            HashMap<String, Long>(),
            TypeToken.getParameterized(
                HashMap::class.java,
                String::class.javaObjectType,
                Long::class.javaObjectType
            ).type
        )

        val recycler = RecyclerView(ctx).apply {
            layoutManager = LinearLayoutManager(ctx)
            adapter = ChannelListAdapter(this@PluginSettings, channels)
        }

        addView(Utils.createCheckedSetting(requireContext(), CheckedSetting.ViewType.SWITCH, "Show unlock button", "Whether to show the checkmark or not. If hidden you will have to unlock the channel through the settings").apply {
            isChecked = settings.getBool("showUnlock", true)
            setOnCheckedListener { settings.setBool("showUnlock", it) }
        })

        Button(ctx).run {
            text = "Add Channel"
            setOnClickListener {
                InputDialog().run {
                    setTitle("Add channel")
                    setDescription("Enter the id of the channel you want to lock")
                    setOnCancelListener { dismiss() }
                    setOnOkListener {
                        if (channels.containsValue(input.toLong())) {
                            dismiss()
                            return@setOnOkListener
                        }
                        channels[StoreStream.getChannels().getChannel(input.toLong()).name] =
                            input.toLong()
                        dismiss()
                        Utils.showToast("Channel added: locked")
                        settings.setObject("channels", channels)
                        recycler.adapter?.notifyItemInserted(recycler.adapter!!.itemCount)
                    }
                    show(fm, "fsdkjlbgxjkhdlbfghfjkmugyhkl")
                }
            }
            layout.addView(this)
        }

        layout.addView(Divider(ctx))

        layout.addView(recycler)
    }
}