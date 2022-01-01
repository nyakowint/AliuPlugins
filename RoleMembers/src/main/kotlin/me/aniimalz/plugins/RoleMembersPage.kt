package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Constants
import com.aliucord.Http
import com.aliucord.Logger
import com.aliucord.Utils
import com.aliucord.fragments.SettingsPage
import com.aliucord.utils.DimenUtils
import com.aliucord.utils.GsonUtils
import com.aliucord.utils.ReflectUtils
import com.aliucord.utils.RxUtils.subscribe
import com.aliucord.wrappers.GuildRoleWrapper.Companion.color
import com.aliucord.wrappers.GuildRoleWrapper.Companion.icon
import com.aliucord.wrappers.GuildRoleWrapper.Companion.name
import com.discord.api.role.GuildRole
import com.discord.stores.StoreStream
import com.discord.utilities.analytics.AnalyticSuperProperties
import com.discord.utilities.color.ColorCompat
import com.discord.utilities.rest.RestAPI
import com.discord.utilities.rx.ObservableExtensionsKt
import com.facebook.drawee.view.SimpleDraweeView
import com.lytefast.flexinput.R
import rx.Subscription
import java.util.concurrent.TimeUnit

var rmSub: Subscription? = null
val loggerd = Logger("RMP")

class RoleMembersPage(private val role: GuildRole, private val guild: Long) : SettingsPage() {
    private val fetchedRoles = mutableMapOf<Long, MutableList<Long>>() // id of role & list of user ids with that role

    @SuppressLint("SetTextI18n")
    override fun onViewBound(view: View) {
        super.onViewBound(view)

        setActionBarTitle("Role Members")
        setActionBarSubtitle(role.name)
        val ctx = requireContext()

        SimpleDraweeView(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(132, 132).apply {
                gravity = Gravity.CENTER
                setMargins(0, 0, DimenUtils.dpToPx(6), 0)
            }
            clipToOutline = true
            background = ShapeDrawable(OvalShape()).apply { paint.color = Color.TRANSPARENT }
            setImageURI("https://cdn.discordapp.com/role-icons/${role.id}/${role.icon}.png?size=128")
            if (role.icon != null) addView(this)
        }

        if (shouldFetch(role.id)) {
            loggerd.info("Fetching role members...")
            rmSub = ObservableExtensionsKt.computationLatest(
                RestAPI.getApi().getGuildRoleMemberIds(guild, role.id)
            ).subscribe {
                try {
                    this.forEach {
                        if (!StoreStream.getUsers().users.keys.contains(it)) StoreStream.getUsers().fetchUsers(
                            listOf(it))
                    }
                    Utils.appActivity.runOnUiThread { // yskysn zt
                        updateList(ctx, this)
                        fetchedRoles[role.id] = this
                    }
                } catch (t: Throwable) {
                    loggerd.error(t)
                }
            }
        } else {
            loggerd.info("Cache for role is present, updating list")
            fetchedRoles[role.id]?.forEach {
                if (!StoreStream.getUsers().users.keys.contains(it)) StoreStream.getUsers().fetchUsers(
                    listOf(it))
            }
            fetchedRoles[role.id]?.let { Utils.appActivity.runOnUiThread { updateList(ctx, it) } }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun updateList(ctx: Context, userList: MutableList<Long>) {
        try {
            loggerd.info("updating list")
            TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header).apply {
                setTextColor(ColorCompat.getColor(this, R.c.brand_new_330))
                text = "${role.name} • ${userList.size}"
                typeface = ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_semibold)
                gravity = Gravity.CENTER
                addView(this)
            }

            if (userList.isEmpty()) {
                TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header).apply {
                    setTextColor(ColorCompat.getColor(this, R.c.brand_new_330))
                    text = "This role has no members, or they are not cached. If this doesn't seem right, try running the command again."
                    isAllCaps = false
                    typeface = ResourcesCompat.getFont(ctx, Constants.Fonts.whitney_semibold)
                    gravity = Gravity.CENTER
                    addView(this)
                }
                return
            }

            StoreStream.getUsers().getUsers(userList, true).forEach {
                if (it.value == null) return@forEach
                loggerd.info(it.value.username)
                if (StoreStream.getUsers().users[it.key] == null) {
                    StoreStream.getUsers().fetchUsers(listOf(it.key))
                    addView(MemberView(ctx, it.value, guild))
                    return@forEach
                }
                addView(MemberView(ctx, it.value, guild))
            }
        } catch (e: Throwable) {
            loggerd.error(e)
        }
    }

    private fun shouldFetch(roleId: Long): Boolean {
        return !fetchedRoles.keys.contains(roleId)
    }
}