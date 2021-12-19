package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.fragments.SettingsPage
import com.discord.models.user.User
import com.discord.stores.StoreStream
import com.discord.widgets.user.usersheet.WidgetUserSheet
import java.util.*

class SettingsTZAdapter(
    page: SettingsPage,
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: TZListHolder, position: Int) {
        val name = ArrayList(usersList.keys)[position]
        val user = StoreStream.getUsers().users[name] as User
        holder.item.name.text = user.username ?: name.toString()
        holder.item.delete.setOnClickListener {
            usersList.remove(name)
            PluginManager.plugins["Timezones"]!!.settings.setObject("usersList", usersList)
            notifyDataSetChanged()
            Utils.showToast("User removed")
        }
/*        val icon =
            ContextCompat.getDrawable(ctx!!, getResId("ic_person_white_a60_24dp", "drawable"))!!
                .mutate()
        icon.setTint(ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal))
        holder.item.name.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)*/
        holder.item.image.setImageURI(Uri.parse(user.avatar))
        holder.item.name.setOnLongClickListener {
            WidgetUserSheet.show(name, Utils.appActivity.supportFragmentManager)
            return@setOnLongClickListener false
        }
    }

    fun onClick() {
    }

}