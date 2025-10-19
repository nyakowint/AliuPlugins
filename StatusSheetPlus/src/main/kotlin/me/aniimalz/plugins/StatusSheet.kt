package me.aniimalz.plugins

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.aliucord.Constants
import com.aliucord.Logger
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.utils.DimenUtils.defaultPadding
import com.aliucord.utils.DimenUtils.dpToPx
import com.aliucord.utils.ReflectUtils
import com.aliucord.widgets.BottomSheet
import com.discord.api.activity.ActivityType
import com.discord.api.presence.ClientStatus
import com.discord.models.domain.emoji.ModelEmojiCustom
import com.discord.models.domain.emoji.ModelEmojiUnicode
import com.discord.models.presence.Presence
import com.discord.stores.StoreStream
import com.discord.stores.StoreStream.getEmojis
import com.discord.utilities.color.ColorCompat
import com.discord.utilities.icon.IconUtils
import com.discord.utilities.images.MGImages
import com.discord.widgets.user.WidgetUserSetCustomStatus
import com.discord.widgets.user.WidgetUserStatusSheetViewModel
import com.discord.widgets.user.profile.UserStatusPresenceCustomView
import com.facebook.drawee.view.SimpleDraweeView
import com.lytefast.flexinput.R
import d0.c0.c
import d0.t.k
import d0.t.u

class StatusSheet(private val logger: Logger) : BottomSheet() {
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        view.setBackgroundColor(ColorCompat.getThemedColor(context, R.b.colorBackgroundPrimary))
        super.onViewCreated(view, bundle)

        val ctx = requireContext()
        val me = StoreStream.getUsers().me

        val pd = dpToPx(6)
        val dp = defaultPadding
        setPadding(dp)

        val clearIcon = ContextCompat.getDrawable(ctx, R.e.ic_close_circle_nova_grey_24dp)?.apply {
            mutate()
            Utils.tintToTheme(this)
        }

        val presence =
            StoreStream.getPresences()::class.java.getDeclaredMethod("getLocalPresence$${Constants.RELEASE_SUFFIX}")
                .invoke(StoreStream.getPresences()) as Presence

        SimpleDraweeView(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(180, 180).apply {
                gravity = Gravity.CENTER
            }
            MGImages.setImage(this, "https://cdn.discordapp.com/avatars/${me.id}/${me.avatar}.png")
            MGImages.setRoundingParams(this, 80f, false, null, null, 0f)
            val p = 6
            setPadding(p, p, p, p)
            background = ShapeDrawable(OvalShape()).apply {
                bounds = Rect(2, 2, 2, 2)
                paint.color = when (presence.status) {
                    ClientStatus.ONLINE -> {
                        Color.parseColor("#3ba55c")
                    }
                    ClientStatus.DND -> {
                        Color.parseColor("#ed4245")
                    }
                    ClientStatus.IDLE -> {
                        Color.parseColor("#faa61a")
                    }
                    ClientStatus.INVISIBLE -> {
                        Color.parseColor("#747f8d")
                    }
                    ClientStatus.OFFLINE -> {
                        Color.parseColor("#747f8d")
                    }
                    null -> {
                        Color.TRANSPARENT
                    }
                }
            }
            addView(this)
        }

        val svm = WidgetUserStatusSheetViewModel()
        val iconSize = LinearLayout.LayoutParams(128, 128)

        addView(LinearLayout(ctx).apply {
            setPadding(0, dp, 0, dpToPx(8))
            ImageView(context).apply {
                gravity = Gravity.CENTER
                layoutParams = iconSize
                setImageDrawable(ContextCompat.getDrawable(ctx, R.e.ic_status_online_16dp))
                setPaddingRelative(0, 0, pd + dpToPx(4), 0)
                setOnClickListener {
                    svm.setStatus(ClientStatus.ONLINE)
                    dismiss()
                }
                setOnLongClickListener {
                    Utils.showToast(getString("status_online"))
                    true
                }
                addView(this)
            }
            ImageView(context).apply {
                gravity = Gravity.CENTER
                layoutParams = iconSize
                setPaddingRelative(pd + dpToPx(2), 0, pd + dpToPx(2), 0)
                setImageDrawable(ContextCompat.getDrawable(ctx, R.e.ic_status_idle_16dp))
                setOnClickListener {
                    svm.setStatus(ClientStatus.IDLE)
                    dismiss()
                }
                setOnLongClickListener {
                    Utils.showToast(getString("status_idle"))
                    true
                }
                addView(this)
            }
            ImageView(context).apply {
                gravity = Gravity.CENTER
                layoutParams = iconSize
                setPaddingRelative(pd + dpToPx(2), 0, pd + dpToPx(2), 0)
                setImageDrawable(ContextCompat.getDrawable(ctx, R.e.ic_status_dnd_16dp))
                setOnClickListener {
                    svm.setStatus(ClientStatus.DND)
                    dismiss()
                }
                setOnLongClickListener {
                    Utils.showToast(getString("status_dnd"))
                    true
                }
                addView(this)
            }
            ImageView(context).apply {
                gravity = Gravity.CENTER
                layoutParams = iconSize
                setPaddingRelative(pd + dpToPx(2), 0, 0, 0)
                setOnClickListener {
                    svm.setStatus(ClientStatus.INVISIBLE)
                    dismiss()
                }
                setImageDrawable(ContextCompat.getDrawable(ctx, R.e.ic_status_invisible_16dp))
                setOnLongClickListener {
                    Utils.showToast(getString("status_invisible"))
                    true
                }
                addView(this)
            }
        })


        //TODO: Placeholder emoji + custom status
        // AccountSwitcher button

        addView(LinearLayout(ctx).apply {
            setOnClickListener {
                (WidgetUserSetCustomStatus.Companion).launch(ctx)
                dismiss()
            }
            val emojiView = SimpleDraweeView(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(64, 64).apply {
                    gravity = Gravity.CENTER_VERTICAL
                }
                val placeholder = getPlaceholderEmoji()
                setImageURI(ModelEmojiUnicode.getImageUri(placeholder.codePoints, ctx))
                addView(this)
            }

            val customStatus = TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
                text = getString("custom_status_set_custom_status")
                setPadding(dp, dp, dp, dp)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 1F
                }
                addView(this)
            }

            val clearStatus = AppCompatImageView(ctx).apply {
                setOnClickListener {
                    svm.clearCustomStatus()
                    customStatus.text = getString("custom_status_set_custom_status")
                    val placeholder = getPlaceholderEmoji()
                    emojiView.apply {
                        visibility = View.VISIBLE
                        setImageURI(
                            ModelEmojiUnicode.getImageUri(
                                placeholder.codePoints,
                                ctx
                            )
                        )
                    }
                    this.visibility = View.GONE
                }
                setImageDrawable(clearIcon)
                visibility = View.GONE
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER_VERTICAL
                }
                addView(this)
            }

            presence.activities.takeIf { !it.isNullOrEmpty() }?.forEach {
                it?.let { activity ->
                    if (activity.p() != ActivityType.CUSTOM_STATUS) return@forEach
                    emojiView.apply {
                        var imageUrl: String? = null
                        if (activity.f() != null) {
                            clearStatus.visibility = View.VISIBLE
                            val emoji = UserStatusPresenceCustomView.Emoji(
                                activity.f().b(),
                                activity.f().c(),
                                activity.f().a()
                            )
                            if (activity.f().b() != null) {
                                imageUrl = ModelEmojiCustom.getImageUri(
                                    emoji.id.toLong(),
                                    emoji.isAnimated,
                                    IconUtils.getMediaProxySize(resources.getDimensionPixelSize(R.d.custom_status_emoji_preview_size))
                                )
                            } else {
                                val emojiUnicode = getEmojis().unicodeEmojiSurrogateMap[emoji.name]
                                if (emojiUnicode != null) {
                                    imageUrl = ModelEmojiUnicode.getImageUri(
                                        emojiUnicode.codePoints,
                                        context
                                    )
                                }
                            }
                        } else {
                            visibility = View.INVISIBLE
                        }
                        imageUrl?.let { img -> MGImages.setImage(this, img) }
                    }
                    if (activity.l() != null) {
                        customStatus.apply {
                            text = activity.l()
                            clearStatus.visibility = View.VISIBLE
                        }
                    } else {
                        customStatus.apply {
                            text = "                    "
                        }
                    }
                }
            }
        })

        if (PluginManager.isPluginEnabled("AccountSwitcher") && PluginManager.plugins.containsKey("AccountSwitcher")) {
            addView(LinearLayout(ctx).apply {
                setOnClickListener {
                    val h = PluginManager.plugins["AccountSwitcher"]

                    // kanged from dps
                    try {
                        val args = h?.settingsTab?.args ?: emptyArray()
                        if (h != null) {
                            ReflectUtils.invokeConstructorWithArgs(h.settingsTab.page, *args).let {
                                Utils.openPageWithProxy(ctx, it)
                            }
                        }
                    } catch (th: Throwable) {
                        logger.errorToast("Failed to launch Account Switcher", th)
                    }
                    dismiss()
                }
                SimpleDraweeView(ctx).apply {
                    layoutParams = LinearLayout.LayoutParams(64, 64).apply {
                        gravity = Gravity.CENTER_VERTICAL
                    }
                    setImageURI("https://i.ibb.co/DDG1xPWb/accountswitch-icon.png")
                    val colorName =
                        if (StoreStream.getUserSettingsSystem().theme == "light")
                            R.c.primary_light_600
                        else
                            R.c.primary_dark_300
                    setColorFilter(ContextCompat.getColor(ctx, colorName))
                    addView(this)
                }

                TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
                    text = "Switch Accounts"
                    setPadding(dp, dp, dp, dp)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        weight = 1F
                    }
                    addView(this)
                }
            })
        }
    }

    private val placeholderEmojis = arrayOf(
        "grinning",
        "grimacing",
        "grin",
        "joy",
        "smiley",
        "smile",
        "sweat_smile",
        "laughing",
        "innocent",
        "wink",
        "blush",
        "slight_smile",
        "upside_down",
        "relaxed",
        "yum",
        "relieved",
        "heart_eyes",
        "kissing_heart",
        "kissing",
        "kissing_smiling_eyes",
        "kissing_closed_eyes",
        "stuck_out_tongue_winking_eye",
        "stuck_out_tongue_closed_eyes",
        "stuck_out_tongue",
        "money_mouth",
        "nerd",
        "sunglasses",
        "hugging",
        "smirk",
        "no_mouth",
        "neutral_face",
        "expressionless",
        "unamused",
        "rolling_eyes",
        "thinking",
        "flushed",
        "disappointed",
        "worried",
        "angry",
        "rage",
        "pensive",
        "confused",
        "slight_frown",
        "frowning2",
        "persevere",
        "confounded",
        "tired_face",
        "weary",
        "triumph",
        "open_mouth",
        "eggplant"
    )

    private fun getString(id: String): String {
        return requireContext().getString(Utils.getResId(id, "string"))
    }

    // literally from decompiled code xd
    private fun getPlaceholderEmoji(): ModelEmojiUnicode {
        val unicodeEmojisNamesMap: Map<String, ModelEmojiUnicode> =
            getEmojis().unicodeEmojisNamesMap
        val strArr = placeholderEmojis
        val aVar = c.k
        val modelEmojiUnicode = unicodeEmojisNamesMap[k.random(strArr, aVar)]
        return modelEmojiUnicode ?: (u.random(
            unicodeEmojisNamesMap.values,
            aVar
        ) as ModelEmojiUnicode)
    }
}