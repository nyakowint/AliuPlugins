package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.Utils.getResId
import com.aliucord.fragments.SettingsPage
import com.discord.utilities.color.ColorCompat
import com.lytefast.flexinput.R
import java.util.*

class ChannelListAdapter(
    page: SettingsPage,
    private val channels: MutableMap<String, Long>
) :
    RecyclerView.Adapter<ChannelListAdapter.ChannelListHolder>() {
    inner class ChannelListHolder(private val adapter: ChannelListAdapter, val item: ChannelCard) :
        RecyclerView.ViewHolder(item), View.OnClickListener {
        override fun onClick(view: View) {
            adapter.onClick()
        }

    }

    private val ctx: Context? = page.context
    override fun getItemCount(): Int {
        return ArrayList(channels.keys).size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelListHolder {
        return ChannelListHolder(this, ChannelCard(ctx))
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ChannelListHolder, position: Int) {
        val name = ArrayList(channels.keys)[position]
        holder.item.name.text = name
        holder.item.delete.setOnClickListener {
            channels.remove(name)
            PluginManager.plugins["ChannelLocker"]!!.settings.setObject("channels", channels)
            notifyDataSetChanged()
            Utils.showToast("Channel removed: unlocked")
        }
        val icon =
            ContextCompat.getDrawable(ctx!!, getResId("ic_text_channel_white_24dp", "drawable"))!!
                .mutate()
        icon.setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal))
        holder.item.name.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
    }

    fun onClick() {

    }

}