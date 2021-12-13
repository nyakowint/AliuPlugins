package me.aniimalz.plugins

import android.view.View
import com.aliucord.Utils

class Fart : View.OnClickListener  {
    override fun onClick(view: View) {
        Utils.showToast("Farting")
    }
}