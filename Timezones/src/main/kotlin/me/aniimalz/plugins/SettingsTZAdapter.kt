package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Logger
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.fragments.SettingsPage
import com.aliucord.utils.RxUtils.subscribe
import com.discord.stores.StoreStream
import com.discord.utilities.rest.RestAPI
import com.discord.widgets.user.usersheet.WidgetUserSheet
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class SettingsTZAdapter(
        private val page: SettingsPage,
        private val usersList: MutableMap<Long, String>
) : RecyclerView.Adapter<SettingsTZAdapter.TZListHolder>() {
    inner class TZListHolder(private val adapter: SettingsTZAdapter, val item: UserTZCard) : RecyclerView.ViewHolder(item), View.OnClickListener {
        override fun onClick(view: View) {
            adapter.onClick()
        }
    }

    private val isFetching = AtomicBoolean(false)

    private val ctx: Context? = page.context
    override fun getItemCount(): Int {
        return ArrayList(usersList.keys).size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TZListHolder {
        return TZListHolder(this, UserTZCard(ctx))
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onBindViewHolder(holder: TZListHolder, position: Int) {
        val name = ArrayList(usersList.keys)[position]
        try {
            val user = StoreStream.getUsers().users[name] ?: run {
                holder.item.name.text = "Loading..."
                if (!isFetching.getAndSet(true)) {
                    Utils.showToast("fetching deez nuts")
                    Utils.threadPool.execute {
                        RestAPI.api.userGet(name).subscribe {
                            StoreStream.`access$getDispatcher$p`(StoreStream.getPresences().stream).schedule {
                                StoreStream.getUsers().handleUserUpdated(this)
                                Utils.mainThread.post {
                                    notifyItemChanged(position)
                                    isFetching.set(false)
                                }
                            }
                        }
                    }
                }
                return
            }
            holder.item.name.text =
                    "${user.username ?: name.toString()} (${usersList.getValue(name)})"
            holder.item.delete.setOnClickListener {
                usersList.remove(name)
                PluginManager.plugins["Timezones"]!!.settings.setObject("usersList", usersList)
                notifyDataSetChanged()
                Utils.showToast("User removed")
            }
            holder.item.image.setImageURI("https://cdn.discordapp.com/avatars/${user.id}/${user.avatar}.png")
            holder.item.image.setOnClickListener {
                WidgetUserSheet.show(
                        user.id,
                        page.parentFragmentManager
                )
            }
            holder.item.image.setOnLongClickListener {
                Utils.setClipboard("User ID", user.id.toString())
                Utils.showToast("Copied ID to clipboard!")
                return@setOnLongClickListener true
            }
        } catch (e: Exception) {
            Logger("Timezones").error("EEEE", e)
        }

    }

    fun onClick() {
    }
}