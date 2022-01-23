package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.api.SettingsAPI
import com.aliucord.fragments.SettingsPage
import com.discord.api.activity.Activity
import com.discord.models.user.User
import com.discord.views.CheckedSetting

class UserActivities(private val user: User, private val activities: List<Activity>) : SettingsPage() {
    @SuppressLint("SetTextI18n")
    override fun onViewBound(view: View?) {
        super.onViewBound(view)
        setActionBarTitle("Activities")
        val ctx = requireContext()

        activities.forEach {
            addView(RichPresenceContainer(ctx, it))
        }
    }
}