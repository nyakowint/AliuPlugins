package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Logger
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.fragments.ConfirmDialog
import com.aliucord.fragments.SettingsPage
import com.discord.models.user.User
import com.discord.stores.StoreStream
import com.discord.widgets.user.usersheet.WidgetUserSheet
import java.util.*

class SettingsTZAdapter(
    private val page: SettingsPage,
    private val usersList: MutableMap<Long, String>
) :
    RecyclerView.Adapter<SettingsTZAdapter.TZListHolder>() {
    inner class TZListHolder(private val adapter: SettingsTZAdapter, val item: UserTZCard) :
        RecyclerView.ViewHolder(item), View.OnClickListener {
        override fun onClick(view: View) {
            adapter.onClick()
        }

    }

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
            val user = StoreStream.getUsers().users[name] as User
            holder.item.name.text =
                "${user.username ?: name.toString()} (${usersList.getValue(name)})"
            holder.item.delete.setOnClickListener {
                if (PluginManager.plugins["Timezones"]!!.settings.getBool(
                        "confirmRemoval",
                        false
                    )
                ) {
                    ConfirmDialog().setTitle("Confirm removal").setOnOkListener {
                        usersList.remove(name)
                        PluginManager.plugins["Timezones"]!!.settings.setObject(
                            "usersList",
                            usersList
                        )
                        notifyDataSetChanged()
                        Utils.showToast("User removed")
                    }.show(page.parentFragmentManager, "fat_finger")
                    return@setOnClickListener
                }
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