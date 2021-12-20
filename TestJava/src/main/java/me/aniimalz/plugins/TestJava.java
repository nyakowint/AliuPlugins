package me.aniimalz.plugins;

import android.content.Context;
import android.view.View;

import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.lytefast.flexinput.R;

@AliucordPlugin
class TestJava extends Plugin {

    @Override
    public void start(Context ctx) {
        Utils.showToast("adszfkjnjkn");
    }

    @Override
    public void stop(Context ctx) {
        patcher.unpatchAll();
        commands.unregisterAll();
    }
}